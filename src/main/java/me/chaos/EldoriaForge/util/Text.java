package me.chaos.EldoriaForge.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

/** Small Adventure helpers (non-italic text). Main thread only. */
public final class Text {

    private Text() {
    }

    public static Component plain(String text, TextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }

    public static Component gray(String text) {
        return plain(text, NamedTextColor.GRAY);
    }

    public static String time(long totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
