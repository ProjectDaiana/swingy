package com.swingy.model.artifact;

import com.swingy.model.hero.Hero;

import jakarta.validation.constraints.Min;

public abstract class Artifact {

    @Min(1)
    protected final int value;

    public Artifact(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    public abstract ArtifactType getType();
    public abstract void applyTo(Hero hero);
    public abstract void removeFrom(Hero hero);
}
