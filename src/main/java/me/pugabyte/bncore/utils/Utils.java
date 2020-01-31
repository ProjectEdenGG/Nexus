package me.pugabyte.bncore.utils;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.nerds.Nerd;
import me.pugabyte.bncore.models.nerds.NerdService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

	public static void blast(String message) {
		Bukkit.getPlayer("Blast").sendMessage(colorize(message));
	}

	public static String getPrefix(String prefix) {
		return colorize("&8&l[&e" + prefix + "&8&l]&3 ");
	}

	public static String colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String decolorize(String string) {
		return string.replaceAll("§", "&");
	}

	public static String loreize(String string) {
		return loreize(string, ChatColor.WHITE);
	}

	public static String loreize(String string, ChatColor color) {
		int i = 0, lineLength = 0;
		boolean watchForNewLine = false, watchForColor = false;

		for (String character : string.split("")) {
			if (watchForNewLine) {
				if ("|".equalsIgnoreCase(character))
					lineLength = 0;
				watchForNewLine = false;
			} else if ("|".equalsIgnoreCase(character))
				watchForNewLine = true;

			if (watchForColor) {
				if (character.matches("[A-Fa-fK-Ok-oRr0-9]"))
					lineLength -= 2;
				watchForColor = false;
			} else if ("&".equalsIgnoreCase(character))
				watchForColor = true;

			++lineLength;

			if (lineLength > 28)
				if (" ".equalsIgnoreCase(character)) {
					String before = left(string, i);
					String excess = right(string, string.length() - i);
					if (excess.length() > 5) {
						excess = excess.trim();
						boolean doSplit = true;
						if (excess.contains("||") && excess.indexOf("||") <= 5)
							doSplit = false;
						if (excess.contains(" ") && excess.indexOf(" ") <= 5)
							doSplit = false;
						if (lineLength >= 38)
							doSplit = true;

						if (doSplit) {
							string = before + "||" + color + excess.trim();
							lineLength = 0;
							i += 4;
						}
					}
				}

			++i;
		}

		return color + string;
	}

	public static List<String> splitLore(String lore) {
		return new ArrayList<>(Arrays.asList(lore.split("\\|\\|")));
	}

	public static String right(String string, int number) {
		return string.substring(Math.max(string.length() - number, 0));
	}

	public static String left(String string, int number) {
		return string.substring(0, Math.min(number, string.length()));
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

	public static boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished"))
			return (meta.asBoolean());
		return false;
	}

	public static boolean canSee(Player viewer, Player target) {
		return !isVanished(target) || viewer.hasPermission("vanish.see");
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
		if (partialName == null || partialName.length() == 0)
			throw new InvalidInputException("No player name given");

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

	@SneakyThrows
	public static int getPing(Player player) {
		Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
		return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
	}

	public static Location getCenteredLocation(Location location) {
		double x = Math.floor(location.getX());
		double y = Math.floor(location.getY());
		double z = Math.floor(location.getZ());
		double yaw = Math.abs(location.getYaw()); // what the fuck minecraft

		x += .5;
		z += .5;

		int newYaw = 0;
		if (yaw < 315) newYaw = 270;
		if (yaw < 225) newYaw = 180;
		if (yaw < 135) newYaw = 90;
		if (yaw < 45) newYaw = 0;

		return new Location(location.getWorld(), x, y, z, newYaw, 0F);
	}

	@Deprecated
	// The above method seems to be more accurate, but neither are 100% accurate
	// Doesn't do yaw/pitch
	public static Location getBlockCenter(Location location) {
		double x = Math.floor(location.getX());
		double y = Math.floor(location.getY());
		double z = Math.floor(location.getZ());

		x += (x >= 0) ? .5 : -.5;
		z += (z >= 0) ? .5 : -.5;

		return new Location(location.getWorld(), x, y, z);
	}

	public static <T extends Entity> T getTargetEntity(final LivingEntity entity) {
		if (entity instanceof Creature)
			return (T) ((Creature) entity).getTarget();

		T target = null;
		double targetDistanceSquared = 0;
		final double radiusSquared = 1;
		final Vector l = entity.getEyeLocation().toVector();
		final Vector n = entity.getLocation().getDirection().normalize();
		final double cos45 = Math.cos(Math.PI / 4);

		for (final T other : (List<T>) entity.getNearbyEntities(50, 50, 50)) {
			if (other == null || other == entity)
				continue;
			if (target == null || targetDistanceSquared > other.getLocation().distanceSquared(entity.getLocation())) {
				final Vector t = other.getLocation().add(0, 1, 0).toVector().subtract(l);
				if (n.clone().crossProduct(t).lengthSquared() < radiusSquared && t.normalize().dot(n) >= cos45) {
					target = other;
					targetDistanceSquared = target.getLocation().distanceSquared(entity.getLocation());
				}
			}
		}

		return target;
	}

	public static void lookAt(Player player, Location lookAt) {
		Vector direction = player.getEyeLocation().toVector().subtract(lookAt.add(0.5, 0.5, 0.5).toVector()).normalize();
		double x = direction.getX();
		double y = direction.getY();
		double z = direction.getZ();

		// Now change the angle
		Location changed = player.getLocation().clone();
		changed.setYaw(180 - toDegree(Math.atan2(x, z)));
		changed.setPitch(90 - toDegree(Math.acos(y)));
		player.teleport(changed);
	}

	private static float toDegree(double angle) {
		return (float) Math.toDegrees(angle);
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

	public static void runCommandAsOp(Player player, String command) {
		player.setOp(true);
		Bukkit.dispatchCommand(player, command);
		player.setOp(false);
	}

	public static void runConsoleCommand(String command) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public static LocalDateTime timestamp(long timestamp) {
		return LocalDateTime.ofInstant(
				Instant.ofEpochMilli(timestamp),
				TimeZone.getDefault().toZoneId());
	}

	public static String timespanDiff(LocalDateTime from) {
		return timespanDiff(from, LocalDateTime.now());
	}

	public static String timespanDiff(LocalDateTime from, LocalDateTime to) {
		return timespanFormat(from.until(to, ChronoUnit.SECONDS));
	}

	public static String timespanFormat(long seconds) {
		return timespanFormat(Long.valueOf(seconds).intValue());
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

	public static String longDateTimeFormat(LocalDateTime dateTime) {
		return longDateFormat(dateTime.toLocalDate()) + " " + timeFormat(dateTime);
	}

	public static String longDateFormat(LocalDate date) {
		return camelCase(date.getMonth().name()) + " " + getNumberSuffix(date.getDayOfMonth()) + ", " + date.getYear();
	}

	public static String shortDateFormat(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/YY");
		return date.format(formatter);
	}

	public static String timeFormat(LocalDateTime time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss a");
		return time.format(formatter);
	}

	public static String getNumberSuffix(int number) {
		String text = String.valueOf(number);
		if (text.endsWith("1"))
			if (text.endsWith("11"))
				return number + "th";
			else
				return number + "st";
		else if (text.endsWith("2"))
			if (text.endsWith("12"))
				return number + "th";
			else
				return number + "nd";
		else if (text.endsWith("3"))
			if (text.endsWith("13"))
				return number + "th";
			else
				return number + "rd";
		else
			return number + "th";
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

	public static boolean isLava(Material material) {
		return material.equals(Material.LAVA) || material.equals(Material.STATIONARY_LAVA);
	}

	public static boolean isSign(Material material) {
		return material.equals(Material.SIGN) || material.equals(Material.SIGN_POST) || material.equals(Material.WALL_SIGN);
	}

	public static boolean isNullOrAir(ItemStack itemStack) {
		return itemStack == null || itemStack.getType().equals(Material.AIR);
	}

	public static boolean isNullOrAir(Material material) {
		return material == null || material.equals(Material.AIR);
	}

	public static boolean isInWater(Player player) {
		Location location = player.getLocation();
		Block block = location.getBlock();
		Location locationBelow = location.subtract(0.0, 1.0, 0.0);
		if (isWater(block.getType())) {
			return true;
		} else if (block.getType().equals(Material.AIR) && isWater(locationBelow.getBlock().getType())) {
			return true;
		}
		return false;
	}

	public static boolean isInLava(Player player) {
		Location location = player.getLocation();
		Block block = location.getBlock();
		Location locationBelow = location.subtract(0.0, 1.0, 0.0);
		if (isLava(block.getType())) {
			return true;
		} else if (block.getType().equals(Material.AIR) && isLava(locationBelow.getBlock().getType())) {
			return true;
		}
		return false;
	}

	public static List<Block> getBlocksInRadius(Block start, int radius){
		return getBlocksInRadius(start, radius, radius, radius);
	}

	public static List<Block> getBlocksInRadius(Block start, int xRadius, int yRadius, int zRadius){
		List<Block> blocks = new ArrayList<>();
		for (int x = -xRadius; x <= xRadius; x++)
			for (int z = -zRadius; z <= zRadius; z++)
				for (int y = -yRadius; y <= yRadius; y++)
					blocks.add(start.getRelative(x, y, z));
		return blocks;
	}

	public static ItemStack addGlowing(ItemStack itemStack) {
		itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public static int randomInt(int min, int max) {
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
