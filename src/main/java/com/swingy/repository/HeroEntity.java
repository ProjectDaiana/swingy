package com.swingy.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "heroes")
public class HeroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private int level;
    private int xp;
    private int attack;
    private int defense;
    private int hitPoints;
    private int weaponValue;
    private int armorValue;
    private int helmValue;

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = hitPoints; }

    public int getWeaponValue() { return weaponValue; }
    public void setWeaponValue(int weaponValue) { this.weaponValue = weaponValue; }

    public int getArmorValue() { return armorValue; }
    public void setArmorValue(int armorValue) { this.armorValue = armorValue; }

    public int getHelmValue() { return helmValue; }
    public void setHelmValue(int helmValue) { this.helmValue = helmValue; }
}
