package com.swingy.view;

public record ArtifactStats(String type, int value, String description) {
    @Override
    public String type() {
        return type.toLowerCase();
    }
}
