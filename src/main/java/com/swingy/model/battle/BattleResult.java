package com.swingy.model.battle;
import com.swingy.model.artifact.Artifact;

public class BattleResult {
    public enum Result {
      WIN,
      FLEES,
      LOSES
    }
    private Artifact artifact;
    private Result result;
    private int xpGained;

    public BattleResult(Artifact artifact, Result result, int xpGained) {
        this.artifact = artifact;
        this.result = result;
        this.xpGained = xpGained;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public int getXPGained() {
        return xpGained;
    }

    public Result getResult() {
        return result;
    }
}
