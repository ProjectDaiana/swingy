package com.swingy.model.hero;

public class HeroBuilder {
    private final String name;
    private final HeroType heroType;
    private int level = 1;
    private int xp = 0;
    private int attack;
    private int defense;
    private int hitPoints;

    public HeroBuilder(String name, HeroType heroType) {
        this.name = name;
        this.heroType = heroType;
        this.attack = heroType.getBaseAttack();
        this.defense = heroType.getBaseDefense();
        this.hitPoints = heroType.getBaseHitPoints();
    }

    public HeroBuilder level(int level) {
        this.level = level;
        return this;
    }

    public HeroBuilder xp(int xp) {
        this.xp = xp;
        return this;
    }

    public HeroBuilder attack(int attack) {
        this.attack = attack;
        return this;
    }

    public HeroBuilder defense(int defense) {
        this.defense = defense;
        return this;
    }

    public HeroBuilder hitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
        return this;
    }

    public Hero build() {
        return new Hero(name, heroType, level, xp, attack, defense, hitPoints);
    }
}