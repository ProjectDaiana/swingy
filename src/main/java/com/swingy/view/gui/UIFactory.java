package com.swingy.view.gui;

import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import com.swingy.view.HeroStats;

public class UIFactory {
    public enum Style {
        PRIMARY,
        SECONDARY,
        FIELD,
        SELECTOR
    }

    public void applyTextStyle(JLabel label, Typography.Style style) {
        label.setFont(Typography.BASE_FONT.deriveFont(style.weight, style.size));
        label.setForeground(style.color);
    }

    public void applyTitleStyle(JLabel label, float size) {
        label.setFont(Typography.BASE_FONT.deriveFont(Font.BOLD, size));
    }

    public void configureScreenPanel(JPanel panel) {
        panel.removeAll();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.setBackground(ColorPalette.BLACK);
    }

    public JButton createButton(String label, Style style) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        switch (style) {
            case PRIMARY -> {
                button.setBackground(new Color(32, 99, 155));
                button.setForeground(ColorPalette.BLACK);
                button.setFont(button.getFont().deriveFont(Font.BOLD, 16f));
            }
            case SECONDARY -> {
                button.setBackground(new Color(235, 235, 235));
                button.setForeground(ColorPalette.BLACK);
                button.setFont(button.getFont().deriveFont(Font.PLAIN, 16f));
            }
            default -> throw new IllegalArgumentException("Unsupported button style: " + style);
        }
        return button;
    }

    public JTextField createTextField(int columns, Style style) {
        JTextField textField = new JTextField(columns);
        // switch (style) {
        // case FIELD -> {
        textField.setBackground(Color.DARK_GRAY);
        textField.setBorder(BorderFactory.createLineBorder(ColorPalette.WHITE, 1));
        textField.setForeground(ColorPalette.WHITE);
        // textField.setFont(textField.getFont().deriveFont(Font.PLAIN, 16f));
        // }
        // default -> throw new IllegalArgumentException("Unsupported text field style:
        // " + style);
        // }
        return textField;
    }

    public <T> JComboBox<T> createSelector(T[] values, Style style) {
        JComboBox<T> selector = new JComboBox<>(values);
        // switch (style) {
        // case SELECTOR -> {
        selector.setBackground(ColorPalette.DARK_GRAY);
        selector.setBorder(BorderFactory.createLineBorder(ColorPalette.WHITE, 1));
        selector.setForeground(ColorPalette.BLACK);
        selector.setFont(selector.getFont().deriveFont(Font.PLAIN, 16f));
        // }
        // default -> throw new IllegalArgumentException("Unsupported selector style: "
        // + style);
        // }
        return selector;
    }

    public JPanel createHeroDetailsBar(HeroStats hero) {
        List<String> equipped = new ArrayList<>();
        if (!hero.weapon().equals("none"))
            equipped.add("Weapon " + hero.weapon());
        if (!hero.armor().equals("none"))
            equipped.add("Armor " + hero.armor());
        if (!hero.helm().equals("none"))
            equipped.add("Helm " + hero.helm());
        String equipmentText = equipped.isEmpty() ? "Equipment: none" : String.join("  ·  ", equipped);

        JLabel[] cells = {
                createStatCell("Level " + hero.level(), Typography.Style.STAT_ACCENT, true),
                createStatCell(hero.name(), true),
                createStatCell(hero.type(), true),
                createStatCell("Experience " + hero.xp(), true),
                createStatCell("Attack " + hero.attack(), true),
                createStatCell("Defense " + hero.defense(), true),
                createStatCell("Hit Points " + hero.hitPoints(), true),
                createStatCell(equipmentText, false)
        };

        JPanel bar = new JPanel(new GridLayout(1, cells.length, 0, 0));
        bar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        bar.setBackground(ColorPalette.BLACK);
        bar.setOpaque(true);
        for (JLabel cell : cells)
            bar.add(cell);

        return bar;
    }

    public JLabel createStatCell(String text, boolean withRightSeparator) {
        return createStatCell(text, Typography.Style.STAT, withRightSeparator);
    }

    public JLabel createStatCell(String text, Typography.Style style, boolean withRightSeparator) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        applyTextStyle(lbl, style);
        javax.swing.border.Border inner = BorderFactory.createEmptyBorder(4, 8, 4, 8);
        javax.swing.border.Border outer = withRightSeparator
                ? BorderFactory.createMatteBorder(0, 0, 0, 1, Color.DARK_GRAY)
                : BorderFactory.createEmptyBorder();
        lbl.setBorder(BorderFactory.createCompoundBorder(outer, inner));
        return lbl;
    }

    public static final int ICON_WIDTH = 48;
    public static final int ICON_HEIGHT = 48;

    public ImageIcon scaleIcon(ImageIcon icon) {
        Image scaled = icon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

}
