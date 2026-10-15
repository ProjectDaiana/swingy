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
import com.swingy.view.ArtifactStats;
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

    public void setView(GameView view) {
        this.view = view;
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
        com.swingy.model.artifact.Artifact armor = h.getEquipped(com.swingy.model.artifact.ArtifactType.ARMOR);
        com.swingy.model.artifact.Artifact helm = h.getEquipped(com.swingy.model.artifact.ArtifactType.HELM);
        return new HeroStats(
                h.getName(), h.getType().toString(), h.getLevel(), h.getXp(),
                h.getAttack(), h.getDefense(), h.getHitPoints(),
                weapon != null ? "+" + weapon.getValue() : "none",
                armor != null ? "+" + armor.getValue() : "none",
                helm != null ? "+" + helm.getValue() : "none");
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

    public void onFight(Hero hero, GameMap map) {
        resolveBattle(hero, map);
    }

    public void onFlee(Hero hero, GameMap map) {
        // view.showMessage("You chose to flee from the battle.");
        if (Battle.tryToRun()) {
            view.showMessage("You successfully fled from the battle.");
            map.moveHeroToPrevPosition();
        } else {
            view.showMessage("Too slow. Now you MUST fight!");
            resolveBattle(hero, map);
        }
    }

    public GameResult onMove(DirectionType direction) {
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
        if (hero.isDefeated())
            return GameResult.DEFEAT;
        if (map.isAtBorder())
            return hero.getLevel() >= Hero.MAX_LEVEL ? GameResult.GAME_WON : GameResult.MAP_CLEARED;
        return GameResult.ONGOING;
    }

    public boolean endGame(GameResult result) {
        switch (result) {
            case DEFEAT -> {
                heroes = repository.loadHeroes();
                view.showGameOver("Game Over! Your hero has been defeated.\nYou will return to hero selection.");
                return false;
            }
            case GAME_WON -> {
                repository.saveHeroes(heroes);
                view.showVictory(hero.getName() + " has reached the pinnacle of power and conquered the realm!");
                return false;
            }
            case MAP_CLEARED -> {
                repository.saveHeroes(heroes);
                boolean keepPlaying = view.askNewGame(hero.getName() + " escaped the map!\nContinue to the next level?");
                if (keepPlaying) this.map = new GameMap(hero.getLevel());
                return keepPlaying;
            }
            default -> { return false; }
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


    private void applyBattleResult(Hero hero, BattleResult battleResult) {
        switch (battleResult.getResult()) {
            case WIN -> {
                int levelBefore = hero.getLevel();
                hero.gainXp(battleResult.getXPGained());
                if (hero.getLevel() > levelBefore)
                    view.showLevelUp(hero.getLevel());
                Artifact artifact = battleResult.getArtifact();
                if (artifact != null && view.askArtifactPickup(getArtifactStats(artifact)))
                    hero.equipArtifact(artifact);
            }
            case LOSES -> {
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

    public ArtifactStats getArtifactStats(Artifact artifact) {
        String description = switch (artifact.getType()) {
            case WEAPON -> "attack";
            case ARMOR -> "defense";
            case HELM -> "hitPoints";
        };
        return new ArtifactStats(artifact.getType().toString(), artifact.getValue(), description);
    }

}
