package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory.InventorySnapshot;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistory.SnapshotReason;
import gg.projecteden.nexus.models.inventoryhistory.InventoryHistoryService;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@NoArgsConstructor
@Permission(Group.SENIOR_STAFF)
public class InventorySnapshotsCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("InventorySnapshots");
	private final InventoryHistoryService service = new InventoryHistoryService();

	private static final Map<UUID, InventorySnapshot> applyingToChest = new HashMap<>();

	public InventorySnapshotsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> [page]")
	@Description("View a list of saved inventory snapshots")
	void history(InventoryHistory history, @Arg("1") int page) {
		if (history.getSnapshots().isEmpty())
			error("No snapshots have been created");

		send(PREFIX + "Snapshots for &e" + history.getName());
		BiFunction<InventorySnapshot, String, JsonBuilder> formatter = (snapshot, index) -> {
			String timestamp = TimeUtils.shortishDateTimeFormat(snapshot.getTimestamp());
			String timestampIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(snapshot.getTimestamp());
			String worldName = snapshot.getLocation().getWorld().getName();
			String reasonString = snapshot.getReason().getColor() + camelCase(snapshot.getReason());
			return json(index + " &e" + timestamp + " &7- &3Reason: &e" + reasonString + "&3, World: &e" + worldName)
					.hover("&3Time since: &e" + Timespan.of(snapshot.getTimestamp()).format())
					.hover("&3Location: &e" + StringUtils.getShortLocationString(snapshot.getLocation()))
					.command("/inventorysnapshots view " + history.getName() + " " + timestampIso);
		};
		new Paginator<InventorySnapshot>()
			.values(history.getSnapshots())
			.formatter(formatter)
			.command("/inventorysnapshots " + history.getName())
			.page(page)
			.send();
	}

	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("view <player> <timestamp>")
	void view(InventoryHistory history, LocalDateTime timestamp) {
		new InventorySnapshotMenu(history.getSnapshot(timestamp)).open(player());
	}

	@Path("takeSnapshot [player]")
	@Description("Take a snapshot of a player's current state")
	void takeSnapshot(@Arg("self") Player player) {
		takeSnapshot(player, SnapshotReason.MANUAL);
		send(PREFIX + "Snapshot created");
	}

	@Async
	@Path("nearbyDeaths [page]")
	@Description("View nearby snapshots created by a death")
	void nearbyDeaths(@Arg("1") int page) {
		Map<InventorySnapshot, Double> nearbyDeaths = new HashMap<>();
		for (InventoryHistory history : service.getAll()) {
			for (InventorySnapshot snapshot : history.getSnapshots()) {
				if (snapshot.getReason() != SnapshotReason.DEATH)
					continue;

				if (!snapshot.getLocation().getWorld().equals(world()))
					continue;

				nearbyDeaths.put(snapshot, Distance.distance(snapshot, location()).getRealDistance());
			}
			service.save(history);
		}

		BiFunction<InventorySnapshot, String, JsonBuilder> function = (snapshot, index) -> {
			String name = PlayerUtils.getPlayer(snapshot.getUuid()).getName();
			int distance = nearbyDeaths.get(snapshot).intValue();
			String timeSince = Timespan.of(snapshot.getTimestamp()).format();
			return json(index + " &e" + name + " &7- " + distance + "m / " + timeSince + " ago")
					.hover("&eClick to teleport")
					.command("/tppos " + StringUtils.getShortLocationString(snapshot.getLocation()));
		};

		new Paginator<InventorySnapshot>()
			.values(Utils.sortByValue(nearbyDeaths).keySet())
			.formatter(function)
			.command("/inventorysnapshots nearbyDeaths")
			.page(page)
			.send();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		takeSnapshot(event.getEntity(), SnapshotReason.DEATH);
	}

	@EventHandler
	public void onWorldChange(PlayerTeleportEvent event) {
		if (!event.getFrom().getWorld().equals(event.getTo().getWorld()))
			takeSnapshot(event.getPlayer(), SnapshotReason.WORLD_CHANGE);
	}

	public void takeSnapshot(Player player, SnapshotReason reason) {
		InventorySnapshot snapshot = new InventorySnapshot(player, reason);
		Tasks.async(() -> new InventoryHistoryService().edit(player, history -> history.takeSnapshot(snapshot)));
	}

	public static class InventorySnapshotMenu extends InventoryProvider {
		private final InventorySnapshot snapshot;
		private final OfflinePlayer owner;

		public InventorySnapshotMenu(InventorySnapshot snapshot) {
			this.snapshot = snapshot;
			this.owner = PlayerUtils.getPlayer(snapshot.getUuid());
		}

		@Override
		public String getTitle() {
			return "&fInv Snapshot - " + PlayerUtils.getPlayer(snapshot.getUuid()).getName();
		}

		@Override
		public void init() {
			addCloseItem();
			ItemStack applyToSelf = new ItemBuilder(Material.PLAYER_HEAD).name("&eApply to self").skullOwner(viewer).build();
			ItemStack applyToOwner = new ItemBuilder(Material.PLAYER_HEAD).name("&eApply to " + owner.getName()).skullOwner(owner).build();
			ItemStack applyToChest = new ItemBuilder(Material.CHEST).name("&eApply to chest").build();
			ItemStack teleport = new ItemBuilder(Material.COMPASS).name("&eTeleport").build();
			ItemStack info = new ItemBuilder(Material.BOOK).name("&eInfo")
				.lore("&3Reason: &e" + snapshot.getReason().getColor() + StringUtils.camelCase(snapshot.getReason()))
				.lore("&3Time: &e" + TimeUtils.shortDateTimeFormat(snapshot.getTimestamp()))
				.lore("&3Location: &e" + StringUtils.getShortLocationString(snapshot.getLocation()))
				.lore("&3Levels: &e" + snapshot.getLevel())
				.loreize(false)
				.build();

			contents.set(0, 3, ClickableItem.of(applyToSelf, e -> snapshot.apply(viewer, viewer)));
			if (!owner.equals(viewer))
				contents.set(0, 4, ClickableItem.of(applyToOwner, e -> {
					if (!owner.isOnline() || owner.getPlayer() == null)
						PlayerUtils.send(viewer, new PlayerNotOnlineException(owner).getMessage());
					else
						snapshot.apply(viewer, owner.getPlayer());
				}));
			contents.set(0, 5, ClickableItem.of(applyToChest, e -> {
				viewer.closeInventory();
				applyingToChest.put(viewer.getUniqueId(), snapshot);
				PlayerUtils.send(viewer, PREFIX + "Click on a chest to apply the inventory to it");
			}));
			contents.set(0, 7, ClickableItem.of(teleport, e -> viewer.teleportAsync(snapshot.getLocation(), TeleportCause.COMMAND)));
			contents.set(0, 8, ClickableItem.empty(info));
			MenuUtils.formatInventoryContents(contents, snapshot.getContents().toArray(ItemStack[]::new));
		}
	}

	@EventHandler(ignoreCancelled = true) // TODO: use TemporaryListener?
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Block block = event.getClickedBlock();

		if (!applyingToChest.containsKey(uuid))
			return;

		if (Nullables.isNullOrAir(block) || !MaterialTag.CHESTS.isTagged(block.getType()))
			return;

		if (!(block.getState() instanceof InventoryHolder holder))
			return;

		event.setCancelled(true);

		final Inventory inventory = holder.getInventory();
		final long freeSpace = Arrays.stream(inventory.getContents()).filter(Nullables::isNullOrAir).count();

		final InventorySnapshot snapshot = applyingToChest.get(uuid);
		final List<ItemStack> contents = snapshot.getContents().stream().filter(Nullables::isNotNullOrAir).toList();

		if (contents.size() > freeSpace) {
			PlayerUtils.send(player, PREFIX + "&cSnapshot contents too large for this inventory");
			return;
		}

		inventory.setContents(snapshot.getContents().toArray(new ItemStack[0]));
		PlayerUtils.send(player, PREFIX + "Snapshot contents applied to inventory (&4Warning&c: does not include exp&3)");
		applyingToChest.remove(uuid);
	}

}
