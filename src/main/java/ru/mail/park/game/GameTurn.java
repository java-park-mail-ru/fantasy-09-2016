package ru.mail.park.game;

import java.util.List;

public class GameTurn {
    private int id;
    private List<Unit> units;
    private Action action;
    private Timeline timeline;

    public GameTurn(int id, List<Unit> units, Action action, Timeline timeline) {
        this.id = id;
        this.units = units;
        this.action = action;
        this.timeline = timeline;
    }
}
