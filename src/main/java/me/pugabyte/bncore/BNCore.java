package me.pugabyte.bncore;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import com.comphenix.protocol.ProtocolLibrary;
import it.sauronsoftware.cron4j.Scheduler;
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
import me.pugabyte.bncore.features.listeners.LiteBans;
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
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Time.Timer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

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
		new Timer("Enable", () -> {
			new Timer(" Config", this::setupConfig);
			new Timer(" Features", this::enableFeatures);
			new Timer(" Commands", () -> {
				commands = new Commands(this, "me.pugabyte.bncore.features");
				commands.registerAll();
			});
		});
	}

	@Override
	public void onDisable() {
		cron.stop();
		Minigames.shutdown();
		AFK.shutdown();
		Discord.shutdown();
		LiteBans.shutdown();
		ProtocolLibrary.getProtocolManager().removePacketListeners(this);
		MySQLPersistence.shutdown();
		MongoDBPersistence.shutdown();
		commands.unregisterAll();
		broadcastReload();
	}

	public void broadcastReload() {
		Stream.of("Pugabyte", "WakkaFlocka", "Blast")
				.map(Bukkit::getOfflinePlayer)
				.filter(OfflinePlayer::isOnline)
				.map(OfflinePlayer::getPlayer)
				.forEach(player -> player.sendMessage(colorize("&7" + StringUtils.shortTimeFormat(LocalDateTime.now()) +
						" &c&l ! &c&l! &eReloading BNCore &c&l! &c&l!")));
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
	private static SignMenuFactory signMenuFactory;

	@Getter
	// http://www.sauronsoftware.it/projects/cron4j/manual.php
	private static Scheduler cron = new Scheduler();

	@Getter
	private static Economy econ = null;

	private void enableFeatures() {
		// Load this first
		new Timer("  Discord", () -> discord = new Discord());

		new Timer("  AFK", () -> afk = new AFK());
		new Timer("  Chat", () -> chat = new Chat());
		new Timer("  ChatOld", () -> chatOld = new ChatOld());
		new Timer("  ClearInventory", () -> clearInventory = new ClearInventory());
		new Timer("  Connect4", () -> connect4 = new Connect4());
		new Timer("  DailyRewardsFeature", () -> dailyRewards = new DailyRewardsFeature());
//		new Timer("  Documentation", () -> documentation = new Documentation());
		new Timer("  Holidays", () -> holidays = new Holidays());
		new Timer("  HomesFeature", () -> homesFeature = new HomesFeature());
//		new Timer("  HoneyPots", () -> honeyPots = new HoneyPots());
		new Timer("  HoursFeature", () -> hoursFeature = new HoursFeature());
		new Timer("  InviteRewards", () -> inviteRewards = new InviteRewards());
		new Timer("  JoinQuit", () -> joinQuit = new JoinQuit());
		new Timer("  Listeners", () -> listeners = new Listeners());
		new Timer("  McMMO", () -> mcmmo = new McMMO());
		new Timer("  ModelListeners", () -> modelListeners = new ModelListeners());
		new Timer("  Minigames", () -> minigames = new Minigames());
		new Timer("  OldMinigames", () -> oldMinigames = new OldMinigames());
		new Timer("  Quests", () -> quests = new Quests());
		new Timer("  RainbowArmour", () -> rainbowArmour = new RainbowArmour());
		new Timer("  RestoreInventory", () -> restoreInventory = new RestoreInventory());
		new Timer("  ShowEnchants", () -> showEnchants = new ShowEnchants());
		new Timer("  Sleep", () -> sleep = new Sleep());
		new Timer("  Tameables", () -> tameables = new Tameables());
		new Timer("  Tickets", () -> tickets = new Tickets());
		new Timer("  Votes", () -> votes = new Votes());
		new Timer("  Wiki", () -> wiki = new Wiki());

		signMenuFactory = new SignMenuFactory(this);
		cron.start();
		econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
	}

}