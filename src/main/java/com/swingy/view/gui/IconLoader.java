package com.swingy.view.gui;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import com.swingy.model.artifact.ArtifactType;
import com.swingy.model.hero.HeroType;

public class IconLoader {
    private static final int ICON_SIZE = 48;
    private final Map<String, ImageIcon> cache = new HashMap<>();

    private ImageIcon load(String path) {
        return cache.computeIfAbsent(path, p -> {
            URL url = IconLoader.class.getResource(p);
            if (url == null) throw new IllegalStateException("Missing required resource: " + p);
            Image scaled = new ImageIcon(url).getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        });
    }

    public ImageIcon hero(HeroType type) {
        return load("/images/h_" + type.toString().toLowerCase() + ".png");
    }

    public ImageIcon hero(String type) {
        return hero(HeroType.valueOf(type));
    }

    public ImageIcon[] allVillains() {
        return new ImageIcon[]{ load("/images/v_dragon.png"), load("/images/v_dracula.png"), load("/images/v_skeleton.png") };
    }

    public ImageIcon artifact(ArtifactType type) {
        return load("/images/a_" + type.toString().toLowerCase() + ".png");
    }

    public ImageIcon artifact(String type) {
        return artifact(ArtifactType.valueOf(type));
    }

    public ImageIcon fight(int frame) {
        return load("/images/fight_" + frame + ".png");
    }

    public ImageIcon lose() {
        return load("/images/lose.png");
    }

    public void preloadAll() {
        for (HeroType type : HeroType.values()) hero(type);
        allVillains();
        for (ArtifactType type : ArtifactType.values()) artifact(type);
        fight(1);
        fight(2);
        lose();
    }
}
