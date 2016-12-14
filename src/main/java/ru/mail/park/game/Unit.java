package ru.mail.park.game;

import ru.mail.park.utility.Utility;

public class Unit {
    private int x;
    private int y;
    private UnitType type;
    private int health;
    private int userId;
    private int id;

    public Unit(int x, int y, UnitType type, int userId, int id) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.health = type.startHealth;
        this.userId = userId;

    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void attack(Unit u) {
        u.health -= type.attackPower + type.attackScatter * (2 * Utility.RANDOM.nextDouble() - 1);
    }

    public void die() {
        health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

  /*  private static double dist(Unit u1, Unit u2) {
        return dist(u1.x, u1.y, u2.x, u2.y);
    }

    private static double dist(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }*/
}
