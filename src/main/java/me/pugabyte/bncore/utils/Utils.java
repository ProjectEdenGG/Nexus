package me.pugabyte.bncore.utils;

import ch.njol.skript.variables.Variables;
import com.google.common.base.Strings;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

	public static void pug(String message) {
		Bukkit.getPlayer("Pugabyte").sendMessage(colorize(message));
	}

	public static void wakka(String message) {
		Bukkit.getPlayer("WakkaFlocka").sendMessage(colorize(message));
	}

	public static String getPrefix(String prefix) {
		return colorize("&8&l[&e" + prefix + "&8&l]&3 ");
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

	public static String camelCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		return Arrays.stream(text.replaceAll("_", " ").split(" "))
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
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

	public static void callEvent(Event event) {
		BNCore.getInstance().getServer().getPluginManager().callEvent(event);
	}

	public static int wait(long delay, Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().runTaskLater(BNCore.getInstance(), runnable, delay).getTaskId();
	}

	public static int repeat(long startDelay, long interval, Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), runnable, startDelay, interval);
	}

	public static int async(Runnable runnable) {
		return BNCore.getInstance().getServer().getScheduler().runTaskAsynchronously(BNCore.getInstance(), runnable).getTaskId();
	}

	public static void cancelTask(int taskId) {
		BNCore.getInstance().getServer().getScheduler().cancelTask(taskId);
	}

	public static boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished"))
			return (meta.asBoolean());
		return false;
	}

	public static boolean isAfk(Player player) {
		return (boolean) Variables.getVariable("afk::" + player.getUniqueId().toString(), null, false);
	}

	public static boolean isAfk(Nerd nerd) {
		return isAfk(nerd.getPlayer());
	}

	public static List<String> getOnlineUuids() {
		return Bukkit.getOnlinePlayers().stream()
				.map(p -> p.getUniqueId().toString())
				.collect(Collectors.toList());
	}

	public static OfflinePlayer getPlayer(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public static OfflinePlayer getPlayer(String partialName) {
		if (partialName.length() == 0) throw new InvalidInputException("No player name given");

		partialName = partialName.toLowerCase();

		if (partialName.length() == 36)
			return getPlayer(UUID.fromString(partialName));
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().toLowerCase().startsWith(partialName))
				return player;
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().toLowerCase().contains((partialName)))
				return player;

		Nerd nerd = new NerdService().find(partialName);
		if (nerd != null && nerd.getUuid() != null)
			return nerd.getOfflinePlayer();

		throw new PlayerNotFoundException();
	}

	public static void giveItem(Player player, ItemStack item) {
		giveItems(player, Collections.singletonList(item));
	}

	public static void giveItems(Player player, List<ItemStack> items) {
		for (ItemStack item : items) {
			Map<Integer, ItemStack> excess = player.getInventory().addItem(item);
			if (!excess.isEmpty()) {
				excess.values().forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
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

	public static String timespanFormat(int seconds) {
		return timespanFormat(seconds, null);
	}

	public static String timespanFormat(int seconds, String noneDisplay) {
		if (seconds == 0 && !Strings.isNullOrEmpty(noneDisplay)) return noneDisplay;

		int original = seconds;
		int years = seconds / 60 / 60 / 24 / 365;
		seconds -= years * 60 * 60 * 24 * 365;
		int days = seconds / 60 / 60 / 24;
		seconds -= days * 60 * 60 * 24;
		int hours = seconds / 60 / 60;
		seconds -= hours * 60 * 60;
		int minutes = seconds / 60;
		seconds -= minutes * 60;

		String result = "";
		if (years > 0)
			result += years + "y ";
		if (days > 0)
			result += days + "d ";
		if (hours > 0)
			result += hours + "h ";
		if (minutes > 0)
			result += minutes + "m ";
		if (years == 0 && days == 0 && hours == 0 && minutes > 0 && seconds > 0)
			result += seconds + "s ";

		if (result.length() > 0)
			return result.trim();
		else
			return original + "s";
	}

	public static void dump(Object object) {
		List<Method> methods = Arrays.asList(object.getClass().getDeclaredMethods());
//		if (object.getClass().getSuperclass().getName().startsWith("me.pugabyte.bncore"))
//			methods.addAll(Arrays.asList(object.getClass().getSuperclass().getDeclaredMethods()));
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

	public static boolean isWater(Material material) {
		return material.equals(Material.WATER) || material.equals(Material.STATIONARY_WATER);
	}

	public static boolean isNullOrAir(ItemStack itemStack) {
		return itemStack == null || itemStack.getType().equals(Material.AIR);
	}

	public static boolean isInWater(Player player){
		Location location = player.getLocation();
		Block block = location.getBlock();
		Location locationBelow = location.subtract(0.0,1.0,0.0);
		if(isWater(block.getType())) {
			return true;
		} else if (block.getType().equals(Material.AIR) && isWater(locationBelow.getBlock().getType())) {
			return true;
		}
		return false;
	}

	public static int randomInt(int min, int max){
		if (min == max) return min;
		if (min > max) throw new InvalidInputException("Min cannot be greater than max!");
		return (int)((Math.random() * ((max - min) + 1)) + min);
	}

	public static boolean isInt(String text) {
		try{
			Integer.parseInt(text);
		} catch (Exception e){
			return false;
		}
		return true;
	}

}
