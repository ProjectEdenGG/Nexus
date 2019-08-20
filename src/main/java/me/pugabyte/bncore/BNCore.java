package me.pugabyte.bncore;

import ch.njol.skript.variables.Variables;
import me.pugabyte.bncore.features.chat.Chat;
import me.pugabyte.bncore.features.clearinventory.ClearInventory;
import me.pugabyte.bncore.features.connect4.Connect4;
import me.pugabyte.bncore.features.dailyrewards.DailyRewardsFeature;
import me.pugabyte.bncore.features.documentation.Documentation;
import me.pugabyte.bncore.features.durabilitywarning.DurabilityWarning;
import me.pugabyte.bncore.features.inviterewards.InviteRewards;
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
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.framework.persistence.Persistence;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

public class BNCore extends JavaPlugin {
	private Commands commands = new Commands(this, "me.pugabyte.bncore.features");

	public static Chat chat;
	public static ClearInventory clearInventory;
	public static Connect4 connect4;
	public static DailyRewardsFeature dailyRewards;
	public static Documentation documentation;
	public static DurabilityWarning durabilityWarning;
	public static InviteRewards inviteRewards;
	public static Leash leash;
	public static OldMinigames oldMinigames;
	public static RainbowArmour rainbowArmour;
	public static RestoreInventory restoreInventory;
	public static ShowEnchants showEnchants;
	public static SidewaysLogs sidewaysLogs;
	public static SidewaysStairs sidewaysStairs;
	public static Sleep sleep;
	public static Tameables tameables;
	public static Wiki wiki;
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

	public static String getPrefix(String prefix) {
		return "§8§l[§e" + prefix + "§8§l]§3 ";
	}

	public static String colorize(String string) {
		return string.replaceAll("&", "§");
	}

	public static String right(String string, int number) {
		return string.substring(Math.max(string.length() - number, 0));
	}

	public static String left(String string, int number) {
		return string.substring(0, number);
	}

	public static String listFirst(String string, String delimiter) {
		return string.split(delimiter)[0];
	}

	public static String listLast(String string, String delimiter) {
		return string.substring(string.lastIndexOf(delimiter) + 1);
	}

	public static String listGetAt(String string, int index, String delimiter) {
		String[] split = string.split(delimiter);
		return split[index - 1];
	}

	public static void registerListener(Listener listener) {
		if (BNCore.getInstance().isEnabled())
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

	public static void callEvent(Event event) {
		getInstance().getServer().getPluginManager().callEvent(event);
	}

	public static void wait(long delay, Runnable runnable) {
		getInstance().getServer().getScheduler().runTaskLater(BNCore.getInstance(), runnable, delay);
	}

	public static int repeat(long startDelay, long interval, Runnable runnable) {
		return getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), runnable, startDelay, interval);
	}

	public static int async(Runnable runnable) {
		return getInstance().getServer().getScheduler().runTaskAsynchronously(getInstance(), runnable).getTaskId();
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

	public static List<String> getOnlineUuids() {
		return Bukkit.getOnlinePlayers().stream()
				.map(p -> p.getUniqueId().toString())
				.collect(Collectors.toList());
	}

	public static OfflinePlayer getPlayer(String partialName) {
		if (partialName.length() == 36)
			return Bukkit.getOfflinePlayer(UUID.fromString(partialName));
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().toLowerCase().startsWith(partialName.toLowerCase()))
				return player;
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().toLowerCase().contains((partialName.toLowerCase())))
				return player;
		for (OfflinePlayer player : Bukkit.getOfflinePlayers())
			if (player.getName().toLowerCase().startsWith(partialName.toLowerCase()))
				return player;
		throw new PlayerNotFoundException();
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

	private void enableFeatures() {
		chat = new Chat();
		clearInventory = new ClearInventory();
		connect4 = new Connect4();
		dailyRewards = new DailyRewardsFeature();
		durabilityWarning = new DurabilityWarning();
		documentation = new Documentation();
		inviteRewards = new InviteRewards();
		leash = new Leash();
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