package com.swingy.view.console;

import com.swingy.controller.Controller;
import com.swingy.model.hero.HeroType;
import com.swingy.view.GameView;
import com.swingy.view.HeroStats;
import com.swingy.view.MapState;
import com.swingy.model.DirectionType;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.artifact.Artifact;
// import com.swingy.view.HeroCreationData;
import java.util.Scanner;
import java.util.List;

public class ConsoleView implements GameView {
    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(Controller controller) {
        List<HeroStats> heroes = controller.loadHeroes();
        if (askNewHero()) {
            String name = askHeroName();
            HeroType heroType = askHeroType();
            controller.createHero(name, heroType);
        } else {
            int selectedHeroIndex = askSelectHero(heroes);
            controller.selectHero(selectedHeroIndex);
        }
        showHeroDetails(controller.getHeroStats());
        drawMap(controller.getMapState());
        while (true) {
            if (controller.isHeroDefeated() || controller.isAtBorder()) {
                controller.endGame();
                break;
            }
            DirectionType direction = askDirection();
            controller.onMove(direction);
            drawMap(controller.getMapState());
        }
    }

    @Override
    public void showHeroDetails(HeroStats stats) {
        System.out.println("Hero Details:");
        System.out.println("Name: " + stats.name());
        System.out.println("Type: " + stats.type());
        System.out.println("Level: " + stats.level());
        System.out.println("XP: " + stats.xp());
        System.out.println("Attack: " + stats.attack());
        System.out.println("Defense: " + stats.defense());
        System.out.println("Hit Points: " + stats.hitPoints());
    }

    public void showBattleResult(BattleResult result) {
        System.out.println("Battle Result: " + result.getResult().toString());
    }

    public boolean askNewHero() {
        System.out.println("Do you want to create a new hero or load an existing one? (1: Create, 2: Load)");
        String input = scanner.nextLine();
        if (!input.matches("^[12]$")) {
            System.out.println("Choose 1 or 2 only.");
            return askNewHero();
        }
        return "1".equals(input);
    }

    public String askHeroName() {
        System.out.println("Enter hero name: ");
        return scanner.nextLine();
    }

    public HeroType askHeroType() {
        System.out.println("Select hero class (1: Warrior, 2: Mage, 3: Rogue): ");
        String input = scanner.nextLine();
        if (!input.matches("\\d+")) {
            System.out.println("Only digits are allowed.");
            return askHeroType(); // or retry
        }
        int choice = Integer.parseInt(input);
        switch (choice) {
            case 1:
                return HeroType.WARRIOR;
            case 2:
                return HeroType.WIZARD;
            case 3:
                return HeroType.ROGUE;
            default:
                System.out.println("Invalid choice. Defaulting to Warrior.");
                return HeroType.WARRIOR;
        }
    }

    public int askSelectHero(List<HeroStats> heroes) {
        System.out.println("Select a hero from the list:");
        for (int i = 0; i < heroes.size(); i++) {
            HeroStats stats = heroes.get(i);
            System.out.println((i + 1) + ": " + stats.name() + " (Level: " + stats.level() + ")");
        }
        String input = scanner.nextLine();
        if (!input.matches("\\d+")) {
            System.out.println("Only digits are allowed.");
            return askSelectHero(heroes);
        }
        int choice = Integer.parseInt(input);
        if (choice < 1 || choice > heroes.size()) {
            System.out.println("Choose between 1 and " + heroes.size() + ".");
            return askSelectHero(heroes);
        }
        return choice - 1;
    }

    public DirectionType askDirection() {
        System.out.println("Enter direction (W/A/S/D): ");
        String input = scanner.nextLine().toUpperCase();

        switch (input) {
            case "W":
                return DirectionType.NORTH;
            case "S":
                return DirectionType.SOUTH;
            case "A":
                return DirectionType.WEST;
            case "D":
                return DirectionType.EAST;
            default:
                System.out.println("Invalid direction. Use W/A/S/D.");
                return askDirection();
        }
    }

    @Override
    public void onBattleStart() {
        System.out.println("A battle begins!");
    }

    public boolean askFight() {
        System.out.println("Do you want to fight the villain? (Y/N): ");
        String input = scanner.nextLine().toUpperCase();
        if (!"Y".equals(input) && !"N".equals(input)) {
            System.out.println("Invalid input. Please enter Y or N.");
            return askFight();
        }
        return "Y".equals(input);
    }

    public boolean askArtifactPickup(Artifact artifact) {
        System.out.println(
                "Villain dropped:: " + artifact.getType().toString() + ". Do you want to pick it up? (Y/N): ");
        String input = scanner.nextLine().toUpperCase();
        if (!"Y".equals(input) && !"N".equals(input)) {
            System.out.println("Invalid input. Please enter Y or N.");
            return askArtifactPickup(artifact);
        }
        return "Y".equals(input);
    }

    @Override
    public void drawMap(MapState state) {
        for (int y = 0; y < state.size(); y++) {
            for (int x = 0; x < state.size(); x++) {
                if (x == state.heroX() && y == state.heroY()) {
                    System.out.print("H ");
                } else if (isVillainAt(state, x, y)) {
                    System.out.print("V ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    private boolean isVillainAt(MapState state, int x, int y) {
        for (int[] pos : state.villainPositions()) {
            if (pos[0] == x && pos[1] == y)
                return true;
        }
        return false;
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showGameOver(String message) {
        System.out.println(message);
    }

    public void showVictory(String message) {
        System.out.println(message);
    }

    public boolean askPlayAgain() {
        System.out.println("Do you want to play again? (Y/N): ");
        String input = scanner.nextLine().toUpperCase();
        if (!"Y".equals(input) && !"N".equals(input)) {
            System.out.println("Invalid input. Please enter Y or N.");
            return askPlayAgain();
        }
        return "Y".equals(input);
    }
}
