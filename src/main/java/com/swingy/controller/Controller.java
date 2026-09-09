package com.swingy.controller;

import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroBuilder;
import com.swingy.model.hero.HeroType;
import com.swingy.repository.HeroRepository;
import com.swingy.model.villain.Villian;
import com.swingy.view.GameView;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.Battle;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.map.GameMap;
import com.swingy.model.GameResult;
import com.swingy.model.DirectionType;
import com.swingy.view.HeroStats;
import com.swingy.view.MapState;
import java.util.List;

public class Controller {
    private GameView view;
    private HeroRepository repository;
    private Villian villian;
    private List<Hero> heroes;
    private Hero hero;
    private GameMap map;

    public Controller(GameView view, HeroRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public List<HeroStats> loadHeroes() {
        heroes = repository.loadHeroes();
        List<HeroStats> heroesStats = new java.util.ArrayList<>();
        for (Hero h : heroes) {
            heroesStats.add(toHeroStats(h));
        }
        return heroesStats;
    }

    private HeroStats toHeroStats(Hero h) {
        com.swingy.model.artifact.Artifact weapon = h.getEquipped(com.swingy.model.artifact.ArtifactType.WEAPON);
        com.swingy.model.artifact.Artifact armor  = h.getEquipped(com.swingy.model.artifact.ArtifactType.ARMOR);
        com.swingy.model.artifact.Artifact helm   = h.getEquipped(com.swingy.model.artifact.ArtifactType.HELM);
        return new HeroStats(
            h.getName(), h.getType().toString(), h.getLevel(), h.getXp(),
            h.getAttack(), h.getDefense(), h.getHitPoints(),
            weapon != null ? "+" + weapon.getValue() : "none",
            armor  != null ? "+" + armor.getValue()  : "none",
            helm   != null ? "+" + helm.getValue()   : "none"
        );
    }

    public void createHero(String name, HeroType type) {
        this.hero = new HeroBuilder(name, type).build();
        heroes.add(this.hero);
        this.map = new GameMap(this.hero.getLevel());
    }

    public void selectHero(int index) {
        this.hero = heroes.get(index);
        this.map = new GameMap(this.hero.getLevel());
    }

    public boolean isHeroDefeated() {
        return hero.isDefeated();
    }

    public boolean isAtBorder() {
        return map.isAtBorder();
    }

    public void onFight(Hero hero, GameMap map) {
        resolveBattle(hero, map);
    }

    public void onFlee(Hero hero, GameMap map) {
        view.showMessage("You chose to flee from the battle.");
        if (Battle.tryToRun()) {
            view.showMessage("You successfully fled from the battle.");
            map.moveHeroToPrevPosition();
        } else {
            view.showMessage("Too slow. Now you MUST fight!");
            resolveBattle(hero, map);
        }
    }

    public void onMove(DirectionType direction) {
        switch (direction) {
            case NORTH -> map.moveHero(GameMap.Direction.NORTH);
            case SOUTH -> map.moveHero(GameMap.Direction.SOUTH);
            case WEST -> map.moveHero(GameMap.Direction.WEST);
            case EAST -> map.moveHero(GameMap.Direction.EAST);
        }
        if (map.hasVillain(map.getHeroX(), map.getHeroY())) {
            villian = map.getVillainAt(map.getHeroX(), map.getHeroY());
            handleBattle(false, hero, map);
        }
    }

    public void endGame() {
        repository.saveHeroes(heroes);
        GameResult result = hero.isDefeated() ? GameResult.DEFEAT : GameResult.VICTORY;
        switch (result) {
            case VICTORY -> view.showVictory("Congratulations! You have won the game!");
            case DEFEAT -> view.showGameOver("Game Over! Your hero has been defeated.");
        }
    }

    public void handleBattle(boolean isFighting, Hero hero, GameMap map) {
        view.onBattleStart();
        boolean fight = view.askFight();

        if (fight) {
            onFight(hero, map);
        } else {
            onFlee(hero, map);
        }
    }

    public void onArtifactPickup(boolean pickup, Hero hero, Artifact artifact) {
        if (pickup) {
            view.showMessage("You picked up the artifact!");
            hero.equipArtifact(artifact);
            view.showMessage("You equipped the artifact: " + artifact.getType().toString() + " with value: "
                    + artifact.getValue());
        } else {
            view.showMessage("You left the artifact behind.");
        }
    }

    public void onArtifactLeave(Hero hero, Artifact artifact) {
        view.showMessage("You left the artifact behind.");
    }

    private void applyBattleResult(Hero hero, BattleResult battleResult) {
        switch (battleResult.getResult()) {
            case WIN -> {
                view.showMessage("You won the battle! You gained " + battleResult.getXPGained() + " XP.");
                hero.gainXp(battleResult.getXPGained());
                Artifact artifact = battleResult.getArtifact();
                if (artifact != null) {
                    boolean pickup = view.askArtifactPickup(artifact);
                    onArtifactPickup(pickup, hero, artifact);
                }
            }
            case LOSES -> {
                view.showMessage("You lost the battle! Your hero has been defeated.");
                hero.dies();
            }
        }
    }

    private void resolveBattle(Hero hero, GameMap map) {
        BattleResult battleResult = Battle.fight(hero, villian);
        if (battleResult.getResult() == BattleResult.Result.WIN) {
            map.removeVillainAt(map.getHeroX(), map.getHeroY());
        }
        view.showBattleResult(battleResult);
        applyBattleResult(hero, battleResult);
    }

    public MapState getMapState() {
        return new MapState(map.getSize(), map.getHeroX(), map.getHeroY(), map.getVillainPositions());
    }

    public HeroStats getHeroStats() {
        return toHeroStats(hero);
    }

}
