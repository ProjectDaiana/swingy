package com.swingy.model.hero;

public enum HeroType {
    WARRIOR(10, 5, 50, 3, 2, 15),
    WIZARD(8, 3, 35, 4, 1, 10),
    MESSI(9, 4, 40, 3, 2, 12);

    private final int baseAttack;
    private final int baseDefense;
    private final int baseHitPoints;
    private final int attackGrowth;
    private final int defenseGrowth;
    private final int hitPointsGrowth;

    HeroType(int baseAttack, int baseDefense, int baseHitPoints,
            int attackGrowth, int defenseGrowth, int hitPointsGrowth) {
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseHitPoints = baseHitPoints;
        this.attackGrowth = attackGrowth;
        this.defenseGrowth = defenseGrowth;
        this.hitPointsGrowth = hitPointsGrowth;
    }

    public int getBaseAttack() {
        return baseAttack;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getBaseHitPoints() {
        return baseHitPoints;
    }

    public int getAttackGrowth() {
        return attackGrowth;
    }

    public int getDefenseGrowth() {
        return defenseGrowth;
    }

    public int getHitPointsGrowth() {
        return hitPointsGrowth;
    }
}
