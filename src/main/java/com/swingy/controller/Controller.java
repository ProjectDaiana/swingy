package com.swingy.controller;

import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroType;
import com.swingy.repository.HeroRepository;
import com.swingy.model.villain.Villian;
import com.swingy.view.GameView;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.Battle;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.map.GameMap;
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

    public void startGame() {
        boolean isNewHero = view.askNewHero();
        String name = null;
        Hero hero = null;
        if (isNewHero) {
            name = view.askHeroName();
            HeroType HeroType = view.askHeroType();
            hero = new Hero(name, HeroType);
        } else {
            heroes = repository.loadHeroes(path);
            Hero selectedHero = view.askSelectHero(heroes);
            hero = selectedHero;
        }

        GameMap map = new GameMap(hero.getLevel()); // Example size, adjust as needed
                                                    // value

        view.showHeroDetails(hero);

        view.drawMap(map, hero);

        while (true) {
            if (hero.getHitPoints() <= 0) {
                view.showGameOver("You have been defeated!");
                break;
            }
            if (map.isAtBorder()) { // Example victory condition
                view.showVictory("Congratulations! You have won the game!");
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

    public void endGame() {
        repository.saveHeroes(heroes, path);
        view.showGameOver("Game Over!");

    }

    public void handleBattle(Hero hero, GameMap map) {
        boolean fight = view.askFight();

        if (fight) {
            resolveBattle(hero);
            return;
        }

        view.showMessage("You chose to flee from the battle.");
        if (Battle.tryToRun()) {
            view.showMessage("You successfully fled from the battle.");
            map.moveHeroTo(prevHeroX, prevHeroY);
        } else {
            view.showMessage("Too slow. Now you MUST fight!");
            resolveBattle(hero);
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
                view.showMessage("You won the battle and gained experience!");
                hero.gainXp(battleResult.getXPGained());
                Artifact artifact = battleResult.getArtifact();
                if (artifact != null) {
                    view.showMessage("The villain dropped an artifact: " + artifact.getType().toString()
                            + " with value: " + artifact.getValue());
                    handleArtifactPickup(hero, artifact);
                }
            }
            case LOSES -> {
                view.showMessage("You lost the battle and took damage.");
                hero.dies();
            }
        }
    }

    private void resolveBattle(Hero hero) {
        BattleResult battleResult = Battle.fight(hero, villian);
        view.showBattleResult(battleResult);
        applyBattleResult(hero, battleResult);
    }
}
