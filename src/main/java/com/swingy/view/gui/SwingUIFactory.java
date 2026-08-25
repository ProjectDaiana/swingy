package com.swingy.view.gui;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;

public class SwingUIFactory extends UIFactory {

    @Override
    public JButton createButton(String label, Components component) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        switch (component) {
            case PRIMARY -> {
                button.setBackground(new Color(32, 99, 155));
                button.setForeground(Color.WHITE);
                button.setFont(button.getFont().deriveFont(Font.BOLD, 16f));
            }
            case SECONDARY -> {
                button.setBackground(new Color(235, 235, 235));
                button.setForeground(new Color(35, 35, 35));
                button.setFont(button.getFont().deriveFont(Font.PLAIN, 16f));
            }
            default -> throw new IllegalArgumentException("Unsupported button style: " + component);
        }
        return button;
    }

    @Override
    public JTextField createTextField(int columns, Components style) {
        JTextField textField = new JTextField(columns);
        // switch (style) {
        //     case FIELD -> {
                textField.setBackground(Color.WHITE);
                textField.setForeground(new Color(35, 35, 35));
        //         textField.setFont(textField.getFont().deriveFont(Font.PLAIN, 16f));
        //     }
        //     default -> throw new IllegalArgumentException("Unsupported text field style: " + style);
        // }
        return textField;
    }

    @Override
    public <T> JComboBox<T> createSelector(T[] values, Components style) {
        JComboBox<T> selector = new JComboBox<>(values);
        // switch (style) {
        //     case SELECTOR -> {
                selector.setBackground(Color.WHITE);
                selector.setForeground(new Color(35, 35, 35));
                selector.setFont(selector.getFont().deriveFont(Font.PLAIN, 16f));
        //     }
        //     default -> throw new IllegalArgumentException("Unsupported selector style: " + style);
        // }
        return selector;
    }
}