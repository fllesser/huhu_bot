//package tech.chowyijiu.huhubot.core.ws;
//
///**
// * @author elastic chow
// * @date 30/6/2023
// */
//@Slf4j
//@Getter
//@ToString
//@SuppressWarnings("unused")
//@RequiredArgsConstructor
//public class BotV2 {
//
//    private final Long userId;
//    private final WebSocketSession session;
//
//    /**
//     * call api 最终调用的方法
//     * Send a WebSocket message
//     *
//     * @param text text
//     */
//    public void sessionSend(String text) {
//        try {
//            this.session.sendMessage(new TextMessage(text));
//        } catch (IOException e) {
//            log.info("{}sessionSend error, session[{}], message[{}], exception[{}]{}",
//                    ANSI.YELLOW, this.session.getId(), text, e.getMessage(), ANSI.RESET);
//        }
//    }
//
//    private static final Map<Integer, String> ECHO_DATA_MAP = new HashMap<>();
//    private static volatile int echo = -128;
//    private static final long timeout = 10000L;
//
//    private synchronized static Integer operaAtomicEcho() {
//        if (echo == 127) echo = -128; //???这段原子性怎么保证
//        return echo++;
//    }
//
//    public static void fillData(int echo, String data) {
//        Integer echoInteger = echo;
//        synchronized (echoInteger) {
//            ECHO_DATA_MAP.put(echo, data);
//            echoInteger.notify();
//        }
//    }
//
//    @SuppressWarnings("all")
//    private String callApi(GocqAction action, Map<String, Object> paramsMap) throws InterruptedException {
//        RequestBoxV2 requestBox = new RequestBoxV2();
//        requestBox.setAction(action.name());
//        Optional.ofNullable(paramsMap).ifPresent(requestBox::setParams);
//        if (action.isHasResp()) {
//            Integer echo = operaAtomicEcho();
//            requestBox.setEcho(echo);
//            //因为可能存在当前线程还没wait, 其他线程就抢先获得了锁的情况, 所以先获取锁, 再发送ws请求
//            synchronized (echo) {
//                this.sessionSend(JSONObject.toJSONString(requestBox));
//                echo.wait(timeout);
//            }
//            return ECHO_DATA_MAP.get(echo);
//        } else {
//            this.sessionSend(JSONObject.toJSONString(requestBox));
//            return "";
//        }
//    }
//
//}
