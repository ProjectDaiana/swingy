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

    public static BattleResult fight(Hero hero, Villian villain) {
        int heroHp = hero.getHitPoints();
        int villainHp = villain.getHitPoints();
        int heroDmg = Math.max(0, hero.getAttack() - villain.getDefense());
        int villainDmg = Math.max(0, villain.getAttack() - hero.getDefense());

        if (heroDmg == 0 && villainDmg == 0) {
            if (hero.getAttack() <= villain.getAttack()) {
                hero.dies();
                return new BattleResult(null, BattleResult.Result.LOSES, 0);
            }
        } else {
            while (heroHp > 0 && villainHp > 0) {
                villainHp -= heroDmg;
                if (villainHp <= 0)
                    break;
                heroHp -= villainDmg;
            }
            if (heroHp <= 0) {
                hero.dies();
                return new BattleResult(null, BattleResult.Result.LOSES, 0);
            }
            hero.setHitPoints(heroHp);
        }

        int xp = villain.getAttack() * 10;
        return new BattleResult(rollArtifact(villain), BattleResult.Result.WIN, xp);
    }

    private static Artifact rollArtifact(Villian villain) {
        Random random = new Random();
        if (random.nextInt(2) != 0)
            return null;
        ArtifactType[] types = ArtifactType.values();
        ArtifactType randomType = types[random.nextInt(types.length)];
        int value = villain.getAttack();
        return switch (randomType) {
            case WEAPON -> new Weapon(value);
            case HELM -> new Helm(value);
            case ARMOR -> new Armor(value);
        };
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
