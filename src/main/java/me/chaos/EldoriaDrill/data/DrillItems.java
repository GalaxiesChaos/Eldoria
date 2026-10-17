package me.chaos.EldoriaDrill.data;

import com.google.gson.Gson;
import me.chaos.EldoriaDrill.base.DrillBase;
import me.chaos.EldoriaDrill.base.DrillBaseRegistry;
import me.chaos.EldoriaDrill.effect.DrillEffect;
import me.chaos.EldoriaDrill.mining.DrillLevels;
import me.chaos.EldoriaDrill.stat.DrillStat;
import me.chaos.EldoriaDrill.stat.DrillStatRegistry;
import me.chaos.EldoriaDrill.stat.DrillStatSet;
import me.chaos.EldoriaDrill.upgrade.DrillUpgrade;
import me.chaos.EldoriaDrill.upgrade.DrillUpgradeRegistry;
import me.chaos.EldoriaDrill.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Creates drill/upgrade items and reads/writes drill state (JSON in PDC, delta only). */
public final class DrillItems {

    private static final Gson GSON = new Gson();

    private DrillItems() {
    }

    /** JSON shape stored in the PDC. Static template data lives in the registries, not here. */
    private static final class Raw {
        String base;
        double fuel;
        Map<String, String> slots = new LinkedHashMap<>();
    }

    // ---------------------------------------------------------------- creation

    public static ItemStack createBase(DrillBase base) {
        ItemStack item = new ItemStack(base.icon());
        write(item, new DrillData(UUID.randomUUID(), base.id()));
        return item;
    }

    public static ItemStack createUpgrade(DrillUpgrade upgrade) {
        ItemStack item = new ItemStack(upgrade.icon());
        item.editMeta(meta -> {
            meta.getPersistentDataContainer().set(DrillKeys.UPGRADE, PersistentDataType.STRING, upgrade.id());
            meta.displayName(Text.plain(upgrade.displayName(), NamedTextColor.AQUA));
            List<Component> lore = new ArrayList<>();
            lore.add(Text.plain(upgrade.slotType().displayName(), NamedTextColor.DARK_AQUA));
            if (upgrade.slotType() == me.chaos.EldoriaDrill.upgrade.UpgradeSlotType.HEAD) {
                lore.add(Text.gray("Abbaustufe: ").append(Component.text(DrillLevels.name(upgrade.miningLevel()), NamedTextColor.WHITE)));
            }
            for (String line : upgrade.description()) {
                lore.add(Text.gray(line));
            }
            lore.addAll(statLines(upgrade.stats()));
            for (DrillEffect effect : upgrade.effects()) {
                lore.add(Text.plain("* " + effect.describe(), NamedTextColor.LIGHT_PURPLE));
            }
            meta.lore(lore);
        });
        return item;
    }

    // ---------------------------------------------------------------- identification

    public static UUID idOf(ItemStack item) {
        ItemMeta meta = metaOf(item);
        if (meta == null) {
            return null;
        }
        String raw = meta.getPersistentDataContainer().get(DrillKeys.ID, PersistentDataType.STRING);
        if (raw == null) {
            return null;
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static boolean isDrill(ItemStack item) {
        return idOf(item) != null;
    }

    public static Optional<DrillUpgrade> upgradeOf(ItemStack item) {
        ItemMeta meta = metaOf(item);
        if (meta == null) {
            return Optional.empty();
        }
        String id = meta.getPersistentDataContainer().get(DrillKeys.UPGRADE, PersistentDataType.STRING);
        return id == null ? Optional.empty() : DrillUpgradeRegistry.get(id);
    }

    private static ItemMeta metaOf(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        return item.getItemMeta();
    }

    // ---------------------------------------------------------------- persistence

    public static Optional<DrillData> read(ItemStack item) {
        ItemMeta meta = metaOf(item);
        if (meta == null) {
            return Optional.empty();
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String idRaw = pdc.get(DrillKeys.ID, PersistentDataType.STRING);
        String json = pdc.get(DrillKeys.DATA, PersistentDataType.STRING);
        if (idRaw == null || json == null) {
            return Optional.empty();
        }
        try {
            Raw raw = GSON.fromJson(json, Raw.class);
            DrillData data = new DrillData(UUID.fromString(idRaw), raw.base);
            data.setFuel(raw.fuel);
            if (raw.slots != null) {
                for (Map.Entry<String, String> entry : raw.slots.entrySet()) {
                    DrillSlot slot = DrillSlot.valueOf(entry.getKey());
                    data.setInstalled(slot, ItemStack.deserializeBytes(Base64.getDecoder().decode(entry.getValue())));
                }
            }
            return Optional.of(data);
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    /** Writes state, name, lore and the durability bar (= fuel level) into the item. */
    public static void write(ItemStack item, DrillData data) {
        Raw raw = new Raw();
        raw.base = data.baseId();
        raw.fuel = data.fuel();
        data.installedView().forEach((slot, stack) ->
                raw.slots.put(slot.name(), Base64.getEncoder().encodeToString(stack.serializeAsBytes())));
        String json = GSON.toJson(raw);

        Optional<DrillBase> base = DrillCalculator.base(data);
        double max = DrillCalculator.maxFuel(DrillCalculator.totalStats(data));
        List<Component> lore = buildLore(data);
        Component name = Text.plain(base.map(DrillBase::displayName).orElse("Unbekannter Bohrer"), NamedTextColor.GOLD);

        item.editMeta(meta -> {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(DrillKeys.ID, PersistentDataType.STRING, data.id().toString());
            pdc.set(DrillKeys.DATA, PersistentDataType.STRING, json);
            meta.displayName(name);
            meta.lore(lore);
            int maxDurability = item.getType().getMaxDurability();
            if (meta instanceof Damageable damageable && maxDurability > 1) {
                double ratio = Math.min(1.0, data.fuel() / max);
                damageable.setDamage((int) Math.round((1.0 - ratio) * (maxDurability - 1)));
            }
        });
    }

    // ---------------------------------------------------------------- lore

    /** Lore of the drill item; also used for the base display in the menu. */
    public static List<Component> buildLore(DrillData data) {
        DrillStatSet stats = DrillCalculator.totalStats(data);
        List<Component> lore = new ArrayList<>();
        lore.add(fuelLine(data.fuel(), DrillCalculator.maxFuel(stats)));
        lore.add(Text.gray("Abbaustufe: ")
                .append(Component.text(DrillLevels.name(DrillCalculator.miningLevel(data)), NamedTextColor.WHITE)));
        List<Component> statLines = statLines(stats);
        if (!statLines.isEmpty()) {
            lore.add(Component.empty());
            lore.addAll(statLines);
        }
        boolean header = false;
        for (DrillSlot slot : DrillSlot.values()) {
            ItemStack stack = data.installed(slot);
            Optional<DrillUpgrade> upgrade = DrillItems.upgradeOf(stack);
            if (upgrade.isEmpty()) {
                continue;
            }
            if (!header) {
                lore.add(Component.empty());
                header = true;
            }
            lore.add(Text.gray(slot.type().displayName() + ": ")
                    .append(Component.text(upgrade.get().displayName(), NamedTextColor.AQUA)));
        }
        for (DrillEffect effect : DrillCalculator.effects(data)) {
            lore.add(Text.plain("* " + effect.describe(), NamedTextColor.LIGHT_PURPLE));
        }
        return lore;
    }

    public static Component fuelLine(double fuel, double max) {
        return Text.gray("Treibstoff: ").append(fuelBar(fuel, max))
                .append(Component.text(" " + Text.num(fuel) + " / " + Text.num(max), NamedTextColor.WHITE));
    }

    public static Component fuelBar(double fuel, double max) {
        int total = 20;
        int filled = max <= 0 ? 0 : (int) Math.round(total * Math.min(1.0, fuel / max));
        if (fuel > 0 && filled == 0) {
            filled = 1;
        }
        return Component.text("|".repeat(filled), NamedTextColor.GREEN)
                .append(Component.text("|".repeat(total - filled), NamedTextColor.RED));
    }

    public static List<Component> statLines(DrillStatSet stats) {
        List<Component> lines = new ArrayList<>();
        for (DrillStat stat : DrillStatRegistry.all()) {
            double value = stats.get(stat);
            if (value == 0) {
                continue;
            }
            String text = (value > 0 ? "+" : "") + Text.num(value) + (stat.percent() ? "%" : "");
            lines.add(Text.gray(stat.displayName() + ": ")
                    .append(Component.text(text, value > 0 ? NamedTextColor.GREEN : NamedTextColor.RED)));
        }
        return lines;
    }
}
