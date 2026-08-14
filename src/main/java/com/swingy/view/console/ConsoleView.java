package com.swingy.view.console;

import com.swingy.model.hero.Hero;
import com.swingy.view.GameView;
import com.swingy.model.DirectionType;
import com.swingy.model.battle.BattleResult;
import java.util.Scanner;

public class ConsoleView implements GameView {
    private Scanner scanner;

    public ConsoleView(Scanner scanner) {
        this.scanner = scanner;
        // Initialize any necessary components for the console view
    }

    @Override
    public void showHeroDetails(Hero hero) {
        System.out.println("Hero Details:");
        System.out.println("Name: " + hero.getName());
        System.out.println("Class: " + hero.getHeroClass().getName());
        System.out.println("Level: " + hero.getLevel());
        System.out.println("XP: " + hero.getXp());
        System.out.println("Attack: " + hero.getAttack());
        System.out.println("Defense: " + hero.getDefense());
        System.out.println("Hit Points: " + hero.getHitPoints());
    }

    public void showBattleResult(BattleResult result) {
        System.out.println("Battle Result: " + result.toString());
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
}
