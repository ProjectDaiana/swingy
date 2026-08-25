package com.swingy.repository;

import com.swingy.model.hero.Hero;
import java.util.List;
import java.nio.file.Path;

public interface HeroRepository {
  public void saveHeroes(List<Hero> heroes);

  public List<Hero> loadHeroes();
}
