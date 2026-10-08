package me.chaos.EldoriaEnchants;

import java.util.HashMap;
import java.util.Map;

/**
 * An EventEnchant's own persistent scratch space on one specific item - e.g. a
 * cooldown timestamp, a stack counter, whatever that enchant needs to remember
 * between triggers. Backed by a plain string map so it serializes trivially
 * alongside the rest of the item's enchant data.
 * <p>
 * Everything here starts empty for a fresh enchant application; use the getters'
 * default values rather than assuming a key exists.
 */
public class EnchantVariables {

    private final Map<String, String> values;
    private boolean dirty = false;

    public EnchantVariables() {
        this(new HashMap<>());
    }

    public EnchantVariables(Map<String, String> values) {
        this.values = values;
    }

    public int getInt(String key, int def) {
        try {
            return values.containsKey(key) ? Integer.parseInt(values.get(key)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public void setInt(String key, int value) {
        values.put(key, String.valueOf(value));
        dirty = true;
    }

    public long getLong(String key, long def) {
        try {
            return values.containsKey(key) ? Long.parseLong(values.get(key)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public void setLong(String key, long value) {
        values.put(key, String.valueOf(value));
        dirty = true;
    }

    public double getDouble(String key, double def) {
        try {
            return values.containsKey(key) ? Double.parseDouble(values.get(key)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public void setDouble(String key, double value) {
        values.put(key, String.valueOf(value));
        dirty = true;
    }

    public String getString(String key, String def) {
        return values.getOrDefault(key, def);
    }

    public void setString(String key, String value) {
        values.put(key, value);
        dirty = true;
    }

    /** True if any setter has been called since this instance was loaded - lets callers skip a needless re-save. */
    public boolean isDirty() {
        return dirty;
    }

    Map<String, String> raw() {
        return values;
    }
}
