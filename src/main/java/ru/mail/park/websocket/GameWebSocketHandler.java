package ru.mail.park.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> SESSIONS = Collections.synchronizedSet(new HashSet<WebSocketSession>());
    private static final Map<WebSocketSession, Consumer<String>> LISTENERS = Collections.synchronizedMap(new HashMap<>());

    static {
        new Thread(() -> {
            try {
                while (true) {
                    TimeUnit.MINUTES.sleep(5);
                    for (WebSocketSession wss : SESSIONS) {
                        if (!wss.isOpen()) {
                            SESSIONS.remove(wss);
                            if (LISTENERS.containsKey(wss)) {
                                LISTENERS.remove(wss);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void addHandleTextMessageListener(Consumer<String> listener, WebSocketSession webSocketSession) {
        LISTENERS.put(webSocketSession, listener);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        SESSIONS.remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        final String login = (String) session.getAttributes().get("login");
        //if (!StringUtils.isEmpty(login)) {
        SESSIONS.add(session);
        InitQueue.addConnection(session);
        //} else {
        //    session.close();
        //}
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (LISTENERS.containsKey(session)) {
            LISTENERS.get(session).accept(message.getPayload());
        }
    }
}
