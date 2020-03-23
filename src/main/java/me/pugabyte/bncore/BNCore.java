package me.pugabyte.bncore;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chatold.ChatOld;
import me.pugabyte.bncore.features.clearinventory.ClearInventory;
import me.pugabyte.bncore.features.connect4.Connect4;
import me.pugabyte.bncore.features.dailyrewards.DailyRewardsFeature;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.documentation.Documentation;
import me.pugabyte.bncore.features.holidays.Holidays;
import me.pugabyte.bncore.features.homes.HomesFeature;
import me.pugabyte.bncore.features.honeypots.HoneyPots;
import me.pugabyte.bncore.features.hours.HoursFeature;
import me.pugabyte.bncore.features.inviterewards.InviteRewards;
import me.pugabyte.bncore.features.listeners.Listeners;
import me.pugabyte.bncore.features.listeners.Sleep;
import me.pugabyte.bncore.features.mcmmo.McMMO;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.oldminigames.OldMinigames;
import me.pugabyte.bncore.features.quests.Quests;
import me.pugabyte.bncore.features.rainbowarmour.RainbowArmour;
import me.pugabyte.bncore.features.restoreinventory.RestoreInventory;
import me.pugabyte.bncore.features.showenchants.ShowEnchants;
import me.pugabyte.bncore.features.store.perks.joinquit.JoinQuit;
import me.pugabyte.bncore.features.tameables.Tameables;
import me.pugabyte.bncore.features.tickets.Tickets;
import me.pugabyte.bncore.features.votes.Votes;
import me.pugabyte.bncore.features.wiki.Wiki;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.persistence.MongoDBPersistence;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.ModelListeners;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class BNCore extends JavaPlugin {
	private Commands commands;
	private static BNCore instance;

	public BNCore() {
		if (instance == null) {
			instance = this;
		} else {
			Bukkit.getServer().getLogger().info("BNCore could not be initialized: Instance is not null, but is: " + instance.getClass().getName());
		}
	}

	public static BNCore getInstance() {
		if (instance == null) {
			Bukkit.getServer().getLogger().info("BNCore could not be initialized");
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

	public static void registerPlaceholder(String id, Function<PlaceholderReplaceEvent, String> function) {
		PlaceholderAPI.registerPlaceholder(getInstance(), id, function::apply);
	}

	@Override
	public void onEnable() {
		setupConfig();
		enableFeatures();
		commands = new Commands(this, "me.pugabyte.bncore.features");
		commands.registerAll();
	}

	@Override
	public void onDisable() {
		Minigames.shutdown();
		AFK.shutdown();
		Discord.shutdown();
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
		MySQLPersistence.shutdown();
		MongoDBPersistence.shutdown();
		commands.unregisterAll();
	}

	private void setupConfig() {
		if (!BNCore.getInstance().getDataFolder().exists())
			BNCore.getInstance().getDataFolder().mkdir();

		FileConfiguration config = getInstance().getConfig();

		config.options().copyDefaults(true);
		saveConfig();
	}

	public void addConfigDefault(String path, Object value) {
		FileConfiguration config = getInstance().getConfig();
		config.addDefault(path, value);

		config.options().copyDefaults(true);
		saveConfig();
	}

	public static AFK afk;
	public static Chat chat;
	public static ChatOld chatOld;
	public static ClearInventory clearInventory;
	public static Connect4 connect4;
	public static DailyRewardsFeature dailyRewards;
	public static Discord discord;
	public static Documentation documentation;
	public static Holidays holidays;
	public static HomesFeature homesFeature;
	public static HoneyPots honeyPots;
	public static HoursFeature hoursFeature;
	public static InviteRewards inviteRewards;
	public static JoinQuit joinQuit;
	public static Listeners listeners;
	public static McMMO mcmmo;
	public static ModelListeners modelListeners;
	public static Minigames minigames;
	public static OldMinigames oldMinigames;
	public static Quests quests;
	public static RainbowArmour rainbowArmour;
	public static RestoreInventory restoreInventory;
	public static ShowEnchants showEnchants;
	public static Sleep sleep;
	public static Tameables tameables;
	public static Tickets tickets;
	public static Votes votes;
	public static Wiki wiki;

	@Getter
	private SignMenuFactory signMenuFactory;

	private void enableFeatures() {
		// Load this first
		discord = new Discord();

		afk = new AFK();
		chat = new Chat();
		chatOld = new ChatOld();
		clearInventory = new ClearInventory();
		connect4 = new Connect4();
		dailyRewards = new DailyRewardsFeature();
//		documentation = new Documentation();
		holidays = new Holidays();
		homesFeature = new HomesFeature();
		//honeyPots = new HoneyPots();
		hoursFeature = new HoursFeature();
		inviteRewards = new InviteRewards();
		joinQuit = new JoinQuit();
		listeners = new Listeners();
		mcmmo = new McMMO();
		modelListeners = new ModelListeners();
		minigames = new Minigames();
		oldMinigames = new OldMinigames();
		quests = new Quests();
		rainbowArmour = new RainbowArmour();
		restoreInventory = new RestoreInventory();
		showEnchants = new ShowEnchants();
		sleep = new Sleep();
		tameables = new Tameables();
		tickets = new Tickets();
		votes = new Votes();
		wiki = new Wiki();

		signMenuFactory = new SignMenuFactory(this);
	}

}