package gg.projecteden.nexus;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.lishid.openinv.IOpenInv;
import com.onarandombox.MultiverseCore.MultiverseCore;
import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.features.menus.SignMenuFactory;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.framework.persistence.mysql.MySQLPersistence;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.GoogleUtils;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.api.common.utils.Utils;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.buycraft.plugin.bukkit.BuycraftPluginBase;
import net.citizensnpcs.Citizens;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Nexus extends JavaPlugin {
	@Getter
	private Commands commands;
	@Getter
	private Features features;
	private static Nexus instance;
	@Getter
	private static Thread thread;
	public static final LocalDateTime EPOCH = LocalDateTime.now();
	@Getter
	private final static HeadDatabaseAPI headAPI = new HeadDatabaseAPI();
	private static API api;
	public static final String DOMAIN = "projecteden.gg";

	public static Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

	public static <T> T singletonOf(Class<T> clazz) {
		return (T) singletons.computeIfAbsent(clazz, $ -> {
			try {
				return clazz.getConstructor().newInstance();
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
				Nexus.getInstance().getLogger().log(Level.FINE, "Failed to create singleton of " + clazz.getName() + ", falling back to Objenesis", ex);
				try {
					return new ObjenesisStd().newInstance(clazz);
				} catch (Throwable t) {
					throw new IllegalStateException("Failed to create singleton of " + clazz.getName() + " using Objenesis", t);
				}
			}
		});
	}

	static {
		Locale.setDefault(Locale.US);
	}

	public Nexus() {
		if (instance == null) {
			instance = this;
			thread = Thread.currentThread();
		} else
			Bukkit.getServer().getLogger().info("Nexus could not be initialized: Instance is not null, but is: " + instance.getClass().getName());

		api = new API();
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
			Nexus.severe("Could not parse environment variable " + env + ", options are: " + EnumUtils.valueNamesPretty(Env.class));
			Nexus.severe("Defaulting to " + Env.DEV.name() + " environment");
			return Env.DEV;
		}
	}

	@Getter
	@Setter
	private static boolean debug = false;

	public static void debug(String message) {
		if (debug)
			getInstance().getLogger().info("[DEBUG] " + ChatColor.stripColor(message));
	}

	public static void log(String message) {
		getInstance().getLogger().info(ChatColor.stripColor(message));
	}

	public static void warn(String message) {
		getInstance().getLogger().warning(ChatColor.stripColor(message));
	}

	public static void severe(String message) {
		getInstance().getLogger().severe(ChatColor.stripColor(message));
	}

	@Getter
	private static final List<Listener> listeners = new ArrayList<>();
	@Getter
	private static final List<TemporaryListener> temporaryListeners = new ArrayList<>();
	@Getter
	private static final List<Class<? extends Event>> eventHandlers = new ArrayList<>();

	public static void registerTemporaryListener(TemporaryListener listener) {
		registerListener(listener);
		temporaryListeners.add(listener);
	}

	public static void unregisterTemporaryListener(TemporaryListener listener) {
		listener.unregister();
		unregisterListener(listener);
		temporaryListeners.remove(listener);
	}

	public static void registerListener(Listener listener) {
		Nexus.debug("Registering listener: " + listener.getClass().getName());
		if (getInstance().isEnabled()) {
			getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
			listeners.add(listener);
			if (!(listener instanceof TemporaryListener))
				for (Method method : ReflectionUtils.methodsAnnotatedWith(listener.getClass(), EventHandler.class))
					eventHandlers.add((Class<? extends Event>) method.getParameters()[0].getType());
		} else
			log("Could not register listener " + listener.getClass().getName() + "!");
	}

	public static void unregisterListener(Listener listener) {
		try {
			HandlerList.unregisterAll(listener);
			listeners.remove(listener);
		} catch (Exception ex) {
			log("Could not unregister listener " + listener.toString() + "!");
			ex.printStackTrace();
		}
	}

	@Override
	public void onLoad() {
		WorldGuardFlagUtils.Flags.register();
	}

	@Override
	public void onEnable() {
		new Timer("Enable", () -> {
			new Timer(" Cache Usernames", () -> OnlinePlayers.getAll().forEach(Name::of));
			new Timer(" Config", this::setupConfig);
			new Timer(" Hooks", this::hooks);
			new Timer(" Databases", this::databases);
			new Timer(" Features", () -> {
				features = new Features(this, "gg.projecteden.nexus.features");
				features.register(Chat.class, Discord.class); // prioritize
				features.registerAll();
			});
			new Timer(" Commands", () -> {
				commands = new Commands(this, "gg.projecteden.nexus.features");
				commands.registerAll();
			});
		});
	}

	// @formatter:off
	@Override
	@SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
	public void onDisable() {
		List<Runnable> tasks = List.of(
			() -> { broadcastReload(); },
			() -> { PlayerUtils.runCommandAsConsole("save-all"); },
			() -> { if (cron.isStarted()) cron.stop(); },
			() -> { if (protocolManager != null) protocolManager.removePacketListeners(this); },
			() -> { if (commands != null) commands.unregisterAll(); },
			() -> { if (features != null) features.unregisterExcept(Discord.class, Chat.class); },
			() -> { if (features != null) features.unregister(Discord.class, Chat.class); },
			() -> { Bukkit.getServicesManager().unregisterAll(this); },
			() -> { MySQLPersistence.shutdown(); },
			() -> { GoogleUtils.shutdown(); },
			() -> { LuckPermsUtils.shutdown(); },
			() -> { if (api != null) api.shutdown(); },
			() -> { shutdownDatabases(); }
		);

		for (Runnable task : tasks)
			try {
				task.run();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
	}

	public void broadcastReload() {
		if (luckPerms == null)
			return;

		Rank.getOnlineStaff().stream()
				.map(Nerd::getPlayer)
				.forEach(player -> {
					GeoIP geoip = new GeoIPService().get(player);
					String message = " &c&l ! &c&l! &eReloading Nexus &c&l! &c&l!";
					if (GeoIP.exists(geoip))
						PlayerUtils.send(player, "&7 " + geoip.getCurrentTimeShort() + message);
					else
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
	private static IOpenInv openInv = null;

	@Getter
	// http://www.sauronsoftware.it/projects/cron4j/manual.php
	private static final Scheduler cron = new Scheduler();

	private void databases() {
//		new Timer(" MySQL", LWCProtectionService::new);
		new Timer(" MongoDB", () -> {
			Tasks.wait(5, () -> MongoService.loadServices("gg.projecteden.nexus.models"));
			new HomeService();
		});
	}

	@SneakyThrows
	private void shutdownDatabases() {
		for (Class<? extends MongoService> service : MongoService.getServices())
			if (Utils.canEnable(service))
				service.getConstructor().newInstance().clearCache();
	}

	private void hooks() {
		signMenuFactory = new SignMenuFactory(this);
		protocolManager = ProtocolLibrary.getProtocolManager();
		multiverseCore = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
		citizens = (Citizens) Bukkit.getPluginManager().getPlugin("Citizens");
		buycraft = (BuycraftPluginBase) Bukkit.getServer().getPluginManager().getPlugin("BuycraftX");
		openInv = (IOpenInv) Bukkit.getPluginManager().getPlugin("OpenInv");
		cron.start();
		perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		RegisteredServiceProvider<LuckPerms> lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
		if (lpProvider != null)
			luckPerms = lpProvider.getProvider();
	}

}
