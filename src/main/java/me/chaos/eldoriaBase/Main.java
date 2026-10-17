package me.chaos.eldoriaBase;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.chaos.EldoriaCash.Interface.BankCommand;
import me.chaos.EldoriaCash.OnClickEvent;
import me.chaos.EldoriaCash.PlayerBank;
import me.chaos.EldoriaCash.PlayerMoney;
import me.chaos.EldoriaAntiDupe.AntiDupeModule;
import me.chaos.EldoriaCommands.CommandsModule;
import me.chaos.EldoriaJewels.JewelsModule;
import me.chaos.EldoriaRules.RulesModule;
import me.chaos.EldoriaContent.MiningContentModule;
import me.chaos.EldoriaItems.Command.EldoriaItemCommand;
import me.chaos.EldoriaItems.EldoriaItemsModule;
import me.chaos.EldoriaDrops.DropModule;
import me.chaos.EldoriaDrill.DrillModule;
import me.chaos.EldoriaNPC.NPCModule;
import me.chaos.EldoriaForge.ForgeModule;
import me.chaos.EldoriaForge.data.PlayerForgeData;
import me.chaos.EldoriaEnchants.Commands.EnchantGiveCommand;
import me.chaos.EldoriaEnchants.EnchantLoader;
import me.chaos.EldoriaEnchants.Listeners.EnchantEventListener;
import me.chaos.EldoriaEnchants.Listeners.EnchantMenuClickListener;
import me.chaos.EldoriaEnchants.Listeners.EnchantMenuCloseListener;
import me.chaos.EldoriaEnchants.Listeners.EnchantStationListener;
import me.chaos.EldoriaQuests.PlayerQuestsManager;
import me.chaos.EldoriaQuests.QuestCommand;
import me.chaos.EldoriaQuests.QuestGuiListener;
import me.chaos.EldoriaQuests.QuestListeners;
import me.chaos.eldoriaBase.Listeners.PlayerJoinListener;
import me.chaos.eldoriaBase.Listeners.PlayerQuitListener;
import me.chaos.eldoriaBase.PlayerData.PlayerDataRegistry;
import me.chaos.eldoriaBase.Stats.PlayerStatData;
import me.chaos.eldoriaBase.Stats.StatEventDispatcher;
import me.chaos.eldoriaBase.Stats.StatItemUtil;
import me.chaos.eldoriaBase.Utils.SignInputListener;
import me.chaos.eldoriaBase.WarpCommand.WarpCommand;
import me.chaos.eldoriaBase.WarpCommand.WarpPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

import java.util.List;

public final class Main extends JavaPlugin {
    private MultiverseCoreApi coreApi;
    public Handlers handler = new Handlers();

    @Override
    public void onEnable() {
        coreApi = MultiverseCoreApi.get();

        saveDefaultConfig();
        registerPlayerData();
        registerListeners();
        registerCommands();
        registerPlayerDataCodec();
        QuestLoader.loadQuests();

        // FIXED: the whole enchant system was never initialized/registered before
        EnchantLoader.loadEnchants();

        StatItemUtil.init(this);
        StatLoader.loadStats(this);
        new StatEventDispatcher(this).registerAll();

        // NEW: custom item system (templates, rarity upgrades, sets, bonus drops) - before all content
        EldoriaItemsModule.init(this);

        // NEW: drop system (block tags + loot tables + direct-to-inventory drops), needs stats loaded
        DropModule.init(this);
        // NEW: drill system (bases, fuel, upgrades, modules); example content disabled,
        // real drill content is registered by MiningContentModule below
        DrillModule.init(this, false);
        // NEW: forge system (16 stations, recipes, Nexo/Eldoria/drill items)
        ForgeModule.init(this);
        // NEW: real mining content (materials, tools, armor sets, drill parts, forge recipes, drops)
        MiningContentModule.init(this);
        // NEW: NPC system (interactable NPCs: dialogue, GUIs, teleports)
        NPCModule.init(this);
        // NEW: world commands (/farmwelt, /plotwelt, /spawn), /ec and /rtp (Multiverse worlds)
        CommandsModule.init(this);
        // NEW: jewel currency (/juwelen) + JewelService for shops/quests (registers its own PlayerData codec)
        JewelsModule.init(this);
        // NEW: rules book + rules NPC (villager with PDC tag, opens the book on right-click)
        RulesModule.init(this);

        // NEW: anti-dupe module (keep last: guards the whole plugin, incl. the menus below)
        AntiDupeModule.init(this);
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        // One shared, REGISTERED SignInputListener instance for every feature that needs sign input
        SignInputListener signInput = new SignInputListener(this);

        pm.registerEvents(new PlayerQuitListener(this), this);
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new OnClickEvent(this, signInput), this);
        pm.registerEvents(signInput, this);

        // Enchants (were completely unwired)
        pm.registerEvents(new EnchantStationListener(), this);
        pm.registerEvents(new EnchantMenuClickListener(this), this);
        pm.registerEvents(new EnchantMenuCloseListener(), this);
        pm.registerEvents(new EnchantEventListener(), this);

        // Quests (were completely unwired)
        pm.registerEvents(new QuestListeners(this), this);
        pm.registerEvents(new QuestGuiListener(handler.getQuestHandler(), handler.getPlayerDataHandler()), this);
    }

    public void registerPlayerDataCodec() {
        PlayerDataRegistry.register(WarpPlayerManager.SAVE_KEY, WarpPlayerManager.CODEC);
        PlayerDataRegistry.register(PlayerMoney.SAVE_KEY, PlayerMoney.CODEC);
        PlayerDataRegistry.register(PlayerBank.SAVE_KEY, PlayerBank.CODEC);
        PlayerDataRegistry.register(PlayerStatData.SAVE_KEY, PlayerStatData.CODEC);
        // FIXED: quest data was never registered, so quests could not persist
        PlayerDataRegistry.register(PlayerQuestsManager.SAVE_KEY, PlayerQuestsManager.CODEC);
        PlayerDataRegistry.register(PlayerForgeData.SAVE_KEY, PlayerForgeData.CODEC);
    }

    private void registerPlayerData() {
        PlayerDataRegistry.register(WarpPlayerManager::new);
        PlayerDataRegistry.register(PlayerBank::new);
        PlayerDataRegistry.register(PlayerMoney::new);
        PlayerDataRegistry.register(PlayerStatData::new);
        PlayerDataRegistry.register(PlayerQuestsManager::new);
        PlayerDataRegistry.register(PlayerForgeData::new);
    }

    private void registerCommands() {
        registerCommand("warp", new WarpCommand(this));
        registerCommand("bank", new BankCommand());
        registerCommand("quests", new QuestCommand());
        registerCommand("enchantgive", new EnchantGiveCommand());
        registerCommand("eitem", new EldoriaItemCommand());
        registerCommand("eitem", new EldoriaItemCommand());
    }

    public void registerCommand(String name, BasicCommand command) {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register(name, command));
    }

    public void registerCommand(String name, List<String> aliases, BasicCommand command) {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register(name, aliases, command));
    }

    @Override
    public void onDisable() {
        // NEW: persist placed NPC positions before data saves
        NPCModule.shutdown();
        // NEW: flush the serial-number registry (quarantine/audit trail) before data saves
        AntiDupeModule.shutdown();
        // FIXED: data was only written on quit, so a crash/reload rolled everyone back.
        // Flush every online player's data on shutdown.
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                handler.getPlayerDataHandler().saveData(player);
            } catch (Exception e) {
                getLogger().severe("Fehler beim Speichern von " + player.getName() + ": " + e.getMessage());
            }
        }
    }

    public MultiverseCoreApi getCoreApi() {
        return coreApi;
    }

    public Handlers getHandler() {
        return handler;
    }
}
