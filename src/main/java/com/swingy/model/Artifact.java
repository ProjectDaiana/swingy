package com.swingy.model;

public class Artifact {
    private ArtifactType type; // enum: WEAPON, ARMOR, HELM
    private int value;

    public Artifact(ArtifactType type, int value) {
        this.type = type;
        this.value = value;
    }

    public ArtifactType getType() { return type; }
    public int getValue() { return value; }
}
