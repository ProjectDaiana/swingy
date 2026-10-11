package com.swingy.repository;

import com.swingy.model.hero.Hero;
import java.util.List;

public interface HeroRepository {
  public void saveHeroes(List<Hero> heroes);

  public List<Hero> loadHeroes();
}
