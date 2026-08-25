package com.swingy.view.gui;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

public abstract class UIFactory {
    public enum Style {
        PRIMARY,
        SECONDARY,
        FIELD,
        SELECTOR
    }

    public abstract JButton createButton(String label, Style style);

    public abstract JTextField createTextField(int columns, Style style);

    public abstract <T> JComboBox<T> createSelector(T[] values, Style style);

    public void applyTitleStyle(JLabel label, float size) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
    }

    public void configureScreenPanel(JPanel panel) {
        panel.removeAll();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        panel.setBackground(new Color(245, 247, 250));
    }
}