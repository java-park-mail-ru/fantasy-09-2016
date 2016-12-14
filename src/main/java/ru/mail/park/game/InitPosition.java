package ru.mail.park.game;

public class InitPosition {

    private Unit[] units;

    public Unit[] getUnits() {
        return units;
    }

    public static final class Unit {
        public String type;
        public int[] position;
        public int id;
    }
}
