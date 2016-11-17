package ru.mail.park.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final GameWebSocketHandler self = new GameWebSocketHandler();
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<WebSocketSession>());

    public static GameWebSocketHandler instance() {
        return self;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.add(session);
        for (String key : session.getAttributes().keySet()) {
            System.out.format("%s: %s\n", key, (String) session.getAttributes().get(key));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        System.out.println(message.getPayload());
        Map<String, Object> m = session.getAttributes();
        for (String key : m.keySet()) {
            System.out.format("%s: %s\n", key, (String) m.get(key));
        }
        System.out.println(session.getAttributes().get("login"));
    }
}
