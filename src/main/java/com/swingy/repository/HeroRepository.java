package com.swingy.repository;

import com.swingy.model.hero.Hero;
import java.util.List;

public interface HeroRepository {
    public void saveHero(Hero hero);

    public List<Hero> loadHeros();
}
