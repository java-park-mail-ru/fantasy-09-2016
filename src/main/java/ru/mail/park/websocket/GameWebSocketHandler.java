package ru.mail.park.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(GameWebSocketHandler.class);

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
                LOGGER.warn("WS session clearer is interrupted", e);
            }
        }, "WS session clearer").start();
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
        LOGGER.info(String.format("New WS connection %s", session.getRemoteAddress()));
        final String login = (String) session.getAttributes().get("login");
        if (!StringUtils.isEmpty(login)) {
            SESSIONS.add(session);
            InitQueue.addConnection(session);
            LOGGER.info(String.format("Login success : %s", login));
        } else {
            LOGGER.warn("Unauthorized access");
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LOGGER.info(String.format("New message\n%s", message.getPayload()));
        if (LISTENERS.containsKey(session)) {
            LISTENERS.get(session).accept(message.getPayload());
        }
    }
}
