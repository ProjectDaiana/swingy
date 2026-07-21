package com.swingy.model;

public class Armor extends Artifact {

    public Armor(int value) { super(value); }

    @Override public ArtifactType getType() { return ArtifactType.ARMOR; }
    @Override public void applyTo(Hero hero)    { hero.setDefense(hero.getDefense() + value); }
    @Override public void removeFrom(Hero hero) { hero.setDefense(hero.getDefense() - value); }
}
