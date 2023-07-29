package tech.chowyijiu.huhu_bot.utils.xiaoai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import tech.chowyijiu.huhu_bot.config.XiaoAiConfig;
import tech.chowyijiu.huhu_bot.utils.StringUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @author elastic chow
 * @date 23/7/2023
 */
@SuppressWarnings("all")
@Slf4j
public class XiaoAIUtil {

    //public static void main(String[] args) throws Exception {

    // 登录
    // 请将返回的deviceId、userId、serviceToken、securityToken填入上面静态变量中
    // 登录一次即可，如果token失效再去登录
    //System.out.println(LoginService.login("xiaomiio", "19216801373", "2023flless"));

    // 参数说明
    // did: 设备ID
    // siid: 功能分类ID
    // piid: 设备属性ID
    // aiid: 设备方法ID

    // 米家产品库
    // https://home.miot-spec.com/


    // 获取全部设备列表
    // 返回结果说明
    // name: 设备名称
    // did: 设备ID
    // isOnline: 设备是否在线
    // model: 设备产品型号, 根据这个去米家产品库查该产品相关的信息
    //post("/home/device_list", "{\"getVirtualModel\": false, \"getHuamiDevices\": 0}");

    // 获取设备属性(例如获取风扇的开关机状态和风速)
    //post("/miotspec/prop/get", "{\"params\":[{\"did\":\"111111111\",\"siid\":2,\"piid\":1},{\"did\":\"111111111\",\"siid\":2,\"piid\":6}]}");

    // 设置设备属性(例如风扇开机并设置风速为70)
    //post("/miotspec/prop/set", "{\"params\":[{\"did\":\"111111111\",\"siid\":2,\"piid\":1,\"value\":true},{\"did\":\"111111111\",\"siid\":2,\"piid\":6,\"value\":70}]}");

    // 调用设备方法(例如让小爱音箱朗读指定文本)
    // in: 入参
    // 例如小爱音箱pro的执行指令方法参数为
    // 1 - text-content 指令文本
    // 2 - silent-execution 是否静默执行
    //post("/miotspec/action", "{\"params\":{\"did\":\"111111111\",\"siid\":5,\"aiid\":5,\"in\":[\"开灯\", true]}}");
    //post("/miotspec/action", "{\"params\":{\"did\":\"700938373\",\"siid\":7,\"aiid\":3,\"in\":[\"阿里云盘签到成功\"]}}");

    // 获取房间列表
    //post("/v2/homeroom/gethome", "{\"fg\":false,\"fetch_share\":true,\"fetch_share_dev\":true,\"limit\":300,\"app_ver\":7}");

    // 获取设备耗材(home_id可从上面的获取房间列表接口得知, owner_id即userId)
    //post("/v2/home/standard_consumable_items", "{\"home_id\":111111111,\"owner_id\":111111111}");

    // 获取红外遥控器的按键列表
    //post("/v2/irdevice/controller/keys", "{\"did\":\"ir.111111111\"}");

    // 触发红外遥控器按键
    //post("/v2/irdevice/controller/key/click", "{\"did\": \"ir.111111111\", \"key_id\": 100000001}");

    // 获取场景列表(包含手动场景和自动化)
    //post("/appgateway/miot/appsceneservice/AppSceneService/GetSceneList", "{\"home_id\":\"111111111\"}");

    // 执行手动场景
    //post("/appgateway/miot/appsceneservice/AppSceneService/RunScene", "{\"scene_id\":\"111111111\",\"trigger_key\":\"user.click\"}");

    //}

    public static void tts(String content) {
        Data data = new Data(ActionParams.builder().did("700938373").siid(7).aiid(3).in(List.of(content)).build());
        XiaoAIUtil.post("/miotspec/action", data.toString());
    }



    // 调用接口
    public static void post(String uri, String data) {
        if (!StringUtil.hasLength(XiaoAiConfig.serviceToken)) {
            log.info("serviceToken is null");
            return;
        }
        try {
            String nonce = generateNonce();
            String signedNonce = generateSignedNonce(XiaoAiConfig.securityToken, nonce);
            String signature = generateSignature(uri, signedNonce, nonce, data);

            Map<String, Object> bodyMap = Map.of("_nonce", nonce, "data", data, "signature", signature);
            HttpResponse response = HttpRequest.post("https://api.io.mi.com/app" + uri)
                    .header("User-Agent", "APP/com.xiaomi.mihome APPV/6.0.103 iosPassportSDK/3.9.0 iOS/14.4 miHSTS")
                    .header("x-xiaomi-protocal-flag-cli", "PROTOCAL-HTTP2")
                    .header("Cookie", "PassportDeviceId=" + XiaoAiConfig.deviceId + ";userId="
                            + XiaoAiConfig.userId + ";serviceToken=" + XiaoAiConfig.serviceToken + ";")
                    .form(bodyMap)
                    .execute();
            log.info("post completed, response:{}", response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateNonce() {
        return random(16);
    }

    private static String generateSignedNonce(String secret, String nonce) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(Base64.getDecoder().decode(secret));
        messageDigest.update(Base64.getDecoder().decode(nonce));
        return Base64.getEncoder().encodeToString(messageDigest.digest());
    }

    private static String generateSignature(String url, String signedNonce, String nonce, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        String sign = url + "&" + signedNonce + "&" + nonce + "&data=" + data;
        hmac.init(new SecretKeySpec(Base64.getDecoder().decode(signedNonce), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(hmac.doFinal(sign.getBytes(StandardCharsets.UTF_8)));
    }

    private static String random(int count) {
        String str = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder rnd = new StringBuilder();
        for (int i = 0; i < count; i++) {
            rnd.append(str.charAt((int) Math.floor(Math.random() * str.length())));
        }
        return rnd.toString();
    }
}