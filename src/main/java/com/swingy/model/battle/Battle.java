package com.swingy.model.battle;

import java.util.Random;
import com.swingy.model.artifact.Armor;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.artifact.ArtifactType;
import com.swingy.model.artifact.Helm;
import com.swingy.model.artifact.Weapon;
import com.swingy.model.hero.Hero;
import com.swingy.model.villain.Villian;

// utility class for handling battles between heroes and villains
public class Battle {
    private Battle() {
    };

    public static BattleResult fight(Hero hero, Villian villian) {
        int damage = Math.max(0, hero.getAttack() - villian.getDefense());
        BattleResult.Result result = damage > 0 ? BattleResult.Result.WIN : BattleResult.Result.LOSES;
        Artifact artifact = null;
        if (damage > 0) {
            Random random = new Random();
            if (random.nextInt(2) == 0) {
                ArtifactType[] types = ArtifactType.values();
                ArtifactType randomType = types[random.nextInt(types.length)];

                switch (randomType) {
                    case WEAPON -> artifact = new Weapon(damage);
                    case HELM -> artifact = new Helm(damage);
                    case ARMOR -> artifact = new Armor(damage);
                }
            }
        }
        return new BattleResult(artifact, result, damage);
    }

    public static boolean tryToRun() {
        Random random = new Random();
        if (random.nextInt(2) == 0) {
            // also needs to retunr to previous position in the controller
            return true;
        }
        return false;
    }

}
