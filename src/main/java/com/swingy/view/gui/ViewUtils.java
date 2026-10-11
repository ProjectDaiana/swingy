package com.swingy.view.gui;

import com.swingy.view.HeroStats;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ViewUtils {
    private ViewUtils() {}

    private static final Map<Integer, String> ANSI_FG = new HashMap<>();
    static {
        ANSI_FG.put(93, ColorPalette.toHex(ColorPalette.ACCENT));
        ANSI_FG.put(97, ColorPalette.toHex(ColorPalette.WHITE));
    }

    public static String ansiToHtml(String ansi) {
        Pattern p = Pattern.compile("\033\\[([0-9;]*)m");
        Matcher m = p.matcher(ansi);
        StringBuilder html = new StringBuilder("<html><pre>");
        int last = 0;
        String curFg = null;

        while (m.find()) {
            if (m.start() > last) {
                String text = ansi.substring(last, m.start())
                    .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                if (curFg != null) {
                    html.append("<span style='color:").append(curFg).append(";'>").append(text).append("</span>");
                } else {
                    html.append(text);
                }
            }
            last = m.end();

            String newFg = curFg;
            for (String part : m.group(1).split(";")) {
                if (part.isEmpty()) continue;
                int code = Integer.parseInt(part);
                if (code == 0) { newFg = null; }
                else if (ANSI_FG.containsKey(code)) newFg = ANSI_FG.get(code);
            }
            curFg = newFg;
        }
        if (last < ansi.length()) html.append(ansi.substring(last));
        html.append("</pre></html>");
        return html.toString();
    }

    public static String toTitleCase(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static String statHtml(String label, Object value) {
        String labelPart = label.isEmpty() ? "" : "<font color='" + ColorPalette.toHex(ColorPalette.WHITE) + "'>" + label + " </font>";
        return "<html>" + labelPart + "<font color='" + ColorPalette.toHex(ColorPalette.ACCENT) + "'>" + value + "</font></html>";
    }

    public static String buildEquipmentText(HeroStats stats) {
        List<String> equipped = new ArrayList<>();
        if (!stats.weapon().equals("none"))
            equipped.add("Weapon " + stats.weapon());
        if (!stats.armor().equals("none"))
            equipped.add("Armor " + stats.armor());
        if (!stats.helm().equals("none"))
            equipped.add("Helm " + stats.helm());
        String value = equipped.isEmpty() ? "none" : String.join("  ·  ", equipped);
        return statHtml("Equipment", value);
    }

    public static GridBagConstraints centeredConstraints(int row, int bottomInset) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, bottomInset, 0);
        return constraints;
    }
}
