package com.swingy.controller;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.swingy.model.DirectionType;
import com.swingy.model.GameResult;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.hero.HeroType;
import com.swingy.repository.HeroRepository;
import com.swingy.model.hero.Hero;
import com.swingy.view.ArtifactStats;
import com.swingy.view.GameView;
import com.swingy.view.HeroStats;
import com.swingy.view.MapState;

import java.util.ArrayList;
import java.util.List;

public class ControllerTest {

    static class MockView implements GameView {
        int showVictoryCount = 0;
        int showGameOverCount = 0;
        int askNewGameCount = 0;
        int showLevelUpCount = 0;
        boolean askNewGameReturn = false;

        @Override
        public void run(com.swingy.controller.Controller c) {
        }

        @Override
        public void showHeroDetails(HeroStats s) {
        }

        @Override
        public boolean askFight() {
            return true;
        }

        @Override
        public void onBattleStart() {
        }

        @Override
        public boolean askArtifactPickup(ArtifactStats a) {
            return false;
        }

        @Override
        public void drawMap(MapState s) {
        }

        @Override
        public void showMessage(String m) {
        }

        @Override
        public void showBattleResult(BattleResult r) {
        }

        @Override
        public void showLevelUp(int l) {
            showLevelUpCount++;
        }

        @Override
        public void showGameOver(String m) {
            showGameOverCount++;
        }

        @Override
        public void showVictory(String m) {
            showVictoryCount++;
        }

        @Override
        public boolean askNewGame(String m) {
            askNewGameCount++;
            return askNewGameReturn;
        }
    }

    static class MockRepository implements HeroRepository {
        List<Hero> heroes = new ArrayList<>();

        @Override
        public List<Hero> loadHeroes() {
            return new ArrayList<>(heroes);
        }

        @Override
        public void saveHeroes(List<Hero> h) {
        }
    }

    private MockView view;
    private MockRepository repository;
    private Controller controller;

    @Before
    public void setUp() {
        view = new MockView();
        repository = new MockRepository();
        controller = new Controller(view, repository);
        controller.loadHeroes();
        controller.createHero("TestHero", HeroType.WARRIOR);
    }

    @Test
    public void mapCleared_showsAskNewGame_notVictory() {
        controller.endGame(GameResult.MAP_CLEARED);
        assertEquals(1, view.askNewGameCount);
        assertEquals(0, view.showVictoryCount);
        assertEquals(0, view.showGameOverCount);
    }

    @Test
    public void gameWon_showsVictory_notAskNewGame() {
        controller.endGame(GameResult.GAME_WON);
        assertEquals(1, view.showVictoryCount);
        assertEquals(0, view.askNewGameCount);
        assertEquals(0, view.showGameOverCount);
    }

    @Test
    public void defeat_showsGameOver_notVictory() {
        controller.endGame(GameResult.DEFEAT);
        assertEquals(1, view.showGameOverCount);
        assertEquals(0, view.showVictoryCount);
        assertEquals(0, view.askNewGameCount);
    }

    @Test
    public void mapCleared_keepPlaying_true_returnsTrue() {
        view.askNewGameReturn = true;
        boolean result = controller.endGame(GameResult.MAP_CLEARED);
        assertTrue(result);
    }

    @Test
    public void mapCleared_keepPlaying_false_returnsFalse() {
        view.askNewGameReturn = false;
        boolean result = controller.endGame(GameResult.MAP_CLEARED);
        assertFalse(result);
    }

    // --- hero creation ---

    @Test
    public void createHero_setsCorrectName() {
        assertEquals("TestHero", controller.getHeroStats().name());
    }

    @Test
    public void createHero_setsCorrectType() {
        assertEquals("WARRIOR", controller.getHeroStats().type());
    }

    @Test
    public void createHero_startsAtLevelOne() {
        assertEquals(1, controller.getHeroStats().level());
    }

    @Test
    public void createHero_startsWithZeroXp() {
        assertEquals(0, controller.getHeroStats().xp());
    }

    @Test
    public void createHero_createsMap() {
        assertNotNull(controller.getMapState());
    }

    @Test
    public void createHero_warrior_hasCorrectBaseStats() {
        HeroStats stats = controller.getHeroStats();
        assertEquals(10, stats.attack());
        assertEquals(5, stats.defense());
        assertEquals(50, stats.hitPoints());
    }

    @Test
    public void createHero_wizard_hasCorrectBaseStats() {
        Controller c = new Controller(view, repository);
        c.loadHeroes();
        c.createHero("Wizard", HeroType.WIZARD);
        HeroStats stats = c.getHeroStats();
        assertEquals(8, stats.attack());
        assertEquals(3, stats.defense());
        assertEquals(35, stats.hitPoints());
    }

    @Test
    public void createHero_rogue_hasCorrectBaseStats() {
        Controller c = new Controller(view, repository);
        c.loadHeroes();
        c.createHero("Rogue", HeroType.ROGUE);
        HeroStats stats = c.getHeroStats();
        assertEquals(9, stats.attack());
        assertEquals(4, stats.defense());
        assertEquals(40, stats.hitPoints());
    }


    // --- view switch ---

    @Test
    public void viewSwitch_preservesHeroPositionStatsAndActiveGame() {
        controller.onMove(DirectionType.NORTH);

        MapState mapBefore = controller.getMapState();
        HeroStats statsBefore = controller.getHeroStats();
        assertTrue(controller.hasActiveGame());

        controller.setView(new MockView());

        assertEquals(mapBefore.heroX(), controller.getMapState().heroX());
        assertEquals(mapBefore.heroY(), controller.getMapState().heroY());
        assertEquals(statsBefore.attack(), controller.getHeroStats().attack());
        assertEquals(statsBefore.hitPoints(), controller.getHeroStats().hitPoints());
        assertTrue(controller.hasActiveGame());
    }
}
