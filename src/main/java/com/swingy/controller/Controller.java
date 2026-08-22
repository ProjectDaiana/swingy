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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Controller {
    private GameView view;
    private HeroRepository repository;
    private Villian villian;
    private Path path = Paths.get("heroes.txt");

    private List<Hero> heroes;
    private int prevHeroX;
    private int prevHeroY;

    public Controller(GameView view, HeroRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public Hero setupHero() {
        boolean isNewHero = view.askNewHero();
        String name = null;
        Hero hero = null;
        if (isNewHero) {
            name = view.askHeroName();
            HeroType archetype = view.askHeroType();
            hero = new HeroBuilder(name, archetype).build();
        } else {
            heroes = repository.loadHeroes(path);
            Hero selectedHero = view.askSelectHero(heroes);
            hero = selectedHero;
        }
        view.showHeroDetails(hero);
        return hero;
    }

    public void startGameLoop() {
        Hero hero = setupHero();

        // Setup the game map and draw it
        GameMap map = new GameMap(hero.getLevel()); // Example size, adjust as needed
        view.drawMap(map, hero);

        // Main game loop
        while (true) {
            // Check for game over conditions
            if (hero.isDefeated()) {
                endGame(GameResult.DEFEAT);
                break;
            }
            if (map.isAtBorder()) { // Example victory condition
                endGame(GameResult.VICTORY);
                break;
            }

            // Ask for direction and move hero
            prevHeroX = map.getHeroX();
            prevHeroY = map.getHeroY();
            switch (view.askDirection()) {
                case NORTH -> map.moveHero(GameMap.Direction.NORTH);
                case SOUTH -> map.moveHero(GameMap.Direction.SOUTH);
                case WEST -> map.moveHero(GameMap.Direction.WEST);
                case EAST -> map.moveHero(GameMap.Direction.EAST);
            }

            // Check for battle
            if (map.hasVillain(map.getHeroX(), map.getHeroY())) {
                villian = map.getVillainAt(map.getHeroX(), map.getHeroY());
                handleBattle(hero, map);
            }

            view.drawMap(map, hero);
        }
    }

    public void endGame(GameResult result) {
        repository.saveHeroes(heroes, path);
        switch (result) {
            case VICTORY -> view.showVictory("Congratulations! You have won the game!");
            case DEFEAT -> view.showGameOver("Game Over! Your hero has been defeated.");
        }
    }

    public void handleBattle(Hero hero, GameMap map) {
        boolean fight = view.askFight();

        if (fight) {
            resolveBattle(hero, map);
            return;
        }

        view.showMessage("You chose to flee from the battle.");
        if (Battle.tryToRun()) {
            view.showMessage("You successfully fled from the battle.");
            map.moveHeroTo(prevHeroX, prevHeroY);
        } else {
            view.showMessage("Too slow. Now you MUST fight!");
            resolveBattle(hero, map);
        }

    }

    public void handleArtifactPickup(Hero hero, Artifact artifact) {
        boolean pickup = view.askArtifactPickup(artifact);
        if (pickup) {
            view.showMessage("You picked up the artifact!");
            hero.equipArtifact(artifact);
            view.showMessage("You equipped the artifact: " + artifact.getType().toString() + " with value: "
                    + artifact.getValue());
        } else {
            view.showMessage("You left the artifact behind.");
        }
    }

    private void applyBattleResult(Hero hero, BattleResult battleResult) {
        switch (battleResult.getResult()) {
            case WIN -> {
                view.showMessage("You won the battle! You gained " + battleResult.getXPGained() + " XP.");
                hero.gainXp(battleResult.getXPGained());
                Artifact artifact = battleResult.getArtifact();
                if (artifact != null) {
                    view.showMessage("The villain dropped an artifact: " + artifact.getType().toString()
                            + " with value: " + artifact.getValue());
                    handleArtifactPickup(hero, artifact);
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
}
