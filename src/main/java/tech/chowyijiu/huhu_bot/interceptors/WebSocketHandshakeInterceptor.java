package tech.chowyijiu.huhu_bot.interceptors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author elastic chow
 * @date 14/5/2023
 */
@Slf4j
public class WebSocketHandshakeInterceptor /*implements HandshakeInterceptor*/ {

    //@Override
    //public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    //    log.info("收到握手请求 {}:{}",request.getRemoteAddress().getHostString(),request.getRemoteAddress().getPort());
    //    if(!checkConnections()){
    //        return false;
    //    }
    //    return true;
    //}
    //
    //@Override
    //public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    //    if(!checkAuthorization(request)){
    //        doForbidden(request,response);
    //    }
    //}
    //
    //private void doForbidden(ServerHttpRequest request,ServerHttpResponse response){
    //    response.setStatusCode(HttpStatus.FORBIDDEN);
    //    log.error("无token或token错误，禁止握手！ {}:{}",request.getRemoteAddress().getHostString(),request.getRemoteAddress().getPort());
    //}
    //
    ///**
    // * 检查当前连接数是否达到上限
    // * @return
    // */
    //private boolean checkConnections(){
    //    if(BotConfig.MAX_CONNECTIONS < 0){
    //        return true;
    //    }
    //    int connections = Server.getConnections();
    //    if(connections >= BotConfig.MAX_CONNECTIONS){
    //        log.info("当前连接数:{},已达到最大连接数:{},本次禁止握手",connections,BotConfig.MAX_CONNECTIONS);
    //        return false;
    //    }
    //    return true;
    //}
    //
    ///**
    // * 检查token
    // * @param request
    // * @return
    // */
    //private boolean checkAuthorization(ServerHttpRequest request){
    //    if (Strings.isNotBlank(BotConfig.ACCESS_TOKEN)) {
    //        HttpHeaders headers = request.getHeaders();
    //        List<String> authorization = headers.get("Authorization");
    //        if (CollectionUtils.isEmpty(authorization)) {
    //            return false;
    //        }
    //        boolean hasToken = false;
    //        for (String s : authorization) {
    //            if(s.contains("Token " + BotConfig.ACCESS_TOKEN)){
    //                hasToken = true;
    //                break;
    //            }
    //        }
    //        return hasToken;
    //    }
    //    return true;
    //}
}