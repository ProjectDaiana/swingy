package com.swingy.model.battle;

import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroBuilder;
import com.swingy.model.hero.HeroType;
import com.swingy.model.villain.Villian;
import org.junit.Test;

public class BattleSimulationTest {

    private static final int RUNS = 1000;

    // Warrior stats at a given level: base + growth * (level - 1)
    private Hero warriorAtLevel(int level) {
        int atk = 10 + 3 * (level - 1);
        int def = 5 + 2 * (level - 1);
        int hp = 50 + 15 * (level - 1);
        return new HeroBuilder("Test", HeroType.WARRIOR)
                .level(level).attack(atk).defense(def).hitPoints(hp)
                .build();
    }

    // Villain stats matching GameMap.populateVillains (same level as hero)
    private Villian villainAtLevel(int vLevel) {
        return new Villian(vLevel, 5 + vLevel * 4, 3 + vLevel, 30 + vLevel * 18);
    }

    private void simulate(int level) {
        int wins = 0, losses = 0;
        for (int i = 0; i < RUNS; i++) {
            Hero hero = warriorAtLevel(level);
            Villian villain = villainAtLevel(level);
            BattleResult result = Battle.fight(hero, villain);
            if (result.getResult() == BattleResult.Result.WIN) wins++;
            else losses++;
        }
        System.out.printf("Level %d — wins: %d, losses: %d (loss rate: %.1f%%)%n",
                level, wins, losses, losses * 100.0 / RUNS);
    }

    @Test
    public void simulateBattlesAcrossLevels() {
        System.out.println("=== Battle simulation (Warrior vs same-level villain) ===");
        for (int level = 1; level <= 7; level++) {
            simulate(level);
        }
    }
}
