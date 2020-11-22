package me.pugabyte.nexus.utils;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.framework.annotations.Disabled;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

public class Utils {

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

	public static Player vroom() {
		return Bukkit.getPlayer("Camaros");
	}

	public static Player lexi() {
		return Bukkit.getPlayer("lexikiq");
	}

	public static void puga(String message) {
		send(puga(), message);
	}

	public static void wakka(String message) {
		send(wakka(), message);
	}

	public static void blast(String message) {
		send(blast(), message);
	}

	public static void zani(String message) {
		send(zani(), message);
	}

	public static void vroom(String message){
		send(vroom(), message);
	}

	public static void lexi(String message){
		send(lexi(), message);
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

	public static EntityType getSpawnEggType(Material type) {
		return EntityType.valueOf(type.toString().split("_SPAWN_EGG")[0]);
	}

	public static Material getSpawnEgg(EntityType type) {
		return Material.valueOf(type.toString() + "_SPAWN_EGG");
	}

	public static <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sortByKey(Map<K, V> map) {
		return collect(map.entrySet().stream().sorted(Entry.comparingByKey()));
	}

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
		return collect(map.entrySet().stream().sorted(Entry.comparingByValue()));
	}

	public static <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sortByKeyReverse(Map<K, V> map) {
		return reverse(sortByKey(map));
	}

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValueReverse(Map<K, V> map) {
		return reverse(sortByValue(map));
	}

	public static <K, V> LinkedHashMap<K, V> collect(Stream<Entry<K, V>> stream) {
		return stream.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static <K, V> LinkedHashMap<K, V> reverse(LinkedHashMap<K, V> sorted) {
		LinkedHashMap<K, V> reverse = new LinkedHashMap<>();
		List<K> keys = new ArrayList<>(sorted.keySet());
		Collections.reverse(keys);
		keys.forEach(key -> reverse.put(key, sorted.get(key)));
		return reverse;
	}

	public static LinkedHashMap<Entity, Long> getNearbyEntities(Location location, int radius) {
		return sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
	}

	public static LinkedHashMap<EntityType, Long> getNearbyEntityTypes(Location location, int radius) {
		return sortByValue(location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
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

	public static Map<String, String> dump(Object object) {
		Map<String, String> output = new HashMap<>();
		List<Method> methods = Arrays.asList(object.getClass().getDeclaredMethods());
		for (Method method : methods) {
			if (method.getName().matches("^(get|is|has).*") && method.getParameterCount() == 0) {
				try {
					output.put(method.getName(), method.invoke(object).toString());
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return output;
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

	public static boolean canEnable(Class<?> clazz) {
		if (clazz.getSimpleName().startsWith("_"))
			return false;
		if (Modifier.isAbstract(clazz.getModifiers()))
			return false;
		if (clazz.getAnnotation(Disabled.class) != null)
			return false;
		if (clazz.getAnnotation(Environments.class) != null && !Env.applies(clazz.getAnnotation(Environments.class).value()))
			return false;

		return true;
	}

	public static void tryRegisterListener(Object object) {
		try {
			boolean hasNoArgsConstructor = Stream.of(object.getClass().getConstructors()).anyMatch(c -> c.getParameterCount() == 0);
			if (object instanceof Listener) {
				if (!hasNoArgsConstructor)
					Nexus.warn("Cannot register listener on command " + object.getClass().getSimpleName() + ", needs @NoArgsConstructor");
				else
					Nexus.registerListener((Listener) object.getClass().newInstance());
			} else if (new ArrayList<>(getAllMethods(object.getClass(), withAnnotation(EventHandler.class))).size() > 0)
				Nexus.warn("Found @EventHandlers in " + object.getClass().getSimpleName() + " which does not implement Listener"
						+ (hasNoArgsConstructor ? "" : " or have a @NoArgsConstructor"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static int getFirstIndexOf(Collection<?> collection, Object object) {
		Iterator<?> iterator = collection.iterator();
		int index = 0;
		while (iterator.hasNext())
			if (iterator.next().equals(object))
				return index;
			else
				++index;

		return -1;
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

	public static void send(String UUID, String message) {
		send(getPlayer(UUID), message);
	}

	public static void send(UUID uuid, String message) {
		OfflinePlayer offlinePlayer = getPlayer(uuid);
		send(offlinePlayer, message);
	}

	public static void send(OfflinePlayer offlinePlayer, String message) {
		if (offlinePlayer.getPlayer() != null)
			send(offlinePlayer.getPlayer(), message);
	}

	public static void send(Player player, String message) {
		if (player != null && player.isOnline())
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

	public static void send(Player player, JsonBuilder builder) {
		if (player.isOnline())
			player.sendMessage(builder.build());
	}

	public static void send(CommandSender sender, JsonBuilder builder) {
		sender.sendMessage(builder.build());
	}

	public static void send(Player player, BaseComponent... baseComponents) {
		if (player.isOnline())
			player.sendMessage(baseComponents);
	}

	public static void send(CommandSender sender, BaseComponent... baseComponents) {
		sender.sendMessage(baseComponents);
	}

	public static void sendStaff(String message) {
		for (Player staff : Bukkit.getOnlinePlayers()) {
			if (!staff.hasPermission("group.moderator")) continue;
			send(staff, message);
		}
	}

	public static void sendStaff(String message, Player exclude) {
		sendStaff(message, Collections.singletonList(exclude));
	}

	public static void sendStaff(String message, List<Player> exclude) {
		List<UUID> excludedUuids = new ArrayList<>();
		for (Player player : exclude)
			excludedUuids.add(player.getUniqueId());

		for (Player staff : Bukkit.getOnlinePlayers()) {
			UUID uuid = staff.getUniqueId();
			if (excludedUuids.contains(uuid)) continue;
			if (!staff.hasPermission("group.moderator")) continue;

			send(staff, message);
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

	public enum ActionGroup {
		CLICK_BLOCK(Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK),
		CLICK_AIR(Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_AIR),
		RIGHT_CLICK(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR),
		LEFT_CLICK(Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR),
		CLICK(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR),
		PHYSICAL(Action.PHYSICAL);

		final List<Action> actions;

		ActionGroup(Action... actions) {
			this.actions = Arrays.asList(actions);
		}

		public boolean applies(PlayerInteractEvent event) {
			return actions.contains(event.getAction());
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
			player.hidePlayer(Nexus.getInstance(), this.player);
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
			player.showPlayer(Nexus.getInstance(), this.player);
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

		public static <T> T random(Class<? extends T> clazz) {
			return RandomUtils.randomElement(clazz.getEnumConstants());
		}

		public static <T> List<String> valueNameList(Class<? extends T> clazz) {
			return Arrays.stream(Env.values()).map(Env::name).collect(Collectors.toList());
		}

		public static <T> List<Enum<?>> valuesExcept(Class<? extends T> clazz, Enum<?>... exclude) {
			List<Enum<?>> excluded = Arrays.asList(exclude);
			List<Enum<?>> values = new ArrayList<>();
 			for (T enumValue : clazz.getEnumConstants())
				if (!excluded.contains(enumValue))
					values.add((Enum<?>) enumValue);

			return values;
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
		String viewTitle = getInvTitle(view);

		if (Strings.isNullOrEmpty(viewTitle))
			return false;

		return viewTitle.equals(title);
	}

	public static boolean containsInvViewTitle(InventoryView view, String title) {
		String viewTitle = getInvTitle(view);

		if (Strings.isNullOrEmpty(viewTitle))
			return false;

		return viewTitle.contains(title);
	}

	private static String getInvTitle(InventoryView view) {
		String viewTitle = null;
		try {
			viewTitle = view.getTitle();
		} catch (Exception ignored) {}

		return viewTitle;
	}

}
