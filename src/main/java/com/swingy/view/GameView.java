package com.swingy.view;

import com.swingy.model.battle.BattleResult;
import com.swingy.controller.Controller;

// because GuiView might need to inherit from 2 diferent classes, we will use an
// interface instead of a class
public interface GameView {
    void run(Controller controller);

    void showHeroDetails(HeroStats stats);

    boolean askFight();

    void onBattleStart();

    boolean askArtifactPickup(ArtifactStats artifactStats);

    void drawMap(MapState state);

    void showMessage(String message);

    void showBattleResult(BattleResult result);

    void showGameOver(String message);

    void showVictory(String message);

}
