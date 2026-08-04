package com.swingy.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroClass;

public class FileHeroRepository implements HeroRepository {
    public void saveHero(Hero hero) {

    }

    public List<Hero> loadHeroes(Path path) {
        List<Hero> heroes = new ArrayList<>();
        if (!Files.exists(path)) {
            return heroes; // no file yet, nothing to load
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] pairs = line.split(";");
                Map<String, String> data = new HashMap<>();
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    String key = keyValue[0];
                    String value = keyValue[1];
                    data.put(key, value);
                }
                String name = data.get("name");
                HeroClass heroClass = HeroClass.valueOf(data.get("class"));
                int level = Integer.parseInt(data.get("level"));
                int xp = Integer.parseInt(data.get("xp"));
                int attack = Integer.parseInt(data.get("attack"));
                int defense = Integer.parseInt(data.get("defense"));
                int hitPoints = Integer.parseInt(data.get("hitPoints"));

                Hero hero = new Hero(name, heroClass, level, xp, attack, defense, hitPoints);
                heroes.add(hero);
            }
        } catch (IOException e) {
            System.out.println("Could not read hero file: " + e.getMessage());
        }
        return heroes;
    }
}
