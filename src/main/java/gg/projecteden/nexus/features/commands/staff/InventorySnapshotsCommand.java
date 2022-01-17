package gg.projecteden.nexus.features.commands.staff;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
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
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.TimeUtils.Timespan;
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

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.PlayerUtils.getPlayer;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.getShortLocationString;
import static gg.projecteden.utils.TimeUtils.shortDateTimeFormat;
import static gg.projecteden.utils.TimeUtils.shortishDateTimeFormat;

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
	void history(InventoryHistory history, @Arg("1") int page) {
		if (history.getSnapshots().isEmpty())
			error("No snapshots have been created");

		send(PREFIX + "Snapshots for &e" + history.getName());
		BiFunction<InventorySnapshot, String, JsonBuilder> formatter = (snapshot, index) -> {
			String timestamp = shortishDateTimeFormat(snapshot.getTimestamp());
			String timestampIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(snapshot.getTimestamp());
			String worldName = snapshot.getLocation().getWorld().getName();
			String reasonString = snapshot.getReason().getColor() + camelCase(snapshot.getReason());
			return json(index + " &e" + timestamp + " &7- &3Reason: &e" + reasonString + "&3, World: &e" + worldName)
					.hover("&3Time since: &e" + Timespan.of(snapshot.getTimestamp()).format())
					.hover("&3Location: &e" + getShortLocationString(snapshot.getLocation()))
					.command("/inventorysnapshots view " + history.getName() + " " + timestampIso);
		};
		paginate(history.getSnapshots(), formatter, "/inventorysnapshots " + history.getName(), page);
	}

	@TabCompleteIgnore
	@Path("view <player> <timestamp>")
	void view(InventoryHistory history, LocalDateTime timestamp) {
		new InventorySnapshotMenu(history.getSnapshot(timestamp)).open(player());
	}

	@Path("takeSnapshot [player]")
	void takeSnapshot(@Arg("self") Player player) {
		takeSnapshot(player, SnapshotReason.MANUAL);
		send(PREFIX + "Snapshot created");
	}

	@Async
	@Path("nearbyDeaths [page]")
	void nearbyDeaths(@Arg("1") int page) {
		Map<InventorySnapshot, Double> nearbyDeaths = new HashMap<>();
		for (InventoryHistory history : service.getAll()) {
			for (InventorySnapshot snapshot : history.getSnapshots()) {
				if (snapshot.getReason() != SnapshotReason.DEATH)
					continue;

				if (snapshot.getLocation() == null || !snapshot.getLocation().getWorld().equals(world()))
					continue;

				nearbyDeaths.put(snapshot, snapshot.getLocation().distance(location()));
			}
			service.save(history);
		}

		BiFunction<InventorySnapshot, String, JsonBuilder> function = (snapshot, index) -> {
			String name = getPlayer(snapshot.getUuid()).getName();
			int distance = nearbyDeaths.get(snapshot).intValue();
			String timeSince = Timespan.of(snapshot.getTimestamp()).format();
			return json(index + " &e" + name + " &7- " + distance + "m / " + timeSince + " ago")
					.hover("&eClick to teleport")
					.command("/tppos " + getShortLocationString(snapshot.getLocation()));
		};

		paginate(Utils.sortByValue(nearbyDeaths).keySet(), function, "/inventorysnapshots nearbyDeaths", page);
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
		Tasks.async(() -> {
			InventoryHistoryService service = new InventoryHistoryService();
			InventoryHistory history = service.get(player);
			history.takeSnapshot(snapshot);
			service.save(history);
		});
	}

	public static class InventorySnapshotMenu extends MenuUtils implements InventoryProvider {
		private final InventorySnapshot snapshot;
		private final OfflinePlayer owner;

		public InventorySnapshotMenu(InventorySnapshot snapshot) {
			this.snapshot = snapshot;
			this.owner = getPlayer(snapshot.getUuid());
		}

		@Override
		public void open(Player player, int page) {
			SmartInventory.builder()
					.provider(this)
					.title(colorize("&fInv Snapshot - " + getPlayer(snapshot.getUuid()).getName()))
					.size(6, 9)
					.build()
					.open(player, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);
			ItemStack applyToSelf = new ItemBuilder(Material.PLAYER_HEAD).name("&eApply to self").skullOwner(player).build();
			ItemStack applyToOwner = new ItemBuilder(Material.PLAYER_HEAD).name("&eApply to " + owner.getName()).skullOwner(owner).build();
			ItemStack applyToChest = new ItemBuilder(Material.CHEST).name("&eApply to chest").build();
			ItemStack teleport = new ItemBuilder(Material.COMPASS).name("&eTeleport").build();
			ItemStack info = new ItemBuilder(Material.BOOK).name("&eInfo")
				.lore("&3Reason: &e" + snapshot.getReason().getColor() + StringUtils.camelCase(snapshot.getReason()))
				.lore("&3Time: &e" + shortDateTimeFormat(snapshot.getTimestamp()))
				.lore("&3Location: &e" + getShortLocationString(snapshot.getLocation()))
				.lore("&3Levels: &e" + snapshot.getLevel())
					.loreize(false)
					.build();

			contents.set(0, 3, ClickableItem.from(applyToSelf, e -> snapshot.apply(player, player)));
			if (!owner.equals(player))
				contents.set(0, 4, ClickableItem.from(applyToOwner, e -> {
					if (!owner.isOnline() || owner.getPlayer() == null)
						PlayerUtils.send(player, new PlayerNotOnlineException(owner).getMessage());
					else
						snapshot.apply(player, owner.getPlayer());
				}));
			contents.set(0, 5, ClickableItem.from(applyToChest, e -> {
				player.closeInventory();
				applyingToChest.put(player.getUniqueId(), snapshot);
				PlayerUtils.send(player, PREFIX + "Click on a chest to apply the inventory to it");
			}));
			contents.set(0, 7, ClickableItem.from(teleport, e -> player.teleportAsync(snapshot.getLocation(), TeleportCause.COMMAND)));
			contents.set(0, 8, ClickableItem.empty(info));
			formatInventoryContents(contents, snapshot.getContents().toArray(ItemStack[]::new));
		}
	}

	@EventHandler(ignoreCancelled = true) // TODO: use TemporaryListener?
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Block block = event.getClickedBlock();

		if (!applyingToChest.containsKey(uuid))
			return;

		if (isNullOrAir(block) || !MaterialTag.CHESTS.isTagged(block.getType()))
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
