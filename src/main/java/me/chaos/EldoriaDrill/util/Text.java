package me.chaos.EldoriaDrill.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Small Adventure helpers (non-italic text, number formatting). Main thread only. */
public final class Text {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT));

    private Text() {
    }

    public static Component plain(String text, TextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    public static Component gray(String text) {
        return plain(text, NamedTextColor.GRAY);
    }

    public static String num(double value) {
        return FORMAT.format(value);
    }
}
