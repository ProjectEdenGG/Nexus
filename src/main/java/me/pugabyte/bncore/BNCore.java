package me.pugabyte.bncore;

import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.clearinventory.ClearInventory;
import me.pugabyte.bncore.features.connect4.Connect4;
import me.pugabyte.bncore.features.dailyrewards.DailyRewardsFeature;
import me.pugabyte.bncore.features.documentation.Documentation;
import me.pugabyte.bncore.features.durabilitywarning.DurabilityWarning;
import me.pugabyte.bncore.features.inviterewards.InviteRewards;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.oldminigames.OldMinigames;
import me.pugabyte.bncore.features.rainbowarmour.RainbowArmour;
import me.pugabyte.bncore.features.restoreinventory.RestoreInventory;
import me.pugabyte.bncore.features.showenchants.ShowEnchants;
import me.pugabyte.bncore.features.sideways.logs.SidewaysLogs;
import me.pugabyte.bncore.features.sideways.stairs.SidewaysStairs;
import me.pugabyte.bncore.features.sleep.Sleep;
import me.pugabyte.bncore.features.staff.leash.Leash;
import me.pugabyte.bncore.features.tameables.Tameables;
import me.pugabyte.bncore.features.wiki.Wiki;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.persistence.Persistence;
import me.pugabyte.bncore.models.ModelListeners;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BNCore extends JavaPlugin {
	private Commands commands = new Commands(this, "me.pugabyte.bncore.features");
	private static BNCore instance;

	public BNCore() {
		if (instance == null) {
			instance = this;
		}
	}

	public static BNCore getInstance() {
		if (instance == null) {
			log("Somethin dun fucked up");
		}
		return instance;
	}

	public static void log(String message) {
		getInstance().getLogger().info(message);
	}

	public static void warn(String message) {
		getInstance().getLogger().warning(message);
	}

	public static void severe(String message) {
		getInstance().getLogger().severe(message);
	}

	public static void registerListener(Listener listener) {
		if (getInstance().isEnabled())
			getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
		else
			log("Could not register listener " + listener.toString() + "!");
	}

	public static void registerCommand(String command, CommandExecutor executor) {
		getInstance().getCommand(command).setExecutor(executor);
	}

	public static void registerTabCompleter(String command, TabCompleter tabCompleter) {
		getInstance().getCommand(command).setTabCompleter(tabCompleter);
	}

	@Override
	public void onEnable() {
		setupConfig();
		enableFeatures();
		commands.registerAll();
	}

	@Override
	public void onDisable() {
		Persistence.shutdown();
		commands.unregisterAll();
	}

	private void setupConfig() {
		if (!BNCore.getInstance().getDataFolder().exists()) {
			BNCore.getInstance().getDataFolder().mkdir();
		}

		FileConfiguration config = getInstance().getConfig();
		config.addDefault("databases.host", "localhost");
		config.addDefault("databases.port", 3306);
		config.addDefault("databases.username", "root");
		config.addDefault("databases.password", "password");
		config.options().copyDefaults(true);
		saveConfig();
	}

	public static Chat chat;
	public static ClearInventory clearInventory;
	public static Connect4 connect4;
	public static DailyRewardsFeature dailyRewards;
	public static Documentation documentation;
	public static DurabilityWarning durabilityWarning;
	public static InviteRewards inviteRewards;
	public static Leash leash;
	public static ModelListeners modelListeners;
	public static Minigames minigames;
	public static OldMinigames oldMinigames;
	public static RainbowArmour rainbowArmour;
	public static RestoreInventory restoreInventory;
	public static ShowEnchants showEnchants;
	public static SidewaysLogs sidewaysLogs;
	public static SidewaysStairs sidewaysStairs;
	public static Sleep sleep;
	public static Tameables tameables;
	public static Wiki wiki;

	private void enableFeatures() {
		chat = new Chat();
		clearInventory = new ClearInventory();
		connect4 = new Connect4();
		dailyRewards = new DailyRewardsFeature();
		durabilityWarning = new DurabilityWarning();
		documentation = new Documentation();
		inviteRewards = new InviteRewards();
		leash = new Leash();
		modelListeners = new ModelListeners();
		minigames = new Minigames();
		oldMinigames = new OldMinigames();
		rainbowArmour = new RainbowArmour();
		restoreInventory = new RestoreInventory();
		showEnchants = new ShowEnchants();
		sidewaysLogs = new SidewaysLogs();
		sidewaysStairs = new SidewaysStairs();
		sleep = new Sleep();
		tameables = new Tameables();
		wiki = new Wiki();
	}

}