package com.swingy.view;

import java.util.List;
import com.swingy.model.DirectionType;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.battle.BattleResult;
import com.swingy.view.HeroStats;
import com.swingy.view.MapState;
import com.swingy.controller.Controller;

// because GuiView might need to inherit from 2 diferent classes, we will use an
// interface instead of a class
public interface GameView {
    void run(Controller controller);



    void showHeroDetails(HeroStats stats);


    boolean askFight();

    void onBattleStart();

    boolean askArtifactPickup(Artifact artifactDetails);

    void drawMap(MapState state);

    void showMessage(String message);

    void showBattleResult(BattleResult result);

    void showGameOver(String message);

    void showVictory(String message);


}
