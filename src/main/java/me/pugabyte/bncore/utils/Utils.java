package me.pugabyte.bncore.utils;

import com.google.common.base.Strings;
import com.sk89q.worldedit.math.transform.AffineTransform;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.BNCore.Env;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Utils {

	public static void puga(String message) {
		Player player = Bukkit.getPlayer("Pugabyte");
		if (player != null && player.isOnline())
			send(player, message);
	}

	public static void wakka(String message) {
		Player player = Bukkit.getPlayer("WakkaFlocka");
		if (player != null && player.isOnline())
			send(player, message);
	}

	public static void blast(String message) {
		Player player = Bukkit.getPlayer("Blast");
		if (player != null && player.isOnline())
			send(player, message);
	}

	public static void zani(String message) {
		Player player = Bukkit.getPlayer("Zanitaeni");
		if (player != null && player.isOnline())
			send(player, message);
	}

	public static Player puga() {
		return Bukkit.getPlayer("Pugabyte");
	}

	public static Player wakka() {
		return Bukkit.getPlayer("WakkaFlocka");
	}

	public static Player blast() {
		return Bukkit.getPlayer("Blast");
	}

	public static Player zani() {
		return Bukkit.getPlayer("Zanitaeni");
	}

	public static boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished"))
			return (meta.asBoolean());
		return false;
	}

	public static boolean canSee(OfflinePlayer viewer, OfflinePlayer target) {
		if (!viewer.isOnline() || !target.isOnline()) return false;
		return (canSee(viewer.getPlayer(), target.getPlayer()));
	}

	public static boolean canSee(Player viewer, Player target) {
		return !isVanished(target) || viewer.hasPermission("pv.see");
	}

	public static boolean isNPC(Entity entity) {
		return entity.hasMetadata("NPC");
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

		String original = partialName;
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

		throw new PlayerNotFoundException(original);
	}

	public static Player getNearestPlayer(Player player) {
		Player nearest = null;
		double distance = Double.MAX_VALUE;
		for (Player _player : player.getWorld().getPlayers()) {
			if (player.getLocation().getWorld() != _player.getLocation().getWorld()) continue;
			double _distance = player.getLocation().distance(_player.getLocation());
			if (_distance < distance) {
				distance = _distance;
				nearest = _player;
			}
		}
		return nearest;
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

	@NotNull
	public static List<Location> getRandomPointInCircle(World world, int radius) {
		return getRandomPointInCircle(world, radius, 0, 0);
	}

	@NotNull
	public static List<Location> getRandomPointInCircle(World world, int radius, double xOffset, double zOffset) {
		List<Location> locationList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			double angle = Math.random() * Math.PI * 2;
			double r = Math.sqrt(Math.random());
			locationList.add(new Location(world, r * Math.cos(angle) * radius, 0, r * Math.sin(angle) * radius));
		}
		return locationList;
	}

	public static EntityType getSpawnEggType(Material type) {
		return EntityType.valueOf(type.toString().split("_SPAWN_EGG")[0]);
	}

	public static Material getSpawnEgg(EntityType type) {
		return Material.valueOf(type.toString() + "_SPAWN_EGG");
	}

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sort(Map<K, V> map) {
		return map.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static LinkedHashMap<Entity, Long> getNearbyEntities(Location location, int radius) {
		return sort(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
	}

	public static LinkedHashMap<EntityType, Long> getNearbyEntityTypes(Location location, int radius) {
		return sort(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Entity::getType, Collectors.counting())));
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

	public static Block getBlockHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		BlockIterator blockIter = new BlockIterator(projectile.getWorld(), projectile.getLocation().toVector(), projectile.getVelocity().normalize(), 0, 4);
		Block blockHit = null;

		while (blockIter.hasNext()) {
			blockHit = blockIter.next();
			if (blockHit.getType() != Material.AIR) break;
		}

		return blockHit;
	}

	public static List<Block> getAdjacentBlocks(Block block) {
		Block north = block.getRelative(BlockFace.NORTH);
		Block east = block.getRelative(BlockFace.EAST);
		Block south = block.getRelative(BlockFace.SOUTH);
		Block west = block.getRelative(BlockFace.WEST);
		Block up = block.getRelative(BlockFace.UP);
		Block down = block.getRelative(BlockFace.DOWN);
		List<Block> relatives = Arrays.asList(north, east, south, west, up, down);
		List<Block> adjacent = new ArrayList<>();
		for (Block relative : relatives) {
			if (!isNullOrAir(relative))
				adjacent.add(relative);
		}
		return adjacent;
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

	public static void giveItem(Player player, Material material) {
		giveItem(player, material, 1);
	}

	public static void giveItem(Player player, Material material, String nbt) {
		giveItem(player, material, 1, nbt);
	}

	public static void giveItem(Player player, Material material, int amount) {
		giveItem(player, material, amount, null);
	}

	public static void giveItem(Player player, Material material, int amount, String nbt) {
		if (material == Material.AIR)
			throw new InvalidInputException("Cannot spawn air");

		if (amount > 64) {
			for (int i = 0; i < (amount / 64); i++)
				giveItem(player, new ItemStack(material, 64), nbt);
			giveItem(player, new ItemStack(material, amount % 64), nbt);
		} else {
			giveItem(player, new ItemStack(material, amount), nbt);
		}
	}

	public static void giveItem(Player player, ItemStack item) {
		giveItems(player, Collections.singletonList(item));
	}

	public static void giveItem(Player player, ItemStack item, String nbt) {
		giveItems(player, Collections.singletonList(item), nbt);
	}

	public static void giveItems(Player player, Collection<ItemStack> items) {
		giveItems(player, items, null);
	}

	public static void giveItems(Player player, Collection<ItemStack> items, String nbt) {
		List<ItemStack> finalItems = new ArrayList<>(items);
		if (!Strings.isNullOrEmpty(nbt)) {
			finalItems.clear();
			NBTContainer nbtContainer = new NBTContainer(nbt);
			for (ItemStack item : new ArrayList<>(items)) {
				NBTItem nbtItem = new NBTItem(item);
				nbtItem.mergeCompound(nbtContainer);
				finalItems.add(nbtItem.getItem());
			}
		}

		for (ItemStack item : finalItems) {
			Map<Integer, ItemStack> excess = player.getInventory().addItem(item);
			if (!excess.isEmpty())
				excess.values().forEach(itemStack -> player.getWorld().dropItemNaturally(player.getLocation(), itemStack));
		}
	}

	public static void runCommand(CommandSender sender, String commandNoSlash) {
//		if (sender instanceof Player)
//			Utils.callEvent(new PlayerCommandPreprocessEvent((Player) sender, "/" + command));
		Bukkit.dispatchCommand(sender, commandNoSlash);
	}

	public static void runCommandAsOp(CommandSender sender, String commandNoSlash) {
		boolean deop = !sender.isOp();
		sender.setOp(true);
		runCommand(sender, commandNoSlash);
		if (deop)
			sender.setOp(false);
	}

	public static void runCommandAsConsole(String commandNoSlash) {
		runCommand(Bukkit.getConsoleSender(), commandNoSlash);
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

	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static ItemStack getToolRequired(Player player) {
		ItemStack item = getTool(player);
		if (isNullOrAir(item))
			throw new InvalidInputException("You are not holding anything");
		return item;
	}

	public static ItemStack getTool(Player player) {
		ItemStack mainHand = player.getInventory().getItemInMainHand();
		ItemStack offHand = player.getInventory().getItemInOffHand();
		if (!isNullOrAir(mainHand))
			return mainHand;
		else if (!isNullOrAir(offHand))
			return offHand;
		return null;
	}

	public static boolean isNullOrAir(Block block) {
		return block == null || block.getType().equals(Material.AIR);
	}

	public static boolean isNullOrAir(ItemStack itemStack) {
		return itemStack == null || itemStack.getType().equals(Material.AIR);
	}

	public static boolean isNullOrAir(Material material) {
		return material == null || material.equals(Material.AIR);
	}

	public static boolean isInWater(Entity entity) {
		Location location = entity.getLocation();
		Block block = location.getBlock();
		Location locationBelow = location.subtract(0.0, 1.0, 0.0);
		if (Material.WATER.equals(block.getType())) {
			return true;
		} else if (Material.AIR.equals(block.getType()) && Material.WATER.equals(locationBelow.getBlock().getType())) {
			return true;
		}
		return false;
	}

	public static boolean isInLava(Player player) {
		Location location = player.getLocation();
		Block block = location.getBlock();
		Location locationBelow = location.subtract(0.0, 1.0, 0.0);
		if (Material.LAVA.equals(block.getType())) {
			return true;
		} else if (Material.AIR.equals(block.getType()) && Material.LAVA.equals(locationBelow.getBlock().getType())) {
			return true;
		}
		return false;
	}

	public static List<Block> getBlocksInRadius(Location start, int radius) {
		return getBlocksInRadius(start.getBlock(), radius, radius, radius);
	}

	public static List<Block> getBlocksInRadius(Location start, int xRadius, int yRadius, int zRadius) {
		return getBlocksInRadius(start.getBlock(), xRadius, yRadius, zRadius);
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
	public static BlockFace getDirection(Block from, Block to) {
		return getDirection(from.getLocation(), to.getLocation());
	}

	public static BlockFace getDirection(Location from, Location to) {
		Axis axis = Axis.getAxis(from, to);
		if (axis == null)
			throw new InvalidInputException("Locations not aligned on an axis, cannot determine direction");

		switch (axis) {
			case X:
				if ((from.getZ() - to.getZ()) > 0)
					return BlockFace.NORTH;
				else
					return BlockFace.SOUTH;
			case Y:
				if (from.getY() - to.getY() > 0)
					return BlockFace.DOWN;
				else
					return BlockFace.UP;
			case Z:
				if (from.getX() - to.getX() > 0)
					return BlockFace.WEST;
				else
					return BlockFace.EAST;
		}

		throw new InvalidInputException("Cannot determine direction");
	}

	public static void send(String UUID, String message) {
		send(getPlayer(UUID), message);
	}

	public static void send(UUID uuid, String message) {
		OfflinePlayer offlinePlayer = getPlayer(uuid);
		send(offlinePlayer, message);
	}

	public static void send(OfflinePlayer offlinePlayer, String message) {
		if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null)
			send(offlinePlayer.getPlayer(), message);
	}

	public static void send(Player player, String message) {
		if (player.isOnline())
			player.sendMessage(colorize(message));
	}

	public static void send(CommandSender sender, String message) {
		if (sender instanceof Player)
			send((Player) sender, message);
		else if (sender instanceof OfflinePlayer) {
			OfflinePlayer offlinePlayer = (OfflinePlayer) sender;
			if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null)
				send(offlinePlayer.getPlayer(), message);
		} else
			sender.sendMessage(colorize(message));
	}

	public static void send(Player player, BaseComponent... baseComponents) {
		if (player.isOnline())
			player.sendMessage(baseComponents);
	}

	public static void send(CommandSender sender, BaseComponent... baseComponents) {
		sender.sendMessage(baseComponents);
	}

	public enum EgocentricDirection {
		LEFT,
		RIGHT
	}

	public enum CardinalDirection implements IteratableEnum {
		NORTH,
		EAST,
		SOUTH,
		WEST;

		public static CardinalDirection of(BlockFace blockFace) {
			return CardinalDirection.valueOf(blockFace.name());
		}

		public static CardinalDirection random() {
			return RandomUtils.randomElement(values());
		}

		// Clockwise
		public CardinalDirection turnRight() {
			return nextWithLoop();
		}

		// Counter-clockwise
		public CardinalDirection turnLeft() {
			return previousWithLoop();
		}

		public BlockFace toBlockFace() {
			return BlockFace.valueOf(name());
		}

		public int getRotation() {
			return ordinal() * -90;
		}

		public AffineTransform getRotationTransform() {
			return new AffineTransform().rotateY(getRotation());
		}
	}

	public enum Axis {
		X,
		Y,
		Z;

		public static Axis getAxis(Location location1, Location location2) {
			if (Math.floor(location1.getX()) == Math.floor(location2.getX()) && Math.floor(location1.getZ()) == Math.floor(location2.getZ()))
				return Y;
			if (Math.floor(location1.getX()) == Math.floor(location2.getX()))
				return X;
			if (Math.floor(location1.getZ()) == Math.floor(location2.getZ()))
				return Z;

			return null;
		}
	}

	public enum MapRotation {
		DEGREE_0,
		DEGREE_90,
		DEGREE_180,
		DEGREE_270;

		public static MapRotation getRotation(Rotation rotation) {
			switch (rotation) {
				case CLOCKWISE_45:
				case FLIPPED_45:
					return DEGREE_90;
				case CLOCKWISE:
				case COUNTER_CLOCKWISE:
					return DEGREE_180;
				case CLOCKWISE_135:
				case COUNTER_CLOCKWISE_45:
					return DEGREE_270;
				default:
					return DEGREE_0;
			}
		}
	}

	public static ItemStack addGlowing(ItemStack itemStack) {
		itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public static boolean attempt(int times, BooleanSupplier to) {
		int count = 0;
		while (++count <= times)
			if (to.getAsBoolean())
				return true;
		return false;
	}

	public static final String ALPHANUMERICS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static boolean isInt(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isDouble(String text) {
		try {
			Double.parseDouble(text);
			return true;
		} catch (Exception ex) {
			return false;
		}
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

	public static HidePlayer hidePlayer(Player player) {
		return new HidePlayer(player);
	}

	public static HidePlayer hidePlayer(Minigamer minigamer) {
		return new HidePlayer(minigamer.getPlayer());
	}

	public static ShowPlayer showPlayer(Player player) {
		return new ShowPlayer(player);
	}

	public static ShowPlayer showPlayer(Minigamer minigamer) {
		return new ShowPlayer(minigamer.getPlayer());
	}

	public static class HidePlayer {
		private Player player;

		public HidePlayer(Player player) {
			this.player = player;
		}

		public void from(Minigamer minigamer) {
			from(minigamer.getPlayer());
		}

		public void from(Player player) {
			player.hidePlayer(BNCore.getInstance(), this.player);
		}
	}

	public static class ShowPlayer {
		private Player player;

		public ShowPlayer(Player player) {
			this.player = player;
		}

		public void to(Minigamer minigamer) {
			to(minigamer.getPlayer());
		}

		public void to(Player player) {
			player.showPlayer(BNCore.getInstance(), this.player);
		}
	}

	public static class RelativeLocation {

		public static Modify modify(Location location) {
			return new Modify(location);
		}

		@Data
		@Accessors(fluent = true)
		public static class Modify {
			@NonNull
			private Location location;
			private String x;
			private String y;
			private String z;
			private String yaw;
			private String pitch;

			public Modify(@NonNull Location location) {
				this.location = location;
			}

			public Location update() {
				location.setX((x.startsWith("~") ? location.getX() + trim(x) : trim(x)));
				location.setY((y.startsWith("~") ? location.getY() + trim(y) : trim(y)));
				location.setZ((z.startsWith("~") ? location.getZ() + trim(z) : trim(z)));
				location.setYaw((float) (x.startsWith("~") ? location.getYaw() + trim(yaw) : trim(yaw)));
				location.setPitch((float) (x.startsWith("~") ? location.getPitch() + trim(pitch) : trim(pitch)));
				return location;
			}
		}

		private static double trim(String string) {
			if (Strings.isNullOrEmpty(string)) return 0;
			if (Utils.isDouble(string)) return Double.parseDouble(string);
			string = StringUtils.right(string, string.length() - 1);
			if (Strings.isNullOrEmpty(string)) return 0;
			return Double.parseDouble(string);
		}
	}

	public static Location getLocation(OfflinePlayer player) {
		if (player.isOnline())
			return player.getPlayer().getLocation();

		try {
			File file = Paths.get(Bukkit.getServer().getWorlds().get(0).getName() + "/playerdata/" + player.getUniqueId().toString() + ".dat").toFile();
			if (!file.exists())
				throw new InvalidInputException("Data file does not exist");

			NBTFile nbt = new NBTFile(file);
			String world = nbt.getString("SpawnWorld");
			NBTList<Double> pos = nbt.getDoubleList("Pos");
			NBTList<Float> rotation = nbt.getFloatList("Rotation");

			if (Strings.isNullOrEmpty(world) || Bukkit.getWorld(world) == null)
				throw new InvalidInputException("Player is not in a valid world (" + world + ")");

			return new Location(Bukkit.getWorld(world), pos.get(0), pos.get(1), pos.get(2), rotation.get(0), rotation.get(1));
		} catch (Exception ex) {
			throw new InvalidInputException("Could not get location of offline player: " + ex.getMessage());
		}
	}

	public static class EnumUtils {
		public static <T> T valueOf(Class<? extends T> clazz, String value) {
			T[] values = clazz.getEnumConstants();
			for (T enumValue : values)
				if (((Enum<?>) enumValue).name().equalsIgnoreCase(value))
					return enumValue;
			throw new IllegalArgumentException();
		}

		public static <T> T next(Class<? extends T> clazz, int ordinal) {
			T[] values = clazz.getEnumConstants();
			return values[Math.min(values.length - 1, ordinal + 1 % values.length)];
		}

		public static <T> T previous(Class<? extends T> clazz, int ordinal) {
			T[] values = clazz.getEnumConstants();
			return values[Math.max(0, ordinal - 1 % values.length)];
		}

		public static <T> T nextWithLoop(Class<? extends T> clazz, int ordinal) {
			T[] values = clazz.getEnumConstants();
			int next = ordinal + 1 % values.length;
			return next >= values.length ? values[0] : values[next];
		}

		public static <T> T previousWithLoop(Class<? extends T> clazz, int ordinal) {
			T[] values = clazz.getEnumConstants();
			int previous = ordinal - 1 % values.length;
			return previous < 0 ? values[values.length - 1] : values[previous];
		}

		public static <T> List<String> valueNameList(Class<? extends T> clazz) {
			return Arrays.stream(Env.values()).map(Env::name).collect(Collectors.toList());
		}

		public static String prettyName(String name) {
			if (!name.contains("_"))
				return camelCase(name);

			List<String> words = new ArrayList<>(Arrays.asList(name.split("_")));

			String first = words.get(0);
			String last = words.get(words.size() - 1);
			words.remove(0);
			words.remove(words.size() - 1);

			StringBuilder result = new StringBuilder(camelCase(first));
			for (String word : words) {
				String character = interpolate(word);
				if (character != null)
					result.append(character);
				else if (word.toLowerCase().matches("and|for|the|a|or|of|from|in|as"))
					result.append(" ").append(word.toLowerCase());
				else
					result.append(" ").append(camelCase(word));
			}

			String character = interpolate(last);
			if (character != null)
				result.append(character);
			else
				result.append(" ").append(last.charAt(0)).append(last.substring(1).toLowerCase());
			return result.toString().trim();
		}

		private static String interpolate(String word) {
			String character = null;
			switch (word.toLowerCase()) {
				case "period":
					character = ".";
					break;
				case "excl":
					character = "!";
					break;
				case "comma":
					character = ",";
					break;
			}
			return character;
		}
	}

	public interface IteratableEnum {
		int ordinal();

		String name();

		default <T extends Enum<?>> T next() {
			return (T) EnumUtils.next(this.getClass(), ordinal());
		}

		default <T extends Enum<?>> T previous() {
			return (T) EnumUtils.previous(this.getClass(), ordinal());
		}

		default <T extends Enum<?>> T nextWithLoop() {
			return (T) EnumUtils.nextWithLoop(this.getClass(), ordinal());
		}

		default <T extends Enum<?>> T previousWithLoop() {
			return (T) EnumUtils.previousWithLoop(this.getClass(), ordinal());
		}
	}

	public static boolean equalsInvViewTitle(InventoryView view, String title) {
		String viewTitle = null;
		try {
			viewTitle = view.getTitle();
		} catch (Exception ignored) {

		}

		if (Strings.isNullOrEmpty(viewTitle))
			return false;

		return viewTitle.equals(title);

	}

}
