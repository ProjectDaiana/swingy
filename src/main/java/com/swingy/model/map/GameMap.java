package com.swingy.model.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.swingy.model.villain.Villian;

public class GameMap {

    public enum Direction { NORTH, EAST, SOUTH, WEST }

    private final int size;
    private int heroX;
    private int heroY;
    private final Map<String, Villian> villains = new HashMap<>();

    public GameMap(int heroLevel) {
        this.size = (heroLevel - 1) * 5 + 10 - (heroLevel % 2);
        this.heroX = size / 2;
        this.heroY = size / 2;
        populateVillains(heroLevel);
    }

    private void populateVillains(int heroLevel) {
        Random randomizer = new Random();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (x == heroX && y == heroY) continue;
                if (randomizer.nextInt(3) == 0) { // ~33% chance per cell
                    int vLevel = Math.max(1, heroLevel + randomizer.nextInt(3) - 1);
                    Villian v = new Villian(
                        vLevel,
                        5 + vLevel * 2,
                        3 + vLevel,
                        20 + vLevel * 5
                    );
                    villains.put(key(x, y), v);
                }
            }
        }
    }

    public void moveHero(Direction dir) {
        switch (dir) {
            case NORTH -> heroY--;
            case SOUTH -> heroY++;
            case WEST  -> heroX--;
            case EAST  -> heroX++;
        }
    }

    public boolean isAtBorder() {
        return heroX == 0 || heroY == 0 || heroX == size - 1 || heroY == size - 1;
    }

    public Villian getVillainAt(int x, int y) {
        return villains.get(key(x, y));
    }

    public void removeVillainAt(int x, int y) {
        villains.remove(key(x, y));
    }

    public int getSize()  { return size; }
    public int getHeroX() { return heroX; }
    public int getHeroY() { return heroY; }

    public boolean hasVillain(int x,int y) { return getVillainAt(x, y) != null; }

    private static String key(int x, int y) { return x + "," + y; }
}
