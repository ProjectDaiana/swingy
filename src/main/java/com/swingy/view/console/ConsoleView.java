package com.swingy.view.console;

import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroType;
import com.swingy.view.GameView;
import com.swingy.model.DirectionType;
import com.swingy.model.battle.BattleResult;
import com.swingy.model.artifact.Artifact;
import com.swingy.model.map.GameMap;
import java.util.Scanner;
import java.util.List;

public class ConsoleView implements GameView {
    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    // @Override
    public void showHeroDetails(Hero hero) {
        System.out.println("Hero Details:");
        System.out.println("Name: " + hero.getName());
        System.out.println("Class: " + hero.getHeroType().toString());
        System.out.println("Level: " + hero.getLevel());
        System.out.println("XP: " + hero.getXp());
        System.out.println("Attack: " + hero.getAttack());
        System.out.println("Defense: " + hero.getDefense());
        System.out.println("Hit Points: " + hero.getHitPoints());
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
        System.out.println("Select hero class (1: Warrior, 2: Mage, 3: Archer): ");
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

    public Hero askSelectHero(List<Hero> heroes) {
        System.out.println("Select a hero from the list:");
        for (int i = 0; i < heroes.size(); i++) {
            Hero hero = heroes.get(i);
            System.out.println((i + 1) + ": " + hero.getName() + " (Level: " + hero.getLevel() + ")");
        }
        int choice = Integer.parseInt(scanner.nextLine());
        return heroes.get(choice - 1);
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

    public void drawMap(GameMap map, Hero hero) {
        int size = map.getSize();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (x == map.getHeroX() && y == map.getHeroY()) {
                    System.out.print("H "); // Hero position
                } else if (map.hasVillain(x, y)) {
                    System.out.print("V "); // Villain position
                } else {
                    System.out.print(". "); // Empty space
                }
            }
            System.out.println();
        }
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
