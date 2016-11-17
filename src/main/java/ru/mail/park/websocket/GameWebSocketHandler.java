package ru.mail.park.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<WebSocketSession>());

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        for (String key : session.getAttributes().keySet()) {
            System.out.format("%s: %s\n", key, session.getAttributes().get(key));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        switch (message.getPayload()) {
            case "me":
                session.sendMessage(new TextMessage(session.getAttributes().get("login").toString()));
                break;
            default:
                System.out.println(message.getPayload());
                break;
        }
    }
}
