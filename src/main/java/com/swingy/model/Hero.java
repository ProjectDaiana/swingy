package com.swingy.model;

public class Hero {
    private String name;
    private HeroClass heroClass;
    private int level;
    private int xp;
    private int attack;
    private int defense;
    private int hitPoints;

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience(){
        return xp;
    }

    public int getAttack() {
        return attack;
    }
    
    public int getDefense() {
        return defense;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public void setLevel(int level) {
    //     this.level = level;
    // }

    // public void setExperience(int experience) {
    //     this.experience = experience;
    // }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    int xpToNextLevel() {
        return level * 1000 + (level - 1) * (level - 1) * 450;
    }

    void levelUp() {
        level++;
        attack    += heroClass.getAttackGrowth();
        defense   += heroClass.getDefenseGrowth();
        hitPoints += heroClass.getHitPointsGrowth();
    }

    void gainExperience(int amount) {
        xp += amount;
        while (xp >= xpToNextLevel()) {
            xp -= xpToNextLevel();
            levelUp();
        }
    }
    
}