package me.chaos.EldoriaItems.Item.Obtain;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Where a specific item can be obtained from, baked directly into its {@code CustomItemTemplate}.
 * {@code detail} is a short free-text explanation, e.g. "Enderdrache" or "Amboss, 3x Eisenbarren".
 */
public record ObtainInfo(ObtainMethod method, String detail) {

    public Component toLoreLine() {
        String text = "Erhalten durch: " + method.getLabel()
                + (detail == null || detail.isBlank() ? "" : " (" + detail + ")");
        return Component.text(text)
                .color(NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, true);
    }
}
