package me.chaos.EldoriaDrill.data;

import me.chaos.EldoriaDrill.upgrade.UpgradeSlotType;

/**
 * The nine installable slots and where they sit in the 5x9 menu (slot = row * 9 + column).
 * <pre>
 * col 0: fuel bar (rows 0-4)      col 4, row 0: base display
 * row 1: free
 * row 2, cols 3-5: HEAD  ENGINE  TANK
 * row 3, cols 3-5: PLATES MOD_1  MOD_2
 * col 8, rows 1-3: MODULE_1..3
 * </pre>
 */
public enum DrillSlot {
    HEAD(UpgradeSlotType.HEAD, 21, -1),
    ENGINE(UpgradeSlotType.ENGINE, 22, -1),
    TANK(UpgradeSlotType.TANK, 23, -1),
    PLATES(UpgradeSlotType.PLATES, 30, -1),
    MODIFIER_1(UpgradeSlotType.MODIFIER, 31, -1),
    MODIFIER_2(UpgradeSlotType.MODIFIER, 32, -1),
    MODULE_1(UpgradeSlotType.MODULE, 17, 0),
    MODULE_2(UpgradeSlotType.MODULE, 26, 1),
    MODULE_3(UpgradeSlotType.MODULE, 35, 2);

    private final UpgradeSlotType type;
    private final int menuSlot;
    private final int moduleIndex;

    DrillSlot(UpgradeSlotType type, int menuSlot, int moduleIndex) {
        this.type = type;
        this.menuSlot = menuSlot;
        this.moduleIndex = moduleIndex;
    }

    public UpgradeSlotType type() {
        return type;
    }

    public int menuSlot() {
        return menuSlot;
    }

    public boolean isModule() {
        return moduleIndex >= 0;
    }

    /** 0-2 for module slots, -1 otherwise. */
    public int moduleIndex() {
        return moduleIndex;
    }

    public static DrillSlot byMenuSlot(int menuSlot) {
        for (DrillSlot slot : values()) {
            if (slot.menuSlot == menuSlot) {
                return slot;
            }
        }
        return null;
    }
}
