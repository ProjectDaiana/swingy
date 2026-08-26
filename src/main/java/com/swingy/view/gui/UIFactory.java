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

public class UIFactory {
    public enum Style {
        PRIMARY,
        SECONDARY,
        FIELD,
        SELECTOR
    }

    public void applyTextStyle(JLabel label, Typography.Style style) {
        label.setFont(label.getFont().deriveFont(style.weight, style.size));
        label.setForeground(style.color);
    }

    public void applyTitleStyle(JLabel label, float size) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
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
        textField.setBackground(ColorPalette.BLACK);
        // textField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
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
        selector.setBackground(ColorPalette.WHITE);
        selector.setForeground(new Color(35, 35, 35));
        selector.setFont(selector.getFont().deriveFont(Font.PLAIN, 16f));
        // }
        // default -> throw new IllegalArgumentException("Unsupported selector style: "
        // + style);
        // }
        return selector;
    }

    public static final int ICON_WIDTH = 48;
    public static final int ICON_HEIGHT = 48;

    public ImageIcon scaleIcon(ImageIcon icon) {
        Image scaled = icon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

}
