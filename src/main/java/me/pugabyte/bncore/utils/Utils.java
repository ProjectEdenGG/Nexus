package me.pugabyte.bncore.utils;

import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {

	public static void pug(String message) {
		Bukkit.getPlayer("Pugabyte").sendMessage(StringUtils.colorize(message));
	}

	public static void wakka(String message) {
		Bukkit.getPlayer("WakkaFlocka").sendMessage(StringUtils.colorize(message));
	}

	public static void blast(String message) {
		Bukkit.getPlayer("Blast").sendMessage(StringUtils.colorize(message));
	}

	public static void mod(String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission("group.staff"))
				player.sendMessage(StringUtils.colorize(message));
		}
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

	public static boolean isNPC(Entity entity) {
		return entity.hasMetadata("NPC");
	}

	public static List<String> getOnlineUuids() {
		return Bukkit.getOnlinePlayers().stream()
				.map(p -> p.getUniqueId().toString())
				.collect(Collectors.toList());
	}

	public static List<Player> getPlayersInWorld(World world) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(_player -> _player.getWorld() == world)
				.collect(Collectors.toList());
	}

	public static List<Player> getPlayersNear(Location location, int distance) {
		return getPlayersInWorld(location.getWorld()).stream()
				.filter(player -> player.getLocation().distance(location) <= distance)
				.collect(Collectors.toList());
	}

	public static OfflinePlayer getPlayer(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public static OfflinePlayer getPlayer(String partialName) {
		if (partialName == null || partialName.length() == 0)
			throw new InvalidInputException("No player name given");

		partialName = partialName.toLowerCase().trim();

		if (partialName.length() == 36)
			return getPlayer(UUID.fromString(partialName));
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().toLowerCase().startsWith(partialName))
				return player;
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().toLowerCase().contains((partialName)))
				return player;

		NerdService nerdService = new NerdService();

		OfflinePlayer fromNickname = nerdService.getFromNickname(partialName);
		if (fromNickname != null)
			return fromNickname;

		List<Nerd> matches = nerdService.find(partialName);
		if (matches.size() > 0) {
			Nerd nerd = matches.get(0);
			if (nerd != null && nerd.getUuid() != null)
				return nerd.getOfflinePlayer();
		}

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
		double yaw = location.getYaw();
		if (yaw < 0)
			yaw += 360; // what the fuck minecraft

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

	public static LinkedHashMap<Entity, Long> getNearbyEntities(Location location, int radius) {
		return location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
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

	public static void updatePrefix(Player player, String prefix) {
		PermissionUser user = PermissionsEx.getUser(player);
		PermissionGroup[] groups = user.getGroups();
		String rank = "";
		TEST:
		for (Rank test : Rank.values()) {
			for (PermissionGroup group : groups) {
				if (group.getName().equalsIgnoreCase(test.name())) {
					rank = test.name();
					break TEST;
				}
			}
		}
		runConsoleCommand("pex user " + player.getName() + " prefix \"" + prefix + "\"");
		runConsoleCommand("pex user " + player.getName() + " suffix \"" + Rank.valueOf(rank).getFormat() + "\"");
	}

	public static void runCommand(CommandSender sender, String command) {
		Bukkit.dispatchCommand(sender, command);
	}

	public static void runCommandAsOp(CommandSender sender, String command) {
		boolean deop = !sender.isOp();
		sender.setOp(true);
		runCommand(sender, command);
		if (deop)
			sender.setOp(false);
	}

	public static void runConsoleCommand(String command) {
		runCommand(Bukkit.getConsoleSender(), command);
	}

	public static LocalDateTime epochSecond(String timestamp) {
		return epochSecond(Long.parseLong(timestamp));
	}

	public static LocalDateTime epochSecond(long timestamp) {
		return epochMilli(timestamp * 1000);
	}

	public static LocalDateTime epochMilli(long timestamp) {
		return Instant.ofEpochMilli(timestamp)
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
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

	public static boolean isInWater(Entity entity) {
		Location location = entity.getLocation();
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

	public static List<Block> getBlocksInRadius(Location start, int radius) {
		return getBlocksInRadius(start.getBlock(), radius, radius, radius);
	}

	public static List<Block> getBlocksInRadius(Block start, int radius) {
		return getBlocksInRadius(start, radius, radius, radius);
	}

	public static List<Block> getBlocksInRadius(Block start, int xRadius, int yRadius, int zRadius) {
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

	public static void sendActionBar(final Player player, final String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.colorize(message)));
	}

	public static void sendActionBar(final Player player, final String message, int duration) {
		sendActionBar(player, message, duration, true);
	}

	public static void sendActionBar(final Player player, final String message, int duration, boolean fade) {
		sendActionBar(player, message);

		if (!fade && duration >= 0)
			Tasks.wait(duration + 1, () -> sendActionBar(player, ""));

		while (duration > 40)
			Tasks.wait(duration -= 40, () -> sendActionBar(player, message));
	}

	public static void sendActionBarToAllPlayers(String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public static void sendActionBarToAllPlayers(String message, int duration) {
		sendActionBarToAllPlayers(message, duration, true);
	}

	public static void sendActionBarToAllPlayers(String message, int duration, boolean fade) {
		for (Player player : Bukkit.getOnlinePlayers())
			sendActionBar(player, message, duration, fade);
	}

	public static int randomInt(int max) {
		return randomInt(0, max);
	}

	public static int randomInt(int min, int max) {
		if (min == max) return min;
		if (min > max) throw new InvalidInputException("Min cannot be greater than max!");
		return (int) ((Math.random() * ((max - min) + 1)) + min);
	}

	public static double randomDouble(double max) {
		return randomDouble(0, max);
	}

	public static double randomDouble(double min, double max) {
		if (min == max) return min;
		if (min > max) throw new InvalidInputException("Min cannot be greater than max!");
		return (Math.random() * ((max - min) + 1)) + min;
	}


	public static boolean chanceOf(int chance) {
		return randomInt(0, 100) <= chance;
	}

	public static <T> T getRandomElement(Object... list) {
		return getRandomElement(Arrays.asList(list));
	}

	public static <T> T getRandomElement(List list) {
		if (list.size() == 0) return null;
		return (T) list.get(new Random().nextInt(list.size()));
	}

	public static boolean isInt(String text) {
		try {
			Integer.parseInt(text);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static BlockFace getBlockFaceBetween(BlockFace face1, BlockFace face2) {
		int x = face1.getModX() + face2.getModX();
		int y = face1.getModY() + face2.getModY();
		int z = face1.getModZ() + face2.getModZ();
		for (BlockFace face : BlockFace.values())
			if (face.getModX() == x && face.getModY() == y && face.getModZ() == z)
				return face;

		return null;
	}

	public static Block getBlockStandingOn(Player player) {
		Location below = player.getLocation().add(0, -.25, 0);
		Block block = below.getBlock();
		if (block.getType().isSolid())
			return block;

		List<BlockFace> priority = new HashMap<BlockFace, Double>() {{
			put(BlockFace.NORTH, below.getZ() - Math.floor(below.getZ()));
			put(BlockFace.EAST, Math.abs(below.getX() - Math.ceil(below.getX())));
			put(BlockFace.SOUTH, Math.abs(below.getZ() - Math.ceil(below.getZ())));
			put(BlockFace.WEST, below.getX() - Math.floor(below.getX()));
		}}.entrySet().stream()
				.filter(direction -> direction.getValue() < .3)
				.sorted(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.limit(2)
				.collect(Collectors.toList());

		if (priority.size() == 2)
			priority.add(getBlockFaceBetween(priority.get(0), priority.get(1)));

		for (BlockFace blockFace : priority) {
			Block relative = block.getRelative(blockFace);
			if (relative.getType().isSolid())
				return relative;
		}

		return null;
	}

}
