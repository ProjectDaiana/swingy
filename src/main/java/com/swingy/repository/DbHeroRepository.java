package com.swingy.repository;

import com.swingy.model.artifact.Armor;
import com.swingy.model.artifact.Helm;
import com.swingy.model.artifact.Weapon;
import com.swingy.model.hero.Hero;
import com.swingy.model.hero.HeroBuilder;
import com.swingy.model.hero.HeroType;
import com.swingy.model.artifact.ArtifactType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class DbHeroRepository implements HeroRepository {

    private final SessionFactory sessionFactory;

    public DbHeroRepository() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Override
    public List<Hero> loadHeroes() {
        try (Session session = sessionFactory.openSession()) {
            List<HeroEntity> entities = session.createQuery("from HeroEntity", HeroEntity.class).list();
            return new java.util.ArrayList<>(entities.stream().map(this::toHero).toList());
        }
    }

    @Override
    public void saveHeroes(List<Hero> heroes) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from HeroEntity").executeUpdate();
            for (Hero hero : heroes) {
                session.persist(toEntity(hero));
            }
            session.getTransaction().commit();
        }
    }

    private Hero toHero(HeroEntity e) {
        Hero hero = new HeroBuilder(e.getName(), HeroType.valueOf(e.getType()))
                .level(e.getLevel())
                .xp(e.getXp())
                .attack(e.getAttack())
                .defense(e.getDefense())
                .hitPoints(e.getHitPoints())
                .build();
        if (e.getWeaponValue() != -1) hero.loadArtifact(new Weapon(e.getWeaponValue()));
        if (e.getArmorValue() != -1)  hero.loadArtifact(new Armor(e.getArmorValue()));
        if (e.getHelmValue() != -1)   hero.loadArtifact(new Helm(e.getHelmValue()));
        return hero;
    }

    private HeroEntity toEntity(Hero hero) {
        HeroEntity e = new HeroEntity();
        e.setName(hero.getName());
        e.setType(hero.getType().name());
        e.setLevel(hero.getLevel());
        e.setXp(hero.getXp());
        e.setAttack(hero.getAttack());
        e.setDefense(hero.getDefense());
        e.setHitPoints(hero.getHitPoints());
        var weapon = hero.getEquipped(ArtifactType.WEAPON);
        var armor  = hero.getEquipped(ArtifactType.ARMOR);
        var helm   = hero.getEquipped(ArtifactType.HELM);
        e.setWeaponValue(weapon != null ? weapon.getValue() : -1);
        e.setArmorValue(armor  != null ? armor.getValue()  : -1);
        e.setHelmValue(helm    != null ? helm.getValue()   : -1);
        return e;
    }
}
