package com.swingy.model;

public class Weapon extends Artifact {

    public Weapon(int value) { super(value); }

    @Override public ArtifactType getType() { return ArtifactType.WEAPON; }
    @Override public void applyTo(Hero hero)    { hero.setAttack(hero.getAttack() + value); }
    @Override public void removeFrom(Hero hero) { hero.setAttack(hero.getAttack() - value); }
}
