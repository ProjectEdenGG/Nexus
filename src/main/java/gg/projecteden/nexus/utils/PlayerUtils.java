package gg.projecteden.nexus.utils;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.afk.AFKUserService;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.mail.MailerService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.nickname.NicknameService;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.parchment.HasOfflinePlayer;
import gg.projecteden.parchment.HasPlayer;
import gg.projecteden.parchment.OptionalPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.api.common.utils.UUIDUtils.isUuid;
import static gg.projecteden.nexus.utils.Distance.distance;
import static gg.projecteden.nexus.utils.ItemUtils.fixMaxStackSize;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.nexus.utils.Utils.getMin;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class PlayerUtils {

	public enum Dev implements HasPlayer, PlayerOwnedObject {
		GRIFFIN("86d7e0e2-c95e-4f22-8f99-a6e83b398307", true),
		WAKKA("e9e07315-d32c-4df7-bd05-acfe51108234", true),
		BLAST("a4274d94-10f2-4663-af3b-a842c7ec729c", true),
		LEXI("d1de9ca8-78f6-4aae-87a1-8c112f675f12"),
		ARBY("0a2221e4-000c-4818-82ea-cd43df07f0d4", true),
		FILID("88f9f7f6-7703-49bf-ad83-a4dec7e8022c"),
		CYN("1d70383f-21ba-4b8b-a0b4-6c327fbdade1", true),
		LUI("fd5d72f3-d599-49d4-9e7b-6e6d7f2ac5b9"),
		POWER("79f66fc9-a975-4043-8b6d-b4823182de62", true),
		KODA("56cb00fd-4738-47bc-be08-cb7c4f9a5a94"),
		SPIKE("e089a260-7aeb-488f-a641-ab5867ab5ccd"),
		BRI("77966ca3-ac85-44b2-bcb0-b7c5f9342e86"),
		;

		@Getter
		private final UUID uuid;
		@Getter
		private final boolean showDeveloperTools;

		public static Dev of(HasUniqueId uuid) {
			return of(uuid.getUniqueId());
		}

		public static Dev of(UUID uuid) {
			for (Dev dev : values())
				if (dev.getUuid().equals(uuid))
					return dev;
			return null;
		}

		public @NotNull UUID getUniqueId() {
			return uuid;
		}

		@Override
		public Player getPlayer() {
			return Bukkit.getPlayer(uuid);
		}

		Dev(String uuid) { this(uuid, false); }

		Dev(String uuid, boolean notifyOfReloads) {
			this.uuid = UUID.fromString(uuid);
			this.showDeveloperTools = notifyOfReloads;
		}

		public void sendIfSelf(HasUniqueId player, Object message) {
			if (is(player))
				send(message);
		}

		public void sendIf(boolean condition, Object message) {
			if (condition)
				send(message);
		}

		public void send(Object message) {
			PlayerUtils.send(this, message);
		}

		public void send(String message, Object... args) {
			send(message.formatted(args));
		}

		public void debug(Object message) {
			if (Nexus.isDebug())
				PlayerUtils.send(this, message);
		}

		public boolean is(HasUniqueId player) {
			return player != null && is(player.getUniqueId());
		}

		public boolean is(UUID uuid) {
			return this.uuid.equals(uuid);
		}

		public boolean isNot(HasUniqueId player) {
			return !is(player);
		}

		public boolean isNot(UUID player) {
			return !is(player);
		}
	}

	public static class OnlinePlayers {
		private UUID viewer;
		private World world;
		private WorldGroup worldGroup;
		private SubWorldGroup subWorldGroup;
		private String region;
		private Location origin;
		private Double radius;
		private Boolean afk;
		private Boolean vanished;
		private Predicate<Rank> rank;
		private String permission;
		private List<UUID> include;
		private List<UUID> exclude;
		private List<Predicate<Player>> filters = new ArrayList<>();

		public static OnlinePlayers where() {
			return new OnlinePlayers();
		}

		public static OnlinePlayers where(Predicate<Player> filter) {
			return new OnlinePlayers().filter(filter);
		}

		public static List<Player> getAll() {
			return where().get();
		}

		public OnlinePlayers viewer(HasUniqueId player) {
			this.viewer = player.getUniqueId();
			return this;
		}

		public OnlinePlayers world(String world) {
			return world(Objects.requireNonNull(Bukkit.getWorld(world)));
		}

		public OnlinePlayers world(World world) {
			this.world = world;
			return this;
		}

		public OnlinePlayers worldGroup(WorldGroup worldGroup) {
			this.worldGroup = worldGroup;
			return this;
		}

		public OnlinePlayers subWorldGroup(SubWorldGroup subWorldGroup) {
			this.subWorldGroup = subWorldGroup;
			return this;
		}

		public OnlinePlayers region(ProtectedRegion region) {
			this.region = region.getId();
			return this;
		}

		public OnlinePlayers region(String region) {
			this.region = region;
			return this;
		}

		public OnlinePlayers radius(double radius) {
			this.radius = radius;
			return this;
		}

		public OnlinePlayers radius(Location origin, double radius) {
			this.origin = origin;
			this.radius = radius;
			return this;
		}

		public OnlinePlayers afk(boolean afk) {
			this.afk = afk;
			return this;
		}

		public OnlinePlayers vanished(boolean vanished) {
			this.vanished = vanished;
			return this;
		}

		public OnlinePlayers rank(Rank rank) {
			return rank(_rank -> _rank == rank);
		}

		public OnlinePlayers rank(Predicate<Rank> rankPredicate) {
			this.rank = rankPredicate;
			return this;
		}

		public OnlinePlayers hasPermission(String permission) {
			this.permission = permission;
			return this;
		}

		public OnlinePlayers includePlayers(List<HasUniqueId> players) {
			return include(players.stream().map(HasUniqueId::getUniqueId).toList());
		}

		public OnlinePlayers include(List<UUID> uuids) {
			if (this.include == null)
				this.include = new ArrayList<>();
			if (uuids == null)
				uuids = new ArrayList<>();

			this.include.addAll(uuids);
			return this;
		}

		public OnlinePlayers excludeSelf() {
			return exclude(viewer);
		}

		public OnlinePlayers excludePlayers(List<HasUniqueId> players) {
			return exclude(players.stream().map(HasUniqueId::getUniqueId).toList());
		}

		public OnlinePlayers exclude(HasUniqueId player) {
			return exclude(List.of(player.getUniqueId()));
		}

		public OnlinePlayers exclude(UUID uuid) {
			return exclude(List.of(uuid));
		}

		public OnlinePlayers exclude(List<UUID> uuids) {
			if (this.exclude == null)
				this.exclude = new ArrayList<>();
			this.exclude.addAll(uuids);
			return this;
		}

		public OnlinePlayers filter(Predicate<Player> filter) {
			this.filters.add(filter);
			return this;
		}

		public List<Player> get() {
			final Supplier<List<UUID>> online = () -> Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(toList());
			final List<UUID> uuids = include == null ? online.get() : include;

			if (uuids.isEmpty())
				return Collections.emptyList();

			Stream<Player> stream = uuids.stream()
				.filter(uuid -> exclude == null || !exclude.contains(uuid))
				.map(Bukkit::getOfflinePlayer)
				.filter(OfflinePlayer::isOnline)
				.map(OfflinePlayer::getPlayer)
				.filter(player -> !CitizensUtils.isNPC(player));

			if (origin == null && this.viewer != null) {
				final Player viewer = Bukkit.getPlayer(this.viewer);
				if (viewer != null)
					origin = viewer.getLocation();
			}

			for (Filter filter : Filter.values())
				stream = filter.filter(this, stream);

			for (Predicate<Player> filter : filters)
				stream = stream.filter(filter);

			return stream.collect(toList());
		}

		public <T> List<T> map(Function<Player, T> mapper) {
			return get().stream().map(mapper).collect(toList());
		}

		public int count() {
			return get().size();
		}

		public void forEach(Consumer<Player> consumer) {
			get().forEach(consumer);
		}

		@AllArgsConstructor
		private enum Filter {
			AFK(
				search -> search.afk != null,
				(search, player) -> new AFKUserService().get(player).isAfk() == search.afk),
			VANISHED(
				search -> search.vanished != null,
				(search, player) -> Nerd.of(player).isVanished() == search.vanished),
			RANK(
				search -> search.rank != null,
				(search, player) -> search.rank.test(Rank.of(player))),
			PERMISSION(
				search -> search.permission != null,
				(search, player) -> player.hasPermission(search.permission)),
			VIEWER(
				search -> search.viewer != null,
				(search, player) -> canSee(Bukkit.getPlayer(search.viewer), player)),
			WORLD(
				search -> search.world != null,
				(search, player) -> player.getWorld().equals(search.world)),
			WORLDGROUP(
				search -> search.worldGroup != null,
				(search, player) -> WorldGroup.of(player) == search.worldGroup),
			SUBWORLDGROUP(
				search -> search.subWorldGroup != null,
				(search, player) -> SubWorldGroup.of(player) == search.subWorldGroup),
			REGION(
				search -> search.world != null && search.region != null,
				(search, player) -> new WorldGuardUtils(search.world).isInRegion(player, search.region)),
			RADIUS(
				search -> search.origin != null && search.radius != null,
				(search, player) -> search.origin.getWorld().equals(player.getWorld()) && distance(player, search.origin).lte(search.radius)),
			;

			private final Predicate<OnlinePlayers> canFilter;
			private final BiPredicate<OnlinePlayers, Player> predicate;

			private Stream<Player> filter(OnlinePlayers search, Stream<Player> stream) {
				if (!canFilter.test(search))
					return stream;

				return stream.filter(player -> predicate.test(search, player));
			}
		}
	}

	public static boolean isWGEdit(Player player) {
		return player.hasPermission("worldguard.region.bypass.*");
	}

	public static boolean isHidden(OptionalPlayer player) {
		if (player.getPlayer() == null) return false;

		return Vanish.isVanished(player) || GameMode.SPECTATOR == player.getPlayer().getGameMode();
	}

	@Contract("null, _ -> false; _, null -> false")
	public static boolean isSelf(@Nullable HasUniqueId player1, @Nullable HasUniqueId player2) {
		return player1 != null && player2 != null && player1.getUniqueId().equals(player2.getUniqueId());
	}

	/**
	 * Tests if a player can see a vanished player. Returns false if either player is null.
	 * @param viewer player who is viewing
	 * @param target target player to check
	 * @return true if the target can be seen by the viewer
	 */
	@Contract("null, _ -> false; _, null -> false")
	public static boolean canSee(@Nullable Player viewer, @Nullable Player target) {
		if (viewer == null || target == null)
			return false;

		if (!(Minigamer.of(viewer).isPlaying() && Minigamer.of(target).isPlaying()))
			if (!viewer.canSee(target))
				return false;

		return !Vanish.isVanished(target) || viewer.hasPermission("pv.see");
	}

	/**
	 * Tests if a player can see a vanished player. Returns false if either player is null.
	 * @param viewer player who is viewing
	 * @param target target player to check
	 * @return true if the target can be seen by the viewer
	 */
	public static boolean canSee(@NotNull OptionalPlayer viewer, @NotNull OptionalPlayer target) {
		return canSee(viewer.getPlayer(), target.getPlayer());
	}

	public static List<String> getOnlineUuids() {
		return OnlinePlayers.getAll().stream()
				.map(player -> player.getUniqueId().toString())
				.collect(toList());
	}

	public static List<UUID> uuidsOf(Collection<Player> players) {
		return players.stream().map(Player::getUniqueId).toList();
	}

	public static @NotNull OfflinePlayer getPlayer(UUID uuid) {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public static @NotNull OfflinePlayer getPlayer(HasUniqueId uuid) {
		return getPlayer(uuid.getUniqueId());
	}

	public static @NotNull OfflinePlayer getPlayer(Identity identity) {
		return getPlayer(identity.uuid());
	}

	/**
	 * Searches for a player whose username or nickname fully or partially matches the given partial name.
	 * @param partialName UUID or partial text of a username/nickname
	 * @return an offline player
	 * @throws InvalidInputException input was null or empty
	 * @throws PlayerNotFoundException a player matching that (nick)name could not be found
	 */
	public static @NotNull OfflinePlayer getPlayer(String partialName) throws InvalidInputException, PlayerNotFoundException {
		if (partialName == null || partialName.length() == 0)
			throw new InvalidInputException("No player name given");

		partialName = stripColor(partialName);
		String original = partialName;
		partialName = partialName.toLowerCase().trim();

		if (isUuid(partialName))
			return getPlayer(UUID.fromString(partialName));

		final List<Player> players = OnlinePlayers.getAll();

		for (Player player : players)
			if (player.getName().equalsIgnoreCase(partialName))
				return player;
		for (Player player : players)
			if (Nickname.of(player).equalsIgnoreCase((partialName)))
				return player;

		NicknameService nicknameService = new NicknameService();
		Nickname fromNickname = nicknameService.getFromNickname(partialName);
		if (fromNickname != null)
			return fromNickname.getOfflinePlayer();

		for (Player player : players)
			if (player.getName().toLowerCase().startsWith(partialName))
				return player;
		for (Player player : players)
			if (Nickname.of(player).toLowerCase().startsWith((partialName)))
				return player;

		for (Player player : players)
			if (player.getName().toLowerCase().contains((partialName)))
				return player;
		for (Player player : players)
			if (Nickname.of(player).toLowerCase().contains((partialName)))
				return player;

		NerdService nerdService = new NerdService();

		Nerd fromAlias = nerdService.getFromAlias(partialName);
		if (fromAlias != null)
			return fromAlias.getOfflinePlayer();

		List<Nerd> matches = nerdService.find(partialName);
		if (matches.size() > 0) {
			Nerd nerd = matches.get(0);
			if (nerd != null)
				return nerd.getOfflinePlayer();
		}

		throw new PlayerNotFoundException(original);
	}

	public static @NotNull Player getOnlinePlayer(UUID uuid) {
		final OfflinePlayer player = getPlayer(uuid);
		if (!player.isOnline() || player.getPlayer() == null)
			throw new PlayerNotOnlineException(player);
		return player.getPlayer();
	}

	public static @NotNull Player getOnlinePlayer(HasUniqueId uuid) {
		return getOnlinePlayer(uuid.getUniqueId());
	}

	public static @NotNull Player getOnlinePlayer(Identity identity) {
		return getOnlinePlayer(identity.uuid());
	}

	public static MinMaxResult<Player> getNearestPlayer(Location location) {
		return getMin(OnlinePlayers.where().world(location.getWorld()).get(), player -> distance(player, location).get());
	}

	public static MinMaxResult<Player> getNearestVisiblePlayer(Location location, Integer radius) {
		List<Player> players = OnlinePlayers.where().world(location.getWorld()).get().stream()
			.filter(_player -> !GameMode.SPECTATOR.equals(_player.getGameMode()))
			.filter(_player -> !Vanish.isVanished(_player))
			.collect(toList());

		if (radius > 0)
			players = players.stream().filter(player -> distance(player, location).lte(radius)).collect(toList());

		return getMin(players, player -> distance(player, location).get());
	}

	public static MinMaxResult<Player> getNearestPlayer(HasPlayer original) {
		Player _original = original.getPlayer();
		List<Player> players = OnlinePlayers.where().world(_original.getWorld()).get().stream()
			.filter(player -> !isSelf(_original, player)).collect(toList());

		return getMin(players, player -> distance(player, _original).get());
	}

	public static ItemFrame getTargetItemFrame(Player player, int maxRadius, @Nullable Map<BlockFace, Integer> offsets) {
		if (offsets != null) {
			if (offsets.values().stream().filter(radius -> radius > 8).toList().size() > 0)
				throw new InvalidInputException("max offset radius size is 8");
			if (offsets.values().stream().filter(radius -> radius < 0).toList().size() > 0)
				throw new InvalidInputException("offset radius cannot be negative");
		}

		final double searchRadius = 0.5;
		List<Block> blocks = player.getLineOfSight(Set.of(Material.BARRIER, Material.AIR, Material.CAVE_AIR, Material.VOID_AIR), maxRadius)
			.stream()
			.sorted(Comparator.comparing(block -> distance(player, block).get()))
			.collect(Collectors.toList());

		if (offsets != null && !offsets.isEmpty()) {
			List<Block> offsetBlockList = new ArrayList<>();
			for (Block block : blocks) {
				for (BlockFace blockFace : offsets.keySet()) {
					for (int i = 1; i <= offsets.get(blockFace); i++)
						offsetBlockList.add(block.getRelative(blockFace, i));
				}
			}
			blocks.addAll(offsetBlockList);
		}

		for (Block block : blocks) {
			Collection<ItemFrame> itemFrames = block.getLocation().toCenterLocation().getNearbyEntitiesByType(ItemFrame.class, searchRadius);
			if (itemFrames.isEmpty())
				continue;

			for (ItemFrame itemFrame : itemFrames) {
				if (isNullOrAir(itemFrame.getItem()))
					continue;

				return itemFrame;
			}
		}

		return null;
	}

	public static BlockFace getBlockFace(Player player) {
		BlockFace[] radial = {BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST,
			BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST};
		float yaw = LocationUtils.normalizeYaw(player.getLocation());
		int ndx = Math.round(yaw / 45F);
		if (ndx > radial.length - 1)
			ndx = 0;

		return radial[ndx];
	}

	public static boolean canEdit(Player player, Location location) {
		if (!new WorldGuardUtils(player).getRegionsAt(location).isEmpty())
			return WorldGuardEditCommand.canWorldGuardEdit(player);

		return true;
	}

	public static void runCommand(CommandSender sender, String commandNoSlash) {
		if (sender == null)
			return;

//		if (sender instanceof Player)
//			Utils.callEvent(new PlayerCommandPreprocessEvent((Player) sender, "/" + command));

		Runnable command = () -> Bukkit.dispatchCommand(sender, commandNoSlash);

		if (Bukkit.isPrimaryThread())
			command.run();
		else
			Tasks.sync(command);
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

	/**
	 * Sends a message to a player
	 * @param recipient a {@link CommandSender}, {@link HasUniqueId}, {@link Identified}, or {@link UUID}
	 * @param message a {@link String} or {@link ComponentLike}
	 * @param objects used to {@link String#format(String, Object...) String#format} the message if <code>message</code> is a {@link String}
	 */
	public static void send(@Nullable Object recipient, @Nullable Object message, @NotNull Object... objects) {
		if (recipient == null || message == null)
			return;

		if (message instanceof String string && objects.length > 0)
			message = String.format(string, objects);

		if (recipient instanceof CommandSender sender) {
			if (!(message instanceof String || message instanceof ComponentLike))
				message = message.toString();

			if (message instanceof String string)
				sender.sendMessage(new JsonBuilder(string));
			else if (message instanceof ComponentLike componentLike)
				sender.sendMessage(componentLike);
		}

		else if (recipient instanceof OfflinePlayer offlinePlayer) {
			Player player = offlinePlayer.getPlayer();
			if (player != null)
				send(player, message);
		}

		else if (recipient instanceof HasOfflinePlayer hasOfflinePlayer)
			send(hasOfflinePlayer.getOfflinePlayer(), message);

		else if (recipient instanceof UUID uuid)
			send(getPlayer(uuid), message);

		else if (recipient instanceof HasUniqueId hasUniqueId)
			send(getPlayer(hasUniqueId), message);

		else if (recipient instanceof Identity identity)
			send(getPlayer(identity), message);

		else if (recipient instanceof Identified identified)
			send(getPlayer(identified.identity()), message);
	}

	public static void sendLine(Object recipient) {
		sendLine(recipient, 1);
	}

	public static void sendLine(Object recipient, int count) {
		for (int i = 0; i < count; i++)
			send(recipient, "");
	}

	public static boolean hasRoomFor(OptionalPlayer player, ItemStack... items) {
		if (player.getPlayer() == null)
			return false;

		return hasRoomFor(player.getPlayer(), items);
	}

	public static boolean hasRoomFor(Player player, ItemStack... items) {
		return hasRoomFor(player, Arrays.asList(items));
	}

	public static boolean hasRoomFor(OptionalPlayer player, List<ItemStack> items) {
		if (player.getPlayer() == null)
			return false;

		return hasRoomFor(player.getPlayer(), items);
	}

	public static boolean hasRoomFor(Player player, List<ItemStack> items) {
		return hasRoomFor(player.getInventory(), items);
	}

	public static boolean hasRoomFor(Inventory inventory, ItemStack item) {
		return hasRoomFor(inventory, Collections.singletonList(item));
	}

	public static boolean hasRoomFor(Inventory inventory, List<ItemStack> items) {
		int size = inventory.getType() == InventoryType.PLAYER ? 36 : inventory.getSize(); // Player Invs are actually 41 slots and we can't make those

		final Inventory inv;
		if (inventory.getType() == InventoryType.CHEST && size % 9 != 0)
			inv = Bukkit.createInventory(null, size);
		else
			inv = Bukkit.createInventory(null, inventory.getType());

		size = inv.getSize();
		for (int i = 0; i < size; i++) {
			ItemStack item = inventory.getItem(i);
			inv.setItem(i, item == null ? null : item.clone());
		}
		return inv.addItem(items.toArray(new ItemStack[0])).isEmpty();
	}

	/**
	 * Tests if a player has an item in their inventory
	 */
	public static boolean playerHas(OptionalPlayer player, Material material) {
		return playerHas(player, new ItemStack(material));
	}

	public static boolean playerHas(OptionalPlayer player, CustomMaterial material) {
		ItemStack item = searchInventory(player, material.getCustomModel());
		return Nullables.isNotNullOrAir(item);
	}

	public static boolean playerHas(OptionalPlayer player, ItemStack itemStack) {
		if (player.getPlayer() == null)
			return false;

		return player.getPlayer().getInventory().containsAtLeast(itemStack, itemStack.getAmount());
	}

	public static ItemStack searchInventory(OptionalPlayer player, CustomModel customModel) {
		if (player.getPlayer() == null)
			return null;

		for (ItemStack content : player.getPlayer().getInventory().getContents())
			if (customModel.equals(content))
				return content;

		return null;
	}

	@NotNull
	public static Set<@NotNull ItemStack> getNonNullInventoryContents(Player player) {
		return Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	public static ItemStack[] getHotbarContents(HasPlayer player) {
		return Arrays.copyOfRange(player.getPlayer().getInventory().getContents(), 0, 9);
	}

	public static void giveItemPreferNonHotbar(Player player, ItemStack item) {
		Set<Integer> openSlots = new HashSet<>();
		for (int i = 9; i < 36; i++) {
			if (isNullOrAir(player.getInventory().getContents()[i]))
				openSlots.add(i);
		}
		if (openSlots.size() > 0)
			player.getInventory().setItem(RandomUtils.randomElement(openSlots), item);
		else
			player.getInventory().addItem(item);
	}

	@Deprecated
	@ReplaceWith("Chat.broadcast(message, StaticChannel.STAFF)")
	public static void sendStaff(String message) {
		throw new UnsupportedOperationException();
	}

	public static long setPlayerTime(HasPlayer hasPlayer, String time) {
		Player player = hasPlayer.getPlayer();
		long ticks;
		try {
			ticks = DescParseTickFormat.parse(time);
		} catch (Exception ex) {
			throw new InvalidInputException("Unable to process time " + time);
		}
		boolean move = !time.startsWith("@");
		long dayTime = player.getPlayerTime();
		dayTime -= dayTime % 24000;
		dayTime += 24000 + ticks;
		if (move)
			dayTime -= player.getWorld().getTime();
		player.setPlayerTime(dayTime, move);
		return ticks;
	}

	public static HidePlayer hidePlayer(HasPlayer player) {
		return new HidePlayer(player);
	}

	public static void hidePlayers(HasPlayer hideFrom, HasPlayer... hide) {
		hidePlayers(hideFrom, Arrays.asList(hide));
	}

	public static void hidePlayers(HasPlayer hideFrom, Collection<? extends HasPlayer> hide) {
		UUID hideFromUUID = hideFrom.getPlayer().getUniqueId();
		hide.stream().map(HasPlayer::getPlayer).filter(player -> !player.getUniqueId().equals(hideFromUUID)).forEach(hasPlayer -> hidePlayer(hasPlayer).from(hideFrom));
	}

	public static ShowPlayer showPlayer(HasPlayer player) {
		return new ShowPlayer(player);
	}

	public static void showPlayers(HasPlayer showTo, HasPlayer... show) {
		showPlayers(showTo, Arrays.asList(show));
	}

	public static void showPlayers(HasPlayer showTo, Collection<? extends HasPlayer> show) {
		UUID showToUUID = showTo.getPlayer().getUniqueId();
		show.stream().map(HasPlayer::getPlayer).filter(player -> !player.getUniqueId().equals(showToUUID)).forEach(hasPlayer -> showPlayer(hasPlayer).to(showTo));
	}

	public static class HidePlayer {
		private final Player player;

		public HidePlayer(OptionalPlayer player) {
			this.player = player.getPlayer();
		}

		public void fromAll() {
			from(OnlinePlayers.getAll());
		}

		public void from(OptionalPlayer... players) {
			from(Arrays.asList(players));
		}

		public void from(Collection<? extends OptionalPlayer> players) {
			if (this.player == null)
				return;

			players.stream()
				.map(OptionalPlayer::getPlayer)
				.filter(Objects::nonNull)
				.filter(player -> !player.getUniqueId().equals(this.player.getUniqueId()))
				.forEach(player -> player.getPlayer().hidePlayer(Nexus.getInstance(), this.player));
		}
	}

	public static class ShowPlayer {
		private final Player player;

		public ShowPlayer(OptionalPlayer player) {
			this.player = player.getPlayer();
		}

		public void toAll() {
			to(OnlinePlayers.getAll());
		}

		public void to(OptionalPlayer... players) {
			to(Arrays.asList(players));
		}

		public void to(Collection<? extends OptionalPlayer> players) {
			if (this.player == null)
				return;

			players.stream()
				.map(OptionalPlayer::getPlayer)
				.filter(Objects::nonNull)
				.filter(player -> !player.getUniqueId().equals(this.player.getUniqueId()))
				.forEach(player -> player.showPlayer(Nexus.getInstance(), this.player));
		}
	}

	@Getter
	private static Map<String, Advancement> advancements = new LinkedHashMap<>();

	static {
		Map<String, Advancement> advancements = new LinkedHashMap<>();
		Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
		while (it.hasNext()) {
			Advancement advancement = it.next();
			advancements.put(advancement.getKey().getKey().toLowerCase(), advancement);
		}

		PlayerUtils.advancements = Utils.sortByKey(advancements);
	}

	public static Advancement getAdvancement(String name) {
		name = name.toLowerCase();
		if (advancements.containsKey(name))
			return advancements.get(name);
		throw new InvalidInputException("Advancement &e" + name + " &cnot found");
	}

	public static boolean selectHotbarItem(Player player, ItemStack toSelect) {
		final ItemStack mainHand = player.getInventory().getItemInMainHand();
		if (isNullOrAir(toSelect) || toSelect.equals(mainHand)) {
			return false;
		}

		List<ItemStack> contents = Arrays.stream(getHotbarContents(player)).toList();
		for (int i = 0; i < contents.size(); i++) {
			ItemStack item = contents.get(i);
			if (Nullables.isNullOrAir(item))
				continue;

			if (ItemUtils.isFuzzyMatch(toSelect, item)) {
				player.getInventory().setHeldItemSlot(i);
				return true;
			}
		}

		return false;

	}

	public static void removeItems(HasPlayer player, List<ItemStack> items) {
		for (ItemStack item : items) {
			removeItem(player, item);
		}
	}

	public static void removeItem(HasPlayer player, ItemStack item) {
		final Player _player = player.getPlayer();
		final PlayerInventory inv = _player.getInventory();
		inv.removeItem(item);
		if (_player.getItemOnCursor().equals(item))
			_player.setItemOnCursor(null);
	}

	public static void removeItem(HasPlayer player, CustomMaterial customMaterial) {
		final Player _player = player.getPlayer();
		final PlayerInventory inv = _player.getInventory();
		ItemStack item = searchInventory(player, customMaterial.getCustomModel());

		if (isNullOrAir(item))
			return;

		inv.removeItemAnySlot(item);
		if (_player.getItemOnCursor().equals(item))
			_player.setItemOnCursor(null);
	}

	public static void giveItem(HasPlayer player, Material material) {
		giveItem(player, material, 1);
	}

	public static void giveItem(HasPlayer player, Material material, String nbt) {
		giveItem(player, material, 1, nbt);
	}

	public static void giveItem(HasPlayer player, Material material, int amount) {
		giveItem(player, material, amount, null);
	}

	public static void giveItem(HasPlayer player, Material material, int amount, String nbt) {
		Player _player = player.getPlayer();
		if (material == Material.AIR)
			throw new InvalidInputException("Cannot spawn air");

		if (amount > 64) {
			for (int i = 0; i < (amount / 64); i++)
				giveItem(_player, new ItemStack(material, 64), nbt);
			giveItem(_player, new ItemStack(material, amount % 64), nbt);
		} else {
			giveItem(_player, new ItemStack(material, amount), nbt);
		}
	}

	public static void giveItem(HasOfflinePlayer player, ItemStack item) {
		giveItems(player, Collections.singletonList(item));
	}

	public static void giveItem(HasOfflinePlayer player, ItemStack item, String nbt) {
		giveItems(player, Collections.singletonList(item), nbt);
	}

	public static void giveItems(HasOfflinePlayer player, Collection<ItemStack> items) {
		giveItems(player, items, null);
	}

	public static void giveItems(HasOfflinePlayer player, Collection<ItemStack> items, String nbt) {
		if (isNullOrEmpty(items))
			return;

		List<ItemStack> finalItems = new ArrayList<>(items);
		finalItems.removeIf(Nullables::isNullOrAir);
		finalItems.removeIf(itemStack -> itemStack.getAmount() == 0);
		if (!Nullables.isNullOrEmpty(nbt)) {
			finalItems.clear();
			NBTContainer nbtContainer = new NBTContainer(nbt);
			for (ItemStack item : new ArrayList<>(items)) {
				NBTItem nbtItem = new NBTItem(item);
				nbtItem.mergeCompound(nbtContainer);
				finalItems.add(nbtItem.getItem());
			}
		}

		OfflinePlayer _player = player.getOfflinePlayer();
		if (_player.isOnline() && _player.getPlayer() != null)
			dropExcessItems(_player.getPlayer(), giveItemsAndGetExcess(_player.getPlayer(), finalItems));
		else
			giveItemsAndMailExcess(_player, finalItems, null, WorldGroup.of(Nerd.of(_player).getLocation()));
	}

	public static List<ItemStack> giveItemsAndGetExcess(HasOfflinePlayer player, ItemStack item) {
		return giveItemsAndGetExcess(player, Collections.singletonList(item));
	}

	public static List<ItemStack> giveItemsAndGetExcess(HasOfflinePlayer player, List<ItemStack> items) {
		if (!player.getOfflinePlayer().isOnline() || player.getOfflinePlayer().getPlayer() == null)
			return items;

		return giveItemsAndGetExcess(player.getOfflinePlayer().getPlayer().getInventory(), items);
	}

	@NotNull
	public static List<ItemStack> giveItemsAndGetExcess(Inventory inventory, List<ItemStack> items) {
		return new ArrayList<>() {{
			for (ItemStack item : fixMaxStackSize(items))
				if (!isNullOrAir(item))
					addAll(inventory.addItem(item.clone()).values());
		}};
	}

	public static void giveItemAndMailExcess(HasOfflinePlayer player, ItemStack items, WorldGroup worldGroup) {
		giveItemsAndMailExcess(player, Collections.singleton(items), null, worldGroup);
	}

	public static void giveItemAndMailExcess(HasOfflinePlayer player, ItemStack items, String message, WorldGroup worldGroup) {
		giveItemsAndMailExcess(player, Collections.singleton(items), message, worldGroup);
	}

	public static void giveItemsAndMailExcess(HasOfflinePlayer player, Collection<ItemStack> items, WorldGroup worldGroup) {
		giveItemsAndMailExcess(player, items, null, worldGroup);
	}

	public static void giveItemsAndMailExcess(HasOfflinePlayer player, Collection<ItemStack> items, String message, WorldGroup worldGroup) {
		OfflinePlayer offlinePlayer = player.getOfflinePlayer();
		List<ItemStack> finalItems = new ArrayList<>(items);
		finalItems.removeIf(Nullables::isNullOrAir);

		List<ItemStack> excess;
		boolean alwaysMail = offlinePlayer.getPlayer() == null || Nerd.of(offlinePlayer).getWorldGroup() != worldGroup;
		if (!alwaysMail)
			excess = giveItemsAndGetExcess(offlinePlayer.getPlayer(), finalItems);
		else
			excess = Utils.clone(items);
		if (isNullOrEmpty(excess)) return;

		mailItems(offlinePlayer, fixMaxStackSize(excess), message, worldGroup);
		String send = alwaysMail ? "Items have been given to you as &c/mail" : "Your inventory was full. Excess items were given to you as &c/mail";
		PlayerUtils.send(player, send);
	}

	public static void mailItem(HasOfflinePlayer player, ItemStack item, String message, WorldGroup worldGroup) {
		mailItems(player, Collections.singletonList(item), message, worldGroup);
	}

	public static void mailItem(HasOfflinePlayer player, ItemStack item, String message, WorldGroup worldGroup, String fromName) {
		mailItems(player, Collections.singletonList(item), message, worldGroup, fromName);
	}

	public static void mailItems(HasOfflinePlayer player, List<ItemStack> items, String message, WorldGroup worldGroup) {
		mailItems(player, items, message, worldGroup, null);
	}

	public static void mailItems(HasOfflinePlayer player, List<ItemStack> items, String message, WorldGroup worldGroup, String fromName) {
		new MailerService().edit(player.getOfflinePlayer(), mailer ->
				Mail.fromServer(mailer.getUuid(), worldGroup, message, items).setFromName(fromName).send());
	}

	public static void dropExcessItems(HasPlayer player, List<ItemStack> excess) {
		dropItems(player.getPlayer().getLocation(), excess);
	}

	public static void dropItems(Location location, List<ItemStack> items) {
		if (!isNullOrEmpty(items))
			for (ItemStack item : items)
				if (!isNullOrAir(item) && item.getAmount() > 0)
					location.getWorld().dropItemNaturally(location, item);
	}

	/**
	 * Gets {@link Player}s from a list of {@link HasPlayer}
	 * @param hasPlayers list of player containers
	 * @return list of players
	 */
	public static @NonNull List<Player> getPlayers(List<? extends @NonNull HasPlayer> hasPlayers) {
		return hasPlayers.stream().map(HasPlayer::getPlayer).collect(toList());
	}

	/**
	 * Gets {@link Player}s from a list of {@link OptionalPlayer} if they are non null
	 * @param hasPlayers list of optional players
	 * @return list of non-null players
	 */
	public static @NonNull List<@NonNull Player> getNonNullPlayers(Collection<? extends @NonNull OptionalPlayer> hasPlayers) {
		return hasPlayers.stream().map(OptionalPlayer::getPlayer).filter(Objects::nonNull).collect(toList());
	}

	@Getter
	@AllArgsConstructor
	public enum ArmorSlot {
		HELMET(EquipmentSlot.HEAD),
		CHESTPLATE(EquipmentSlot.CHEST),
		LEGGINGS(EquipmentSlot.LEGS),
		BOOTS(EquipmentSlot.FEET),
		;

		private final EquipmentSlot slot;

		public Material getLeather() {
			return Material.getMaterial("LEATHER_" + name());
		}

	}
}
