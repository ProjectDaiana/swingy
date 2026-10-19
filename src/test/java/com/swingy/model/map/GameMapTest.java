package com.swingy.model.map;

import static org.junit.Assert.*;
import org.junit.Test;

public class GameMapTest {

    @Test
    public void mapSize_level1_is9() {
        assertEquals(9, new GameMap(1).getSize());
    }

    @Test
    public void mapSize_level2_is15() {
        assertEquals(15, new GameMap(2).getSize());
    }

    @Test
    public void mapSize_level3_is19() {
        assertEquals(19, new GameMap(3).getSize());
    }

    @Test
    public void mapSize_level4_is25() {
        assertEquals(25, new GameMap(4).getSize());
    }

    @Test
    public void mapSize_level5_is29() {
        assertEquals(29, new GameMap(5).getSize());
    }

    @Test
    public void mapSize_level6_is35() {
        assertEquals(35, new GameMap(6).getSize());
    }

    @Test
    public void mapSize_level7_is39() {
        assertEquals(39, new GameMap(7).getSize());
    }

    @Test
    public void mapSize_growsWithLevel() {
        assertTrue(new GameMap(2).getSize() > new GameMap(1).getSize());
        assertTrue(new GameMap(3).getSize() > new GameMap(2).getSize());
        assertTrue(new GameMap(4).getSize() > new GameMap(3).getSize());
        assertTrue(new GameMap(5).getSize() > new GameMap(4).getSize());
        assertTrue(new GameMap(6).getSize() > new GameMap(5).getSize());
        assertTrue(new GameMap(7).getSize() > new GameMap(6).getSize());
    }

    @Test
    public void heroStartsAtCenter() {
        GameMap map = new GameMap(1);
        int center = map.getSize() / 2;
        assertEquals(center, map.getHeroX());
        assertEquals(center, map.getHeroY());
    }
}
