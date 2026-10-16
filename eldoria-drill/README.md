# EldoriaDrill

Drill system for Eldoria (Paper 1.21+, Java 21): drill bases, fuel, upgrade slots, modules, 5x9 menu.

## Integration

Embedded in the main plugin (same pattern as the other modules), one line in `Main#onEnable`:

```java
DrillModule.init(this);          // with example content
DrillModule.init(this, false);   // without example content, register your own
```

`DrillPlugin` + `plugin.yml` exist only for running it as its own jar. The root package is `net.eldoria.drill`
(rename it with your IDE if your base package differs).

## Commands

| Command | What it does |
|---|---|
| `/drill` | Opens the drill menu (drill in main hand) |
| `/drill fuel` | Opens the refuel menu |
| `/drill admin givebase <id>` | Gives a drill base (permission `eldoria.drill.admin`) |
| `/drill admin giveupgrade <id>` | Gives an upgrade/module |
| `/drill admin setfuel <amount>` | Sets the fuel of the held drill |

## Menu layout (5 rows x 9 columns, slot = row * 9 + column)

```
col:   0   1  2  3      4       5   6  7   8
row 0  F   .  .  .    BASE      .   .  .   .
row 1  F   .  .  .      .       .   .  .  MOD1
row 2  F   .  .  HEAD ENGINE  TANK  .  .  MOD2
row 3  F   .  .  PLATE MOD_A  MOD_B .  .  MOD3
row 4  F   .  .  .   REFUEL     .   .  .   .
```

`F` = fuel bar (green = fuel left, red = used, fills from the bottom). Clicking a fuel slot or REFUEL opens the refuel menu.
Slots are defined in `DrillSlot`.

## Defining content

```java
// Base: stats + locked module slots (0-2) + accepted fuels (empty = all)
DrillBaseRegistry.register(DrillBase.builder("mithril_drill", "Mithril Drill")
        .stat(DrillStatRegistry.MAX_FUEL, 500)
        .lockModule(2)
        .build());

// Upgrade (HEAD sets the mining level; effects are used for modules/modifiers)
DrillUpgradeRegistry.register(DrillUpgrade.builder("mithril_head", "Mithril Head", UpgradeSlotType.HEAD)
        .icon(Material.NETHERITE_PICKAXE).miningLevel(5).restrictTo("mithril_drill").build());

// Fuel (any matcher works, e.g. a PDC check for custom items)
FuelTypeRegistry.register(new FuelType("magma_core", "Magma Core", stack -> isMagmaCore(stack), 400));

// Blocks that need a custom level
BlockMiningLevels.override(Material.ANCIENT_DEBRIS, DrillLevels.NETHERITE);

// New stat
DrillStatRegistry.register(new DrillStat("vein_chance", "Vein Chance", true));
```

## Rules implemented

- Stats = base + all active slots (additive). Locked module slots and upgrades not allowed on the base are ignored.
- Mining level = max(base level, head level). No head and no base level = cannot mine. A block needs the vanilla tool tier
  (stone 1, iron 2, diamond 3; everything else 0). Netherite = 4.
- 1 fuel per block, reduced by `FUEL_EFFICIENCY` (percent, max 90, negative = more). Empty drill cancels block breaking.
- Fuel is shown as the item's durability bar; drills take no tool damage.
- `MINING_SPEED` = hidden Haste level while holding a fuelled drill.
- Drill state lives in the item PDC as JSON (base id, fuel, installed upgrade items); templates stay in the registries.
- Use a NETHERITE (or DIAMOND) pickaxe as base icon so vanilla drop rules never limit the drill.

## Hooks for later systems

- `BlockTags.setResolver(block -> tagIds)`: connect the custom block tag system. Needed for `DropBoostEffect` / `LootOddsBoostEffect`.
- `DrillBreakEvent`: fired per broken block with the combined stats (`DROP_CHANCE`) and the module loot odds multipliers per tag.
- `REINFORCEMENT` stat (plates) has no behaviour yet.

## Notes

- Not compiled against real Paper jars yet: do a first build and tell me about any errors.
- Alle Texte und Item-Namen sind deutsch und inline im Code (ggf. in ein Lang-System auslagern).
