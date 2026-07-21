package com.swingy.model;

public class Helm extends Artifact {

    public Helm(int value) { super(value); }

    @Override public ArtifactType getType() { return ArtifactType.HELM; }
    @Override public void applyTo(Hero hero)    { hero.setHitPoints(hero.getHitPoints() + value); }
    @Override public void removeFrom(Hero hero) { hero.setHitPoints(hero.getHitPoints() - value); }
}
