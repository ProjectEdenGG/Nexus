package me.pugabyte.nexus;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import me.pugabyte.nexus.features.listeners.LiteBans;
import me.pugabyte.nexus.features.menus.SignMenuFactory;
import me.pugabyte.nexus.framework.commands.Commands;
import me.pugabyte.nexus.framework.features.Features;
import me.pugabyte.nexus.framework.persistence.MongoDBPersistence;
import me.pugabyte.nexus.framework.persistence.MySQLPersistence;
import me.pugabyte.nexus.models.geoip.GeoIP;
import me.pugabyte.nexus.models.geoip.GeoIPService;
import me.pugabyte.nexus.models.home.HomeService;
import me.pugabyte.nexus.models.lwc.LWCProtectionService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Timer;
import me.pugabyte.nexus.utils.WorldGuardFlagUtils;
import net.buycraft.plugin.bukkit.BuycraftPluginBase;
import net.citizensnpcs.Citizens;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.util.stream.Collectors.joining;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.TimeUtils.shortDateTimeFormat;
import static me.pugabyte.nexus.utils.TimeUtils.shortTimeFormat;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

public class Nexus extends JavaPlugin {
	@Getter
	private Commands commands;
	@Getter
	private Features features;
	private static Nexus instance;
	@Getter
	private final static UUID UUID0 = new UUID(0, 0);

	public Nexus() {
		if (instance == null)
			instance = this;
		else
			Bukkit.getServer().getLogger().info("Nexus could not be initialized: Instance is not null, but is: " + instance.getClass().getName());
	}

	public static Nexus getInstance() {
		if (instance == null)
			Bukkit.getServer().getLogger().info("Nexus could not be initialized");
		return instance;
	}

	public static Env getEnv() {
		String env = getInstance().getConfig().getString("env", Env.DEV.name()).toUpperCase();
		try {
			return Env.valueOf(env);
		} catch (IllegalArgumentException ex) {
			Nexus.severe("Could not parse environment variable " + env + ", options are: " + String.join(", ", EnumUtils.valueNameList(Env.class)));
			Nexus.severe("Defaulting to " + Env.DEV.name() + " environment");
			return Env.DEV;
		}
	}

	public static boolean isUUID0(UUID uuid) {
		return UUID0.equals(uuid);
	}

	@Getter
	@Setter
	private static boolean debug = false;

	public static void debug(String message) {
		if (debug)
			getInstance().getLogger().info("[DEBUG] " + ChatColor.stripColor(message));
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
	@Getter
	private static int tempListenerCount = 0;
	@Getter
	private static final List<Class<? extends Event>> eventHandlers = new ArrayList<>();

	public static void registerTempListener(Listener listener) {
		registerListener(listener);
		++tempListenerCount;
	}

	public static void unregisterTempListener(Listener listener) {
		unregisterListener(listener);
		--tempListenerCount;
	}

	public static void registerListener(Listener listener) {
		if (getInstance().isEnabled()) {
			getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
			++listenerCount;
			for (Method method : getMethods(listener.getClass(), withAnnotation(EventHandler.class)))
				eventHandlers.add((Class<? extends Event>) method.getParameters()[0].getType());
		} else
			log("Could not register listener " + listener.toString() + "!");
	}

	public static void unregisterListener(Listener listener) {
		try {
			HandlerList.unregisterAll(listener);
			--listenerCount;
		} catch (Exception ex) {
			log("Could not unregister listener " + listener.toString() + "!");
			ex.printStackTrace();
		}
	}

	public static void registerCommand(String command, CommandExecutor executor) {
		getInstance().getCommand(command).setExecutor(executor);
	}

	public static void registerTabCompleter(String command, TabCompleter tabCompleter) {
		getInstance().getCommand(command).setTabCompleter(tabCompleter);
	}

	public static void fileLog(String file, String message) {
		Tasks.async(() -> fileLogActual(file, message));
	}

	public static void fileLogSync(String file, String message) {
		Tasks.sync(() -> fileLogActual(file, message));
	}

	public static void fileLogActual(String file, String message) {
		try {
			Path path = Paths.get("plugins/Nexus/logs/" + file + ".log");
			if (!path.toFile().exists())
				path.toFile().createNewFile();
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
				writer.append(System.lineSeparator()).append("[").append(shortDateTimeFormat(LocalDateTime.now())).append("] ").append(message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void csvLog(String file, String message) {
		Tasks.async(() -> {
			try {
				Path path = Paths.get("plugins/Nexus/logs/" + file + ".csv");
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
		File file = Paths.get("plugins/Nexus/" + path).toFile();
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
		Locale.setDefault(Locale.US);

		new Timer("Enable", () -> {
			Nexus.log("Loaded worlds: " + Bukkit.getWorlds().stream().map(World::getName).collect(joining(", ")));

			new Timer(" Config", this::setupConfig);
			new Timer(" Databases", this::databases);
			new Timer(" Hooks", this::hooks);
			new Timer(" Features", () -> {
				features = new Features(this, "me.pugabyte.nexus.features");
				features.register(Chat.class, Discord.class); // prioritize
				features.registerAll();
			});
			new Timer(" Commands", () -> {
				commands = new Commands(this, "me.pugabyte.nexus.features");
				commands.registerAll();
			});
		});
	}

	// @formatter:off
	@Override
	public void onDisable() {
		try { broadcastReload();										} catch (Throwable ex) { ex.printStackTrace(); }
		try { PlayerUtils.runCommandAsConsole("save-all");				} catch (Throwable ex) { ex.printStackTrace(); }
		try { cron.stop();												} catch (Throwable ex) { ex.printStackTrace(); }
		try { LiteBans.shutdown();										} catch (Throwable ex) { ex.printStackTrace(); }
		try { BFQuests.shutdown();										} catch (Throwable ex) { ex.printStackTrace(); }
		try { protocolManager.removePacketListeners(this);				} catch (Throwable ex) { ex.printStackTrace(); }
		try { commands.unregisterAll();									} catch (Throwable ex) { ex.printStackTrace(); }
		try { features.unregisterExcept(Discord.class, Chat.class);		} catch (Throwable ex) { ex.printStackTrace(); }
		try { features.unregisterAll();									} catch (Throwable ex) { ex.printStackTrace(); }
		try { Bukkit.getServicesManager().unregisterAll(this);			} catch (Throwable ex) { ex.printStackTrace(); }
		try { MySQLPersistence.shutdown();								} catch (Throwable ex) { ex.printStackTrace(); }
		try { MongoDBPersistence.shutdown();							} catch (Throwable ex) { ex.printStackTrace(); }
	}
	// @formatter:on;

	public void broadcastReload() {
		Rank.getOnlineStaff().stream()
				.filter(nerd -> nerd.getOfflinePlayer().isOnline() && nerd.getPlayer() != null)
				.map(Nerd::getPlayer)
				.forEach(player -> {
					GeoIP geoIp = new GeoIPService().get(player);
					String message = " &c&l ! &c&l! &eReloading Nexus &c&l! &c&l!";
					if (geoIp != null && geoIp.getTimezone() != null) {
						String timestamp = shortTimeFormat(LocalDateTime.now(ZoneId.of(geoIp.getTimezone().getId())));
						PlayerUtils.send(player, "&7 " + timestamp + message);
					} else
						PlayerUtils.send(player, message);
				});
	}

	private void setupConfig() {
		if (!Nexus.getInstance().getDataFolder().exists())
			Nexus.getInstance().getDataFolder().mkdir();

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
	private static MultiverseCore multiverseCore;
	@Getter
	private static Citizens citizens;
	@Getter
	private static BuycraftPluginBase buycraft;
	@Getter
	private static Permission perms = null;
	@Getter
	private static LuckPerms luckPerms = null;

	@Getter
	// http://www.sauronsoftware.it/projects/cron4j/manual.php
	private static Scheduler cron = new Scheduler();

	private void databases() {
		new Timer("  MySQL", LWCProtectionService::new);
		new Timer("  MongoDB", HomeService::new);
	}

	private void hooks() {
		signMenuFactory = new SignMenuFactory(this);
		protocolManager = ProtocolLibrary.getProtocolManager();
		multiverseCore = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
		citizens = (Citizens) Bukkit.getPluginManager().getPlugin("Citizens");
		buycraft = (BuycraftPluginBase) Bukkit.getServer().getPluginManager().getPlugin("BuycraftX");
		cron.start();
		perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		RegisteredServiceProvider<LuckPerms> lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (lpProvider != null)
			luckPerms = lpProvider.getProvider();
	}

}