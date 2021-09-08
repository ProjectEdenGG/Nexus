package gg.projecteden.nexus.utils;

import com.google.common.base.Strings;
import com.viaversion.viaversion.api.Via;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.CustomModel;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.mail.Mailer;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.mail.MailerService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.nickname.NicknameService;
import gg.projecteden.utils.Utils.MinMaxResult;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.lexikiq.HasOfflinePlayer;
import me.lexikiq.HasPlayer;
import me.lexikiq.HasUniqueId;
import me.lexikiq.OptionalPlayer;
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
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gg.projecteden.nexus.utils.ItemUtils.fixMaxStackSize;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.nexus.utils.Utils.getMin;
import static gg.projecteden.utils.StringUtils.isUuid;

@UtilityClass
public class PlayerUtils {

	public enum Dev implements PlayerOwnedObject {
		GRIFFIN("86d7e0e2-c95e-4f22-8f99-a6e83b398307"),
		WAKKA("e9e07315-d32c-4df7-bd05-acfe51108234"),
		BLAST("a4274d94-10f2-4663-af3b-a842c7ec729c"),
		LEXI("d1de9ca8-78f6-4aae-87a1-8c112f675f12"),
		FILID("88f9f7f6-7703-49bf-ad83-a4dec7e8022c"),
		LUI("fd5d72f3-d599-49d4-9e7b-6e6d7f2ac5b9"),
		KODA("56cb00fd-4738-47bc-be08-cb7c4f9a5a94"),
		SPIKE("e089a260-7aeb-488f-a641-ab5867ab5ccd");

		@Getter
		private final UUID uuid;

		public @NotNull UUID getUniqueId() {return uuid;}

		Dev(String uuid) {
			this.uuid = UUID.fromString(uuid);
		}

		public void send(String message) {
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

	public static List<Player> getOnlinePlayers(List<UUID> uuids) {
		return uuids.stream()
			.map(PlayerUtils::getOnlinePlayer)
			.filter(OfflinePlayer::isOnline)
			.map(OfflinePlayer::getPlayer)
			.toList();
	}

	public static List<Player> getOnlinePlayers() {
		return getOnlinePlayers(null, null);
	}

	public static List<Player> getOnlinePlayers(Player player) {
		return getOnlinePlayers(player, null);
	}

	public static List<Player> getOnlinePlayers(World world) {
		return getOnlinePlayers(null, world);
	}

	public static List<Player> getOnlinePlayers(Player player, World world) {
		Stream<Player> stream = Bukkit.getOnlinePlayers().stream()
			.filter(_player -> !CitizensUtils.isNPC(_player)).map(Player::getPlayer);

		if (player != null)
			stream = stream.filter(_player -> canSee(player, _player));
		if (world != null)
			stream = stream.filter(_player -> _player.getWorld().equals(world));

		return stream.toList();
	}

	public static boolean isVanished(OptionalPlayer player) {
		if (player.getPlayer() == null) return false;
		for (MetadataValue meta : player.getPlayer().getMetadata("vanished"))
			return (meta.asBoolean());
		return false;
	}

	public static boolean isSelf(HasUniqueId player1, HasUniqueId player2) {
		return player1.getUniqueId().equals(player2.getUniqueId());
	}

	/**
	 * Tests if a player can see a vanished player. Returns false if either player is null.
	 * @param viewer player who is viewing
	 * @param target target player to check
	 * @return true if the target can be seen by the viewer
	 */
	@Contract("null, _ -> false; _, null -> false")
	public static boolean canSee(@Nullable Player viewer, @Nullable Player target) {
		if (viewer == null || target == null) return false;
		return !isVanished(target) || viewer.hasPermission("pv.see");
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
		return PlayerUtils.getOnlinePlayers().stream()
				.map(player -> player.getUniqueId().toString())
				.collect(Collectors.toList());
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

		String original = partialName;
		partialName = partialName.toLowerCase().trim();

		if (isUuid(partialName))
			return getPlayer(UUID.fromString(partialName));

		for (Player player : PlayerUtils.getOnlinePlayers())
			if (player.getName().equalsIgnoreCase(partialName))
				return player;
		for (Player player : PlayerUtils.getOnlinePlayers())
			if (Nickname.of(player).equalsIgnoreCase((partialName)))
				return player;

		NicknameService nicknameService = new NicknameService();
		Nickname fromNickname = nicknameService.getFromNickname(partialName);
		if (fromNickname != null)
			return fromNickname.getOfflinePlayer();

		for (Player player : PlayerUtils.getOnlinePlayers())
			if (player.getName().toLowerCase().startsWith(partialName))
				return player;
		for (Player player : PlayerUtils.getOnlinePlayers())
			if (Nickname.of(player).toLowerCase().startsWith((partialName)))
				return player;

		for (Player player : PlayerUtils.getOnlinePlayers())
			if (player.getName().toLowerCase().contains((partialName)))
				return player;
		for (Player player : PlayerUtils.getOnlinePlayers())
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
		return getMin(getOnlinePlayers(location.getWorld()), player -> player.getLocation().distance(location));
	}

	public static MinMaxResult<Player> getNearestVisiblePlayer(Location location, Integer radius) {
		List<Player> players = getOnlinePlayers(location.getWorld()).stream()
			.filter(_player -> !GameMode.SPECTATOR.equals(_player.getGameMode()))
			.collect(Collectors.toList());

		if (radius < 0)
			players = players.stream().filter(player -> player.getLocation().distance(location) <= radius).collect(Collectors.toList());

		return getMin(players, player -> player.getLocation().distance(location));
	}

	public static MinMaxResult<Player> getNearestPlayer(HasPlayer original) {
		Player _original = original.getPlayer();
		List<Player> players = getOnlinePlayers(_original.getWorld()).stream()
			.filter(player -> !isSelf(_original, player)).collect(Collectors.toList());

		return getMin(players, player -> player.getLocation().distance(_original.getLocation()));
	}

	public static void runCommand(CommandSender sender, String commandNoSlash) {
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
		if (message instanceof String string && objects.length > 0)
			message = String.format(string, objects);

		if (recipient == null || message == null)
			return;

		if (recipient instanceof CommandSender sender) {
			if (message instanceof String string)
				sender.sendMessage(Identity.nil(), new JsonBuilder(string));
			else if (message instanceof ComponentLike componentLike)
				sender.sendMessage(Identity.nil(), componentLike);
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

	public static boolean hasRoomFor(OptionalPlayer player, ItemStack... items) {
		if (player.getPlayer() == null) return false;
		return hasRoomFor(player.getPlayer(), items);
	}

	public static boolean hasRoomFor(Player player, ItemStack... items) {
		return hasRoomFor(player, Arrays.asList(items));
	}

	public static boolean hasRoomFor(OptionalPlayer player, List<ItemStack> items) {
		if (player.getPlayer() == null) return false;
		return hasRoomFor(player.getPlayer(), items);
	}

	public static boolean hasRoomFor(Player player, List<ItemStack> items) {
		int usedSlots = 0;
		int openSlots = 0;
		boolean[] fullSlot = new boolean[36];
		ItemStack[] inv = player.getInventory().getContents();
		for (ItemStack item : items) {
			int maxStack = item.getMaxStackSize();
			int needed = item.getAmount();
			for (int i = 0; i < 36; i++) {
				if (fullSlot[i]) continue;
				ItemStack invItem = inv[i];
				if (isNullOrAir(invItem)) {
					openSlots++;
					continue;
				}
				if (invItem.isSimilar(item)) {
					int available = maxStack - invItem.getAmount();
					needed -= available;
					if (needed > 0)
						fullSlot[i] = true;
				}
			}
			if (needed > 0)
				usedSlots += Math.ceil((double) needed / (double) maxStack);
		}
		return openSlots >= usedSlots;
	}

	/**
	 * Tests if a player has an item in their inventory
	 */
	public static boolean playerHas(OptionalPlayer player, ItemStack itemStack) {
		if (player.getPlayer() == null) return false;
		return Arrays.asList(player.getPlayer().getInventory().getContents()).contains(itemStack);
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
		return Arrays.copyOfRange(player.getPlayer().getInventory().getContents(), 0, 8);
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

		public HidePlayer(HasPlayer player) {
			this.player = player.getPlayer();
		}

		public void from(HasPlayer... players) {
			from(Arrays.asList(players));
		}

		public void from(Collection<? extends HasPlayer> players) {
			UUID uuid = player.getUniqueId();
			players.stream().map(HasPlayer::getPlayer).filter(player1 -> !player1.getUniqueId().equals(uuid)).forEach(player1 -> player1.getPlayer().hidePlayer(Nexus.getInstance(), this.player));
		}
	}

	public static class ShowPlayer {
		private final Player player;

		public ShowPlayer(HasPlayer player) {
			this.player = player.getPlayer();
		}

		public void to(HasPlayer... players) {
			to(Arrays.asList(players));
		}

		public void to(Collection<? extends HasPlayer> players) {
			UUID uuid = player.getUniqueId();
			players.stream().map(HasPlayer::getPlayer).filter(player1 -> !player1.getUniqueId().equals(uuid)).forEach(player1 -> player1.showPlayer(Nexus.getInstance(), this.player));
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
		List<ItemStack> finalItems = new ArrayList<>(items);
		finalItems.removeIf(ItemUtils::isNullOrAir);
		if (!Strings.isNullOrEmpty(nbt)) {
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

		List<ItemStack> excess = new ArrayList<>();
		for (ItemStack item : fixMaxStackSize(items))
			if (!isNullOrAir(item))
				excess.addAll(player.getOfflinePlayer().getPlayer().getInventory().addItem(item.clone()).values());

		return excess;
	}

	public static void giveItemAndMailExcess(HasOfflinePlayer player, ItemStack items, WorldGroup worldGroup) {
		giveItemsAndMailExcess(player, Collections.singleton(items), null, worldGroup);
	}

	public static void giveItemAndMailExcess(HasOfflinePlayer player, ItemStack items, String message, WorldGroup worldGroup) {
		giveItemsAndMailExcess(player, Collections.singleton(items), message, worldGroup);
	}

	public static void giveItemsAndMailExcess(HasOfflinePlayer player, Collection<ItemStack> items, String message, WorldGroup worldGroup) {
		OfflinePlayer offlinePlayer = player.getOfflinePlayer();
		List<ItemStack> finalItems = new ArrayList<>(items);
		finalItems.removeIf(ItemUtils::isNullOrAir);

		List<ItemStack> excess;
		boolean alwaysMail = offlinePlayer.getPlayer() == null || Nerd.of(offlinePlayer).getWorldGroup() != worldGroup;
		if (!alwaysMail)
			excess = giveItemsAndGetExcess(offlinePlayer.getPlayer(), finalItems);
		else
			excess = Utils.clone(items);
		if (Utils.isNullOrEmpty(excess)) return;

		MailerService service = new MailerService();
		Mailer mailer = service.get(offlinePlayer);
		Mail.fromServer(mailer.getUuid(), worldGroup, message, fixMaxStackSize(excess)).send();
		service.save(mailer);

		String send = alwaysMail ? "Items have been given to you as &c/mail" : "Your inventory was full. Excess items were given to you as &c/mail";
		mailer.sendMessage(JsonBuilder.fromPrefix("Mail").next(send).command("/mail box").hover("&eClick to view your mail box"));
	}

	public static void dropExcessItems(HasPlayer player, List<ItemStack> excess) {
		Player _player = player.getPlayer();
		if (!excess.isEmpty())
			for (ItemStack itemStack : excess)
				if (!isNullOrAir(itemStack) && itemStack.getAmount() > 0)
					_player.getWorld().dropItemNaturally(_player.getLocation(), itemStack);
	}

	/**
	 * Gets {@link Player}s from a list of {@link HasPlayer}
	 * @param hasPlayers list of player containers
	 * @return list of players
	 */
	public static @NonNull List<Player> getPlayers(List<? extends @NonNull HasPlayer> hasPlayers) {
		return hasPlayers.stream().map(HasPlayer::getPlayer).collect(Collectors.toList());
	}

	/**
	 * Gets {@link Player}s from a list of {@link OptionalPlayer} if they are non null
	 * @param hasPlayers list of optional players
	 * @return list of non-null players
	 */
	public static @NonNull List<@NonNull Player> getNonNullPlayers(List<? extends @NonNull OptionalPlayer> hasPlayers) {
		return hasPlayers.stream().map(OptionalPlayer::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
	}

	// https://wiki.vg/Protocol_version_numbers
	// TODO Is this available somewhere besides the wiki?
	private static final Map<Integer, String> versions = Map.of(
		756, "1.17.1",
		755, "1.17"
	);

	public static String getPlayerVersion(Player player) {
		try {
			if (Bukkit.getServer().getPluginManager().getPlugin("ViaVersion") == null)
				return "Unknown (ViaVersion not loaded)";

			final int version = Via.getAPI().getPlayerVersion(player);
			if (versions.containsKey(version))
				return versions.get(version);
			return "Unknown (" + version + ")";
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			return "Unknown (ViaVersion error)";
		}
	}

	/**
	 * Extension of {@link PlayerInteractEvent} used to test if a plugin like WorldGuard or LWC will block the event.
	 */
	public static class FakePlayerInteractEvent extends PlayerInteractEvent {
		public FakePlayerInteractEvent(Player player, Action action, ItemStack itemInHand, Block clickedBlock, BlockFace blockFace) {
			super(player, action, itemInHand, clickedBlock, blockFace);
		}
	}

}
