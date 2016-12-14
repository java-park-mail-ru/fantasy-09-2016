package ru.mail.park.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.game.Game;

import java.util.ArrayList;
import java.util.List;

public class InitQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitQueue.class);
    private static List<NetUser> queue = new ArrayList<>();

    public static synchronized void addConnection(WebSocketSession webSocketSession) {
        queue.add(new NetUser(webSocketSession));
        if (queue.size() > 1) {
            final NetUser u1 = queue.remove(queue.size() - 1);
            final NetUser u2 = queue.remove(queue.size() - 1);
            LOGGER.info(String.format("New game:\n\tUser1 - %s\n\tUser2 - %s", u1.getLogin(), u2.getLogin()));
            Game.createGame(u1, u2);
        }
    }
}
