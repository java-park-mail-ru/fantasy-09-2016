package ru.mail.park.game;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mail.park.message.MessageReceiver;
import ru.mail.park.websocket.NetUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Game {

    private static List<Game> games = Collections.synchronizedList(new ArrayList<>());

    private NetUser[] users = new NetUser[2];
    private List<Unit> units = Collections.synchronizedList(new ArrayList<>());
    private Timeline timeline = new Timeline();

    private Game(NetUser u1, NetUser u2) {
        users[0] = u1;
        users[1] = u2;
    }

    public static void createGame(NetUser u1, NetUser u2) {
        final Game room = new Game(u1, u2);
        new Thread() {
            @Override
            public void run() {
                room.start();
            }
        }.start();
    }

    private void start() {
        final InitPosition ipm1;
        final InitPosition ipm2;
        try {
            ipm1 = MessageReceiver.receiveInitPositionMessage(users[0]);
            ipm2 = MessageReceiver.receiveInitPositionMessage(users[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return; // TODO error
        }
        for (InitPosition.Unit u : ipm1.getUnits()) {
            units.add(new Unit(u.position[0], u.position[1], UnitType.parse(u.type), 1, u.id));
        }
        for (InitPosition.Unit u : ipm2.getUnits()) {
            units.add(new Unit(u.position[0], u.position[1], UnitType.parse(u.type), 2, u.id));
        }

        final List<Unit> order = new ArrayList<>(units);
        Collections.shuffle(order);
        timeline.pushFragments(order);
        sendGameTurn(new GameTurn(0, units, null, timeline));
        for (int id = 1; ; id++) {
            final Timeline.TimelineFragment fragment = timeline.popFragment();
            timeline.pushFragment(fragment.getUserId(), fragment.getUnitId());
            final NetUser next = users[fragment.getUserId() + 1];
            final Action action;
            try {
                action = MessageReceiver.receiveActionMessage(next);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            processAction(action, fragment);
            sendGameTurn(new GameTurn(id, units, action, timeline));
            if (checkEnd()) break;
        }
    }

    private void processAction(Action action, Timeline.TimelineFragment fragment) {
        final Unit unit = units.stream()
                .filter(u -> u.getUserId() == fragment.getUserId() && u.getId() == fragment.getId())
                .findFirst().get();
        switch (action.getType()) {
            case MOVE:
                unit.move(action.getMoveTo().get(0), action.getMoveTo().get(1));
                break;
            case ATTACK:
                final Unit enemy = units.stream()
                        .filter(u -> u.getX() == action.getActionTo().get(0) && u.getY() == action.getActionTo().get(1))
                        .findFirst().get();
                unit.attack(enemy);
                break;
            default:
                break;
        }
    }

    private boolean checkEnd() {
        return units.stream().filter(u -> u.getUserId() == 0).count() == 0
                || units.stream().filter(u -> u.getUserId() == 1).count() == 0;
    }

    public void sendGameTurn(GameTurn gameTurn) {
        String message = null;
        try {
            message = new ObjectMapper().writeValueAsString(gameTurn);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        users[0].send(message);
        users[1].send(message);
    }
}
