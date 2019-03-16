package me.pugabyte.bncore;

import ch.njol.skript.variables.Variables;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.pugabyte.bncore.features.antibots.AntiBots;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.chat.alerts.Alerts;
import me.pugabyte.bncore.features.clearinventory.ClearInventory;
import me.pugabyte.bncore.features.connect4.Connect4;
import me.pugabyte.bncore.features.damagetracker.DamageTracker;
import me.pugabyte.bncore.features.documentation.Documentation;
import me.pugabyte.bncore.features.durabilitywarning.DurabilityWarning;
import me.pugabyte.bncore.features.inviterewards.InviteRewards;
import me.pugabyte.bncore.features.menus.itemeditor.ItemEditor;
import me.pugabyte.bncore.features.menus.warps.Warps;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.oldminigames.OldMinigames;
import me.pugabyte.bncore.features.rainbowarmour.RainbowArmour;
import me.pugabyte.bncore.features.restoreinventory.RestoreInventory;
import me.pugabyte.bncore.features.scoreboards.Scoreboards;
import me.pugabyte.bncore.features.showenchants.ShowEnchants;
import me.pugabyte.bncore.features.sideways.logs.SidewaysLogs;
import me.pugabyte.bncore.features.sideways.stairs.SidewaysStairs;
import me.pugabyte.bncore.features.sleep.Sleep;
import me.pugabyte.bncore.features.permhelper.PermHelper;
import me.pugabyte.bncore.features.stattrack.StatTrack;
import me.pugabyte.bncore.features.tab.Tab;
import me.pugabyte.bncore.features.tameables.Tameables;
import me.pugabyte.bncore.features.wiki.Wiki;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class BNCore extends JavaPlugin {
	public static AntiBots antiBots;
	public static Chat chat;
	public static ClearInventory clearInventory;
	public static Connect4 connect4;
	public static DamageTracker damageTracker;
	public static Documentation documentation;
	public static DurabilityWarning durabilityWarning;
	public static InviteRewards inviteRewards;
	public static ItemEditor itemEditor;
	public static Minigames minigames;
	public static OldMinigames oldMinigames;
	public static PermHelper permHelper;
	public static RainbowArmour rainbowArmour;
	public static RestoreInventory restoreInventory;
	public static Scoreboards scoreboards;
	public static ShowEnchants showEnchants;
	public static SidewaysLogs sidewaysLogs;
	public static SidewaysStairs sidewaysStairs;
	public static Sleep sleep;
	public static StatTrack statTrack;
	public static Tab tab;
	public static Tameables tameables;
	public static Warps warps;
	public static Wiki wiki;
	private static BNCore instance;
	private static ProtocolManager protocolManager;

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

	public static String getPrefix(String prefix) {
		return "§8§l[§e" + prefix + "§8§l]§3 ";
	}

	public static String colorize(String string) {
		return string.replaceAll("&", "§");
	}

	public static void registerListener(Listener listener) {
		getInstance().getServer().getPluginManager().registerEvents(listener, getInstance());
	}

	public static void registerCommand(String command, CommandExecutor executor) {
		if (BNCore.getInstance().isEnabled()) {
			PluginCommand pluginCommand = getInstance().getCommand(command);
			pluginCommand.setExecutor(executor);
		} else {
			log("Could not register command /" + command + "!");
		}
	}

	public static void registerTabCompleter(String command, TabCompleter tabCompleter) {
		if (BNCore.getInstance().isEnabled()) {
			PluginCommand pluginCommand = getInstance().getCommand(command);
			pluginCommand.setTabCompleter(tabCompleter);
		} else {
			log("Could not register tab completer for command /" + command + "!");
		}
	}

	public static void callEvent(Event event) {
		getInstance().getServer().getPluginManager().callEvent(event);
	}

	public static void runTaskLater(long startDelay, Runnable runnable) {
		getInstance().getServer().getScheduler().runTaskLater(BNCore.getInstance(), runnable, startDelay);
	}

	public static int runTaskAsync(Runnable runnable) {
		return getInstance().getServer().getScheduler().runTaskAsynchronously(getInstance(), runnable).getTaskId();
	}

	public static int scheduleSyncRepeatingTask(long startDelay, long interval, Runnable runnable) {
		return getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), runnable, startDelay, interval);
	}

	public static void cancelTask(int taskId) {
		getInstance().getServer().getScheduler().cancelTask(taskId);
	}

	public static boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished"))
			return (meta.asBoolean());
		return false;
	}

	public static boolean isAfk(Player player) {
		return (boolean) Variables.getVariable("afk::" + player.getUniqueId().toString(), null, false);
	}

	public static void dump(Object object) {
		Method[] methods = object.getClass().getDeclaredMethods();
		BNCore.log("================");
		for (Method method : methods) {
			if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
				try {
					BNCore.log(method.getName() + ": " + method.invoke(object));
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getRankDisplay(Player player) {
		PermissionUser user = PermissionsEx.getUser(player);
		PermissionGroup[] ranks = user.getGroups();
		for (PermissionGroup rank : ranks) {
			return rank.getPrefix() + rank.getSuffix();
		}
		return null;
	}

	public static LocalDateTime timestamp(long timestamp) {
		return LocalDateTime.ofInstant(
				Instant.ofEpochMilli(timestamp),
				TimeZone.getDefault().toZoneId());
	}

	public static ProtocolManager getProtocolManager() {
		return protocolManager;
	}

	@Override
	public void onEnable() {
		setupConfig();
		enableFeatures();
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

	private void enableFeatures() {
		antiBots = new AntiBots();
		chat = new Chat();
		clearInventory = new ClearInventory();
		connect4 = new Connect4();
		damageTracker = new DamageTracker();
		durabilityWarning = new DurabilityWarning();
		documentation = new Documentation();
		inviteRewards = new InviteRewards();
		itemEditor = new ItemEditor();
		minigames = new Minigames();
		oldMinigames = new OldMinigames();
		permHelper = new PermHelper();
		rainbowArmour = new RainbowArmour();
		restoreInventory = new RestoreInventory();
		//scoreboards = new Scoreboards();
		showEnchants = new ShowEnchants();
		//sidewaysLogs = new SidewaysLogs();
		//sidewaysStairs = new SidewaysStairs();
		sleep = new Sleep();
		//statTrack = new StatTrack();
		//tab = new Tab();
		tameables = new Tameables();
		warps = new Warps();
		wiki = new Wiki();

		protocolManager = ProtocolLibrary.getProtocolManager();
	}

	@Override
	public void onDisable() {
		Alerts.write();
		AntiBots.write();
	}

}