package me.pugabyte.bncore;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.Getter;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.dailyrewards.DailyRewardsFeature;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.documentation.Documentation;
import me.pugabyte.bncore.features.holidays.Holidays;
import me.pugabyte.bncore.features.homes.HomesFeature;
import me.pugabyte.bncore.features.hours.HoursFeature;
import me.pugabyte.bncore.features.listeners.Listeners;
import me.pugabyte.bncore.features.listeners.LiteBans;
import me.pugabyte.bncore.features.mcmmo.McMMO;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.particles.Particles;
import me.pugabyte.bncore.features.quests.Quests;
import me.pugabyte.bncore.features.radar.honeypots.HoneyPots;
import me.pugabyte.bncore.features.recipes.CustomRecipes;
import me.pugabyte.bncore.features.restoreinventory.RestoreInventory;
import me.pugabyte.bncore.features.shops.Shops;
import me.pugabyte.bncore.features.store.perks.joinquit.JoinQuit;
import me.pugabyte.bncore.features.tickets.Tickets;
import me.pugabyte.bncore.features.trust.TrustFeature;
import me.pugabyte.bncore.features.votes.Votes;
import me.pugabyte.bncore.features.wiki.Wiki;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.persistence.MongoDBPersistence;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.ModelListeners;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time.Timer;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class BNCore extends JavaPlugin {
	private Commands commands;
	private static BNCore instance;
	@Getter
	private final static UUID UUID0 = new UUID(0, 0);

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

	public static boolean disableWorldEditPasting() {
		return true;
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

	public static void fileLog(String file, String message) {
		Tasks.async(() -> {
			try {
				Path path = Paths.get("plugins/BNCore/logs/" + file + ".log");
				if (!path.toFile().exists())
					path.toFile().createNewFile();
				try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
					writer.append(System.lineSeparator()).append("[").append(StringUtils.shortDateTimeFormat(LocalDateTime.now())).append("] ").append(message);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public static void csvLog(String file, String message) {
		Tasks.async(() -> {
			try {
				Path path = Paths.get("plugins/BNCore/logs/" + file + ".csv");
				if (!path.toFile().exists())
					path.toFile().createNewFile();
				try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
					writer.append(System.lineSeparator()).append(message);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
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
		try { cron.stop();									} catch (Exception ex) { ex.printStackTrace(); }
		try { Minigames.shutdown();							} catch (Exception ex) { ex.printStackTrace(); }
		try { AFK.shutdown();								} catch (Exception ex) { ex.printStackTrace(); }
		try { Discord.shutdown();							} catch (Exception ex) { ex.printStackTrace(); }
		try { LiteBans.shutdown();							} catch (Exception ex) { ex.printStackTrace(); }
		try { TrustFeature.shutdown();						} catch (Exception ex) { ex.printStackTrace(); }
		try { protocolManager.removePacketListeners(this);	} catch (Exception ex) { ex.printStackTrace(); }
		try { commands.unregisterAll();						} catch (Exception ex) { ex.printStackTrace(); }
		try { broadcastReload();							} catch (Exception ex) { ex.printStackTrace(); }
		try { Chat.shutdown();								} catch (Exception ex) { ex.printStackTrace(); }
		try { MySQLPersistence.shutdown();					} catch (Exception ex) { ex.printStackTrace(); }
		try { MongoDBPersistence.shutdown();				} catch (Exception ex) { ex.printStackTrace(); }
	}

	public void broadcastReload() {
		Stream.of("Pugabyte", "WakkaFlocka", "Blast", "lexikiq")
				.map(Bukkit::getOfflinePlayer)
				.filter(OfflinePlayer::isOnline)
				.map(OfflinePlayer::getPlayer)
				.forEach(player -> {
					GeoIP geoIp = new GeoIPService().get(player);
					if (geoIp != null && geoIp.getTimezone() != null)
						player.sendMessage(colorize("&7 " + StringUtils.shortTimeFormat(LocalDateTime.now(ZoneId.of(geoIp.getTimezone().getId()))) +
							" &c&l ! &c&l! &eReloading BNCore &c&l! &c&l!"));
					else
						player.sendMessage(colorize(" &c&l ! &c&l! &eReloading BNCore &c&l! &c&l!"));
				});
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
	public static CustomRecipes recipes;
	public static DailyRewardsFeature dailyRewards;
	public static Discord discord;
	public static Documentation documentation;
	public static Holidays holidays;
	public static HomesFeature homes;
	public static HoneyPots honeyPots;
	public static HoursFeature hours;
	public static JoinQuit joinQuit;
	public static Listeners listeners;
	public static McMMO mcmmo;
	public static ModelListeners modelListeners;
	public static Minigames minigames;
	public static Particles particles;
	public static Quests quests;
	public static RestoreInventory restoreInventory;
	public static Shops shops;
	public static Tickets tickets;
	public static TrustFeature trust;
	public static Votes votes;
	public static Wiki wiki;

	@Getter
	private static SignMenuFactory signMenuFactory;
	@Getter
	private static ProtocolManager protocolManager;
	@Getter
	private static Essentials essentials;

	@Getter
	// http://www.sauronsoftware.it/projects/cron4j/manual.php
	private static Scheduler cron = new Scheduler();

	@Getter
	private static Economy econ = null;
	@Getter
	private static Permission perms = null;
	@Getter
	private static LuckPerms luckPerms = null;

	private void enableFeatures() {
		// Load this first
		new Timer("  Discord", () -> discord = new Discord());

		new Timer("  AFK", () -> afk = new AFK());
		new Timer("  Chat", () -> chat = new Chat());
		new Timer("  CustomRecipes", () -> recipes = new CustomRecipes());
		new Timer("  DailyRewards", () -> dailyRewards = new DailyRewardsFeature());
//		new Timer("  Documentation", () -> documentation = new Documentation());
		new Timer("  Holidays", () -> holidays = new Holidays());
		new Timer("  Homes", () -> homes = new HomesFeature());
		new Timer("  HoneyPots", () -> honeyPots = new HoneyPots());
		new Timer("  Hours", () -> hours = new HoursFeature());
		new Timer("  JoinQuit", () -> joinQuit = new JoinQuit());
		new Timer("  Listeners", () -> listeners = new Listeners());
		new Timer("  McMMO", () -> mcmmo = new McMMO());
		new Timer("  ModelListeners", () -> modelListeners = new ModelListeners());
		new Timer("  Minigames", () -> minigames = new Minigames());
		new Timer("  Particles", () -> particles = new Particles());
		new Timer("  Quests", () -> quests = new Quests());
		new Timer("  RestoreInventory", () -> restoreInventory = new RestoreInventory());
		new Timer("  Shops", () -> shops = new Shops());
		new Timer("  Tickets", () -> tickets = new Tickets());
		new Timer("  Trust", () -> trust = new TrustFeature());
		new Timer("  Votes", () -> votes = new Votes());
		new Timer("  Wiki", () -> wiki = new Wiki());

		signMenuFactory = new SignMenuFactory(this);
		protocolManager = ProtocolLibrary.getProtocolManager();
		essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		cron.start();
		econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		RegisteredServiceProvider<LuckPerms> lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (lpProvider != null)
			luckPerms = lpProvider.getProvider();
	}

}