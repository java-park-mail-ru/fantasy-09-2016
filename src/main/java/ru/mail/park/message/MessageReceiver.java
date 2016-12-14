package ru.mail.park.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mail.park.game.Action;
import ru.mail.park.game.InitPosition;
import ru.mail.park.websocket.NetUser;

import java.io.IOException;

public class MessageReceiver {

    private static ObjectMapper mapper = new ObjectMapper();

    public static InitPosition receiveInitPositionMessage(NetUser user) throws IOException {
        return new ObjectMapper().readValue(user.receive(), InitPosition.class);
    }

    public static Action receiveActionMessage(NetUser user) throws IOException {
        return new ObjectMapper().readValue(user.receive(), Action.class);
    }
}
