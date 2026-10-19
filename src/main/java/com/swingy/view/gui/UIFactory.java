package com.swingy.view.gui;

import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import com.swingy.view.HeroStats;

public class UIFactory {
    public static final int COMPONENT_WIDTH = 260;

    public enum Style {
        PRIMARY(ColorPalette.ACCENT, ColorPalette.BLACK, Font.BOLD, 16f),
        SECONDARY(ColorPalette.LIGHT_GRAY, ColorPalette.BLACK, Font.PLAIN, 16f),
        FIELD(ColorPalette.DARK_GRAY, ColorPalette.WHITE, Font.PLAIN, 16f),
        SELECTOR(ColorPalette.DARK_GRAY, ColorPalette.WHITE, Font.PLAIN, 16f),
        TEXT(ColorPalette.ACCENT, ColorPalette.BLACK, Font.PLAIN, 13f);

        public final Color background;
        public final Color foreground;
        public final int weight;
        public final float size;

        Style(Color background, Color foreground, int weight, float size) {
            this.background = background;
            this.foreground = foreground;
            this.weight = weight;
            this.size = size;
        }
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
        button.setForeground(style.foreground);
        button.setFont(button.getFont().deriveFont(style.weight, style.size));
        if (style == Style.TEXT) {
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setForeground(style.background);
            button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            button.setMargin(new Insets(10, 20, 10, 20));
            button.setPreferredSize(new Dimension(COMPONENT_WIDTH, button.getPreferredSize().height));
            button.setBackground(style.background);
        }
        return button;
    }

    public JTextField createTextField() {
        Style style = Style.FIELD;
        JTextField textField = new JTextField(0);
        textField.setOpaque(true);
        textField.setBackground(style.background);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.WHITE, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        textField.setForeground(style.foreground);
        textField.setCaretColor(style.foreground);
        textField.setFont(textField.getFont().deriveFont(style.weight, style.size));
        textField.setPreferredSize(new Dimension(COMPONENT_WIDTH, textField.getPreferredSize().height));
        return textField;
    }

    public <T> JComboBox<T> createSelector(T[] values) {
        Style style = Style.SELECTOR;
        JComboBox<T> selector = new JComboBox<>(values);
        selector.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼");
                btn.setBackground(ColorPalette.DARK_GRAY);
                btn.setForeground(ColorPalette.WHITE);
                btn.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                btn.setFocusPainted(false);
                btn.setContentAreaFilled(false);
                return btn;
            }

            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                listBox.setBackground(ColorPalette.DARK_GRAY);
            }

        });
        selector.setBackground(style.background);
        selector.setFocusable(false);
        selector.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorPalette.WHITE, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        selector.setForeground(style.foreground);
        selector.setFont(selector.getFont().deriveFont(style.weight, style.size));
        selector.setPreferredSize(new Dimension(COMPONENT_WIDTH, selector.getPreferredSize().height));

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
                createStatCell("Level " + hero.level(), Typography.Style.STAT_ACCENT),
                createStatCell(hero.name()),
                createStatCell(hero.type()),
                createStatCell("Experience " + hero.xp()),
                createStatCell("Attack " + hero.attack()),
                createStatCell("Defense " + hero.defense()),
                createStatCell("Hit Points " + hero.hitPoints()),
                createStatCell(equipmentText)
        };

        JPanel bar = new JPanel(new GridLayout(1, cells.length, 1, 0));
        bar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        bar.setBackground(ColorPalette.DARK_GRAY);
        bar.setOpaque(true);
        for (JLabel cell : cells)
            bar.add(cell);

        return bar;
    }

    public JLabel createStatCell(String text) {
        return createStatCell(text, Typography.Style.STAT);
    }

    public JLabel createStatCell(String text, Typography.Style style) {
        JLabel lbl = new JLabel(text, JLabel.LEFT);
        applyTextStyle(lbl, style);
        lbl.setOpaque(true);
        lbl.setBackground(ColorPalette.BLACK);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return lbl;
    }

    public static final int ICON_WIDTH = 48;
    public static final int ICON_HEIGHT = 48;

    public ImageIcon scaleIcon(ImageIcon icon) {
        Image scaled = icon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

}
