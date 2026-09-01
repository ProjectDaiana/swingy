package com.swingy.view;

import java.util.List;

import com.swingy.model.DirectionType;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.hero.Hero;
import com.swingy.model.map.GameMap;
import com.swingy.view.HeroCreationData;

// because GuiView might need to inherit from 2 diferent classes, we will use an
// interface instead of a class
public interface GameView {
    boolean askNewHero();

    HeroCreationData createNewHero();

    Hero askSelectHero(List<Hero> heroes);

    void showHeroDetails(Hero hero);

    DirectionType askDirection();

    boolean askFight();

    boolean askArtifactPickup(Artifact artifactDetails);

    void drawMap(GameMap map, Hero hero);

    void showMessage(String message);

    void showBattleResult(BattleResult result);

    void showGameOver(String message);

    void showVictory(String message);

    boolean askPlayAgain();

}
