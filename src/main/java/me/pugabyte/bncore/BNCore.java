package me.pugabyte.bncore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.features.listeners.LiteBans;
import me.pugabyte.bncore.features.menus.SignMenuFactory;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.features.Features;
import me.pugabyte.bncore.framework.persistence.MongoDBPersistence;
import me.pugabyte.bncore.framework.persistence.MySQLPersistence;
import me.pugabyte.bncore.models.geoip.GeoIP;
import me.pugabyte.bncore.models.geoip.GeoIPService;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.models.nerd.Rank;
import me.pugabyte.bncore.utils.Env;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.EnumUtils;
import me.pugabyte.bncore.utils.WorldGuardFlagUtils;
import net.buycraft.plugin.bukkit.BuycraftPluginBase;
import net.citizensnpcs.Citizens;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class BNCore extends JavaPlugin {
	private Commands commands;
	private Features features;
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

	public static Env getEnv() {
		String env = getInstance().getConfig().getString("env", Env.DEV.name()).toUpperCase();
		try {
			return Env.valueOf(env);
		} catch (IllegalArgumentException ex) {
			BNCore.severe("Could not parse environment variable " + env + ", options are: " + String.join(", ", EnumUtils.valueNameList(Env.class)));
			BNCore.severe("Defaulting to " + Env.DEV.name() + " environment");
			return Env.DEV;
		}
	}

	public static void log(String message) {
		getInstance().getLogger().info(stripColor(message));
	}

	public static void warn(String message) {
		getInstance().getLogger().warning(stripColor(message));
	}

	public static void severe(String message) {
		getInstance().getLogger().severe(stripColor(message));
	}

	@Getter
	private static int listenerCount = 0;

	public static void registerListener(Listener listener) {
		if (getInstance().isEnabled()) {
			getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
			++listenerCount;
		} else
			log("Could not register listener " + listener.toString() + "!");
	}

	public static void registerCommand(String command, CommandExecutor executor) {
		getInstance().getCommand(command).setExecutor(executor);
	}

	public static void registerTabCompleter(String command, TabCompleter tabCompleter) {
		getInstance().getCommand(command).setTabCompleter(tabCompleter);
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

	@SneakyThrows
	public static File getFile(String path) {
		File file = Paths.get("plugins/BNCore/" + path).toFile();
		if (!file.exists()) file.createNewFile();
		return file;
	}

	@SneakyThrows
	public static YamlConfiguration getConfig(String path) {
		return YamlConfiguration.loadConfiguration(getFile(path));
	}

	@Override
	public void onLoad() {
		WorldGuardFlagUtils.Flags.register();
	}

	@Override
	public void onEnable() {
		new Timer("Enable", () -> {
			new Timer(" Config", this::setupConfig);
			new Timer(" Databases", this::databases);
			new Timer(" Hooks", this::hooks);
			new Timer(" Features", () -> {
				features = new Features(this, "me.pugabyte.bncore.features");
				features.register(Chat.class, Discord.class); // prioritize
				features.registerAll();
			});
			new Timer(" Commands", () -> {
				commands = new Commands(this, "me.pugabyte.bncore.features");
				commands.registerAll();
			});
		});
	}

	// @formatter:off
	@Override
	public void onDisable() {
		try { broadcastReload();										} catch (Throwable ex) { ex.printStackTrace(); }
		try { Utils.runCommandAsConsole("save-all");					} catch (Throwable ex) { ex.printStackTrace(); }
		try { cron.stop();												} catch (Throwable ex) { ex.printStackTrace(); }
		try { LiteBans.shutdown();										} catch (Throwable ex) { ex.printStackTrace(); }
		try { BFQuests.shutdown();										} catch (Throwable ex) { ex.printStackTrace(); }
		try { protocolManager.removePacketListeners(this);				} catch (Throwable ex) { ex.printStackTrace(); }
		try { commands.unregisterAll();									} catch (Throwable ex) { ex.printStackTrace(); }
		try { features.unregisterExcept(Discord.class, Chat.class);		} catch (Throwable ex) { ex.printStackTrace(); }
		try { features.unregister(Discord.class, Chat.class);			} catch (Throwable ex) { ex.printStackTrace(); }
		try { MySQLPersistence.shutdown();								} catch (Throwable ex) { ex.printStackTrace(); }
		try { MongoDBPersistence.shutdown();							} catch (Throwable ex) { ex.printStackTrace(); }
	}
	// @formatter:on;

	public void broadcastReload() {
		Rank.getOnlineMods().stream()
				.filter(nerd -> nerd.getOfflinePlayer().isOnline() && nerd.getPlayer() != null)
				.map(Nerd::getPlayer)
				.forEach(player -> {
					GeoIP geoIp = new GeoIPService().get(player);
					String message = " &c&l ! &c&l! &eReloading BNCore &c&l! &c&l!";
					if (geoIp != null && geoIp.getTimezone() != null) {
						String timestamp = StringUtils.shortTimeFormat(LocalDateTime.now(ZoneId.of(geoIp.getTimezone().getId())));
						Utils.send(player, "&7 " + timestamp + message);
					} else
						Utils.send(player, message);
				});
	}

	private void setupConfig() {
		if (!BNCore.getInstance().getDataFolder().exists())
			BNCore.getInstance().getDataFolder().mkdir();

		FileConfiguration config = getInstance().getConfig();

		addConfigDefault("env", "dev");

		config.options().copyDefaults(true);
		saveConfig();
	}

	public void addConfigDefault(String path, Object value) {
		FileConfiguration config = getInstance().getConfig();
		config.addDefault(path, value);

		config.options().copyDefaults(true);
		saveConfig();
	}

	@Getter
	private static SignMenuFactory signMenuFactory;
	@Getter
	private static ProtocolManager protocolManager;
	@Getter
	private static Essentials essentials;
	@Getter
	private static Citizens citizens;
	@Getter
	private static BuycraftPluginBase buycraft;
	@Getter
	private static Economy econ = null;
	@Getter
	private static Permission perms = null;
	@Getter
	private static LuckPerms luckPerms = null;

	@Getter
	// http://www.sauronsoftware.it/projects/cron4j/manual.php
	private static Scheduler cron = new Scheduler();

	private void databases() {
		new Timer("  MySQL", NerdService::new);
		new Timer("  MongoDB", HomeService::new);
	}

	private void hooks() {
		signMenuFactory = new SignMenuFactory(this);
		protocolManager = ProtocolLibrary.getProtocolManager();
		essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		citizens = (Citizens) Bukkit.getPluginManager().getPlugin("Citizens");
		buycraft = (BuycraftPluginBase) Bukkit.getServer().getPluginManager().getPlugin("BuycraftX");
		cron.start();
		econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		RegisteredServiceProvider<LuckPerms> lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (lpProvider != null)
			luckPerms = lpProvider.getProvider();
	}

}