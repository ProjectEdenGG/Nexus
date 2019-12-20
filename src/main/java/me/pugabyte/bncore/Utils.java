package me.pugabyte.bncore;

import ch.njol.skript.variables.Variables;
import com.google.common.base.Strings;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

	public static void pug(String message) {
		Bukkit.getPlayer("Pugabyte").sendMessage(message);
	}

	public static void wakka(String message) {
		Bukkit.getPlayer("WakkaFlocka").sendMessage(message);
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

	public static void wait(long delay, Runnable runnable) {
		BNCore.getInstance().getServer().getScheduler().runTaskLater(BNCore.getInstance(), runnable, delay);
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
		if (nerd != null)
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

	public static boolean isInWater(Player player){
		Location location = player.getLocation();
		Block block = location.getBlock();
		Location locationBelow = location.subtract(0.0,1.0,0.0);
		if((block.getType().equals(Material.WATER)) || (block.getType().equals(Material.STATIONARY_WATER))) {
			return true;
		} else if(block.getType().equals(Material.AIR) && locationBelow.getBlock().getType().equals(Material.WATER)) {
			return true;
		}
		return false;
	}

	public static final Map<String, Color> STR_COLORS = new HashMap<String, Color>() {{
		put("white", Color.WHITE);
		put("light gray", Color.SILVER);
		put("gray", Color.GRAY);
		put("black", Color.BLACK);
		put("brown", Color.fromRGB(139, 69, 42));
		put("red", Color.RED);
		put("orange", Color.ORANGE);
		put("yellow", Color.YELLOW);
		put("lime", Color.LIME);
		put("light green", Color.LIME);
		put("green", Color.GREEN);
		put("cyan", Color.AQUA);
		put("aqua", Color.AQUA);
		put("light blue", Color.TEAL);
		put("blue", Color.BLUE);
		put("purple", Color.PURPLE);
		put("magenta", Color.FUCHSIA);
		put("pink", Color.fromRGB(255, 105, 180));
	}};

	public static Color getColor(String color){
		if (STR_COLORS.containsKey(color)){
			return STR_COLORS.get(color);
		}
		return Color.WHITE;
	}

	public static final Map<Integer, String> INT_COLORS = new HashMap<Integer, String>() {{
		put(0, "white");
		put(8, "light gray");
		put(7, "gray");
		put(15, "black");
		put(12, "brown");
		put(14, "red");
		put(1, "orange");
		put(4, "yellow");
		put(5, "lime");
		put(13, "green");
		put(9, "cyan");
		put(3, "light blue");
		put(11, "blue");
		put(10, "purple");
		put(2, "magenta");
		put(6, "pink");
	}};

	public static String getColor(Integer color){
		if (INT_COLORS.containsKey(color)){
			return INT_COLORS.get(color);
		}
		return "white";
	}

}
