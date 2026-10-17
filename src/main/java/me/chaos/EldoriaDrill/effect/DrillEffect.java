package me.chaos.EldoriaDrill.effect;

/** Behaviour attached to an upgrade/module that runs for every block the drill breaks. */
public interface DrillEffect {

    void onBlockBroken(DrillBreakContext context);

    /** One-line description shown in the item lore. */
    String describe();
}
