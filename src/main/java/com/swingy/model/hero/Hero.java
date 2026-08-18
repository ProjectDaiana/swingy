package com.swingy.model.hero;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.EnumMap;
import java.util.Map;

import com.swingy.model.artifact.Artifact;
import com.swingy.model.artifact.ArtifactType;

public class Hero {

    @NotBlank(message = "Name may not be blank")
    @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
    private String name;

    @NotNull(message = "Hero class must be selected")
    private HeroClass heroClass;

    @Min(value = 1, message = "Level must be at least 1")
    private int level;

    @Min(value = 0, message = "XP cannot be negative")
    private int xp;

    @Min(value = 0, message = "Attack cannot be negative")
    private int attack;

    @Min(value = 0, message = "Defense cannot be negative")
    private int defense;

    @Min(value = 1, message = "Hit points must be at least 1")
    private int hitPoints;

    private final Map<ArtifactType, Artifact> equipped = new EnumMap<>(ArtifactType.class);

    public Hero(String name, HeroClass heroClass) {
        this.name = name;
        this.heroClass = heroClass;
        this.level = 1;
        this.xp = 0;
        this.attack = heroClass.getBaseAttack();
        this.defense = heroClass.getBaseDefense();
        this.hitPoints = heroClass.getBaseHitPoints();
    }

    public Hero(String name, HeroClass heroClass, int level, int xp, int attack, int defense, int hitPoints) {
        this.name = name;
        this.heroClass = heroClass;
        this.level = level;
        this.xp = xp;
        this.attack = attack;
        this.defense = defense;
        this.hitPoints = hitPoints;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
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

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public void dies() {
        this.hitPoints = 0;
    }

    int xpToNextLevel() {
        return level * 1000 + (level - 1) * (level - 1) * 450;
    }

    public void levelUp() {
        level++;
        attack += heroClass.getAttackGrowth();
        defense += heroClass.getDefenseGrowth();
        hitPoints += heroClass.getHitPointsGrowth();
    }

    public void gainXp(int amount) {
        xp += amount;
        while (xp >= xpToNextLevel()) {
            xp -= xpToNextLevel();
            levelUp();
        }
    }

    public void equipArtifact(Artifact artifact) {
        Artifact current = equipped.get(artifact.getType());
        if (current != null)
            current.removeFrom(this);
        artifact.applyTo(this);
        equipped.put(artifact.getType(), artifact);
    }

    public void loadArtifact(Artifact artifact) {
        equipped.put(artifact.getType(), artifact);
    }

    public Artifact getEquipped(ArtifactType type) {
        return equipped.get(type);
    }

    public void takeDamage(int damage) {
        int effectiveDamage = Math.max(0, damage - defense);
        hitPoints -= effectiveDamage;
        if (hitPoints < 0) {
            hitPoints = 0;
        }
    }

    public HeroClass getHeroClass() {
        return heroClass;
    }
}