package com.swingy.repository;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.swingy.model.artifact.Armor;
import com.swingy.model.artifact.ArtifactType;
import com.swingy.model.artifact.Helm;
import com.swingy.model.artifact.Weapon;
import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroBuilder;
import com.swingy.model.hero.HeroType;

import java.util.List;

public class DbHeroRepositoryTest {

    private DbHeroRepository repository;

    @Before
    public void setUp() {
        repository = new DbHeroRepository();
        repository.saveHeroes(List.of()); // start clean
    }

    @Test
    public void saveAndLoad_heroPreservesNameAndType() {
        Hero hero = new HeroBuilder("Aria", HeroType.WARRIOR).build();
        repository.saveHeroes(List.of(hero));

        List<Hero> loaded = repository.loadHeroes();
        assertEquals(1, loaded.size());
        assertEquals("Aria", loaded.get(0).getName());
        assertEquals(HeroType.WARRIOR, loaded.get(0).getType());
    }

    @Test
    public void saveAndLoad_heroPreservesStats() {
        Hero hero = new HeroBuilder("Aria", HeroType.ROGUE)
                .level(3).xp(500).attack(15).defense(8).hitPoints(60)
                .build();
        repository.saveHeroes(List.of(hero));

        Hero loaded = repository.loadHeroes().get(0);
        assertEquals(3, loaded.getLevel());
        assertEquals(500, loaded.getXp());
        assertEquals(15, loaded.getAttack());
        assertEquals(8, loaded.getDefense());
        assertEquals(60, loaded.getHitPoints());
    }

    @Test
    public void saveAndLoad_heroPreservesEquippedArtifacts() {
        Hero hero = new HeroBuilder("Aria", HeroType.WIZARD).build();
        hero.loadArtifact(new Weapon(7));
        hero.loadArtifact(new Armor(4));
        hero.loadArtifact(new Helm(10));
        repository.saveHeroes(List.of(hero));

        Hero loaded = repository.loadHeroes().get(0);
        assertEquals(7,  loaded.getEquipped(ArtifactType.WEAPON).getValue());
        assertEquals(4,  loaded.getEquipped(ArtifactType.ARMOR).getValue());
        assertEquals(10, loaded.getEquipped(ArtifactType.HELM).getValue());
    }

    @Test
    public void saveAndLoad_noArtifacts_equippedIsNull() {
        Hero hero = new HeroBuilder("Aria", HeroType.WARRIOR).build();
        repository.saveHeroes(List.of(hero));

        Hero loaded = repository.loadHeroes().get(0);
        assertNull(loaded.getEquipped(ArtifactType.WEAPON));
        assertNull(loaded.getEquipped(ArtifactType.ARMOR));
        assertNull(loaded.getEquipped(ArtifactType.HELM));
    }

    @Test
    public void saveAndLoad_multipleHeroes() {
        Hero a = new HeroBuilder("Aria", HeroType.WARRIOR).build();
        Hero b = new HeroBuilder("Bron", HeroType.ROGUE).build();
        repository.saveHeroes(List.of(a, b));

        assertEquals(2, repository.loadHeroes().size());
    }
}
