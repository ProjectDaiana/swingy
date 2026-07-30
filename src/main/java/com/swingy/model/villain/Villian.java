package com.swingy.model.villain;

public class Villian {
    private int level;
    private int attack;
    private int defense;
    private int hitPoints;

    public Villian(int level, int attack, int defense, int hitPoints) {
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.hitPoints = hitPoints;
    }

    public int getLevel()    { return level; }
    public int getAttack()   { return attack; }
    public int getDefense()  { return defense; }
    public int getHitPoints(){ return hitPoints; }
}
