package me.chaos.EldoriaDrill.stat;

/** A stat key. Register new ones in {@link DrillStatRegistry}. */
public record DrillStat(String id, String displayName, boolean percent) {
}
