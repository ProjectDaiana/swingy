// com/swingy/view/gui/Typography.java
package com.swingy.view.gui;

import java.awt.Color;
import java.awt.Font;

public final class Typography {
    private Typography() {
    }

    public static final Font BASE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    public enum Style {
        TITLE(28f, Font.PLAIN, ColorPalette.LIGHT_GRAY),
        H1(24f, Font.PLAIN, ColorPalette.LIGHT_GRAY),
        H2(18f, Font.PLAIN, ColorPalette.LIGHT_GRAY),
        BODY(16f, Font.PLAIN, ColorPalette.LIGHT_GRAY),
        STAT(14f, Font.PLAIN, ColorPalette.LIGHT_GRAY),
        STAT_ACCENT(14f, Font.PLAIN, ColorPalette.ACCENT);

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