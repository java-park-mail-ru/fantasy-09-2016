package ru.mail.park.game;

import java.util.List;

public class Action {
    private ActionType type;
    private List<Integer> moveTo;
    private List<Integer> actionTo;

    public ActionType getType() {
        return type;
    }

    public List<Integer> getMoveTo() {
        return moveTo;
    }

    public List<Integer> getActionTo() {
        return actionTo;
    }

    public enum ActionType {
        MOVE, ATTACK, HEAL
    }
}
