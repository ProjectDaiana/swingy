// com/swingy/view/gui/Typography.java
package com.swingy.view.gui;

import java.awt.Color;
import java.awt.Font;

public final class Typography {
    private Typography() {
    }

    public enum Style {
        TITLE(28f, Font.BOLD, ColorPalette.LIGHT_GRAY),
        H1(24f, Font.BOLD, ColorPalette.LIGHT_GRAY),
        H2(18f, Font.BOLD, ColorPalette.LIGHT_GRAY),
        BODY(16f, Font.PLAIN, ColorPalette.LIGHT_GRAY);

        public final float size;
        public final int weight;
        public final Color color;

        Style(float size, int weight, Color color) {
            this.size = size;
            this.weight = weight;
            this.color = color;
        }
    }
}