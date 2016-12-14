package ru.mail.park.game;


public enum UnitType {
    WARRIOR(4, 1, 400, 20, 2),
    ARCHER(3, 4, 250, 10, 5),
    WIZARD(3, 3, 200, 5, 10);

    public final int moveRadius;
    public final int attackRadius;
    public final int startHealth;
    public final int attackPower;
    public final int attackScatter;

    UnitType(int moveRadius, int attackRadius, int startHealth, int attackPower, int attackScatter) {
        this.moveRadius = moveRadius;
        this.attackRadius = attackRadius;
        this.startHealth = startHealth;
        this.attackPower = attackPower;
        this.attackScatter = attackScatter;
    }

    public static UnitType parse(String type) {
        switch (type) {
            case "warrior":
                return WARRIOR;
            case "archer":
                return ARCHER;
            case "wizard":
                return WIZARD;
            default:
                return null;
        }
    }
}
