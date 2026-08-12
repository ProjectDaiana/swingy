package com.swingy.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroClass;

public class FileHeroRepository implements HeroRepository {
    public void saveHeroes(List<Hero> heroes, Path path) {
        StringBuilder toWrite = new StringBuilder(); // not thread safe, use StringBuffer if needed
        for (Hero hero : heroes) {
            String line = String.format(
                    "name=%s;class=%s;level=%d;xp=%d;attack=%d;defense=%d;hitPoints=%d",
                    hero.getName(),
                    hero.getHeroClass().name(),
                    hero.getLevel(),
                    hero.getXp(),
                    hero.getAttack(),
                    hero.getDefense(),
                    hero.getHitPoints());
            toWrite.append(line).append(System.lineSeparator());
        }
        try {
            Files.writeString(path, toWrite);
        } catch (IOException e) {
            System.out.println("Could not save hero: " + e.getMessage());
        }
    }

    public List<Hero> loadHeroes(Path path) {
        List<Hero> heroes = new ArrayList<>();
        if (!Files.exists(path)) {
            return heroes; // no file yet, nothing to load
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                parseHero(line).ifPresent(heroes::add);
                // Lambda replaces:
                // Optional<Hero> result = parseHeroLine(line);
                // if (result.isPresent()) {
                // Hero hero = result.get();
                // heroes.add(hero);
                // }
            }

        } catch (IOException e) {
            System.out.println("Could not read hero file: " + e.getMessage());
        }
        return heroes;
    }

    private Optional<Hero> parseHero(String line) {
        try {
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
            return Optional.of(hero);
        } catch (Exception e) {
            System.out.println("Skipping corrupted hero line: " + e.getMessage());
            return Optional.empty();
        }
    }
}
