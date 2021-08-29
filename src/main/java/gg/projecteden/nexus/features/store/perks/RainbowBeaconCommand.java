package gg.projecteden.nexus.features.store.perks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeacon;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeaconService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.features.store.perks.RainbowBeaconCommand.PERMISSION;

@NoArgsConstructor
@Permission(PERMISSION)
public class RainbowBeaconCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "rainbow.beacon";
	private final RainbowBeaconService service = new RainbowBeaconService();

	public RainbowBeaconCommand(CommandEvent event) {
		super(event);
	}

	@Path("start [player]")
	void activate(@Arg(value = "self", permission = "group.seniorstaff") RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getTaskId() != null)
			error(formatWho(rainbowBeacon, WhoType.POSSESSIVE_UPPER) + " rainbow beacon is already activated");

		if (rainbowBeacon.getLocation() == null)
			if (location().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.BEACON)) {
				rainbowBeacon.setLocation(location().getBlock().getLocation());
				service.save(rainbowBeacon);
			} else
				error("You must be standing on a beacon");

		startTask(rainbowBeacon);
		send(PREFIX + "Activated " + formatWho(rainbowBeacon, WhoType.POSSESSIVE_LOWER) + " rainbow beacon");
	}

	@Path("stop [player]")
	void stop(@Arg(value = "self", permission = "group.seniorstaff") RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getTaskId() == null)
			error(formatWho(rainbowBeacon, WhoType.ACTIONARY_UPPER) + " not have a running rainbow beacon");
		Tasks.cancel(rainbowBeacon.getTaskId());
		rainbowBeacon.getLocation().getBlock().setType(Material.AIR);
		send(PREFIX + "Successfully deactivated " +  formatWho(rainbowBeacon, WhoType.POSSESSIVE_LOWER) + " rainbow beacon");
	}

	@Path("delete [player]")
	void delete(@Arg(value = "self", permission = "group.seniorstaff") RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getLocation() == null)
			error(formatWho(rainbowBeacon, WhoType.ACTIONARY_UPPER) + " not have a rainbow beacon set");

		if (rainbowBeacon.getTaskId() != null)
			Tasks.cancel(rainbowBeacon.getTaskId());

		rainbowBeacon.getLocation().getBlock().setType(Material.AIR);
		service.delete(rainbowBeacon);
		send(PREFIX + "Successfully deleted " + formatWho(rainbowBeacon, WhoType.POSSESSIVE_LOWER) + " rainbow beacon");
	}

	@Path("tp [player]")
	void tp(@Arg(value = "self", permission = "group.seniorstaff") RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getLocation() == null)
			error(formatWho(rainbowBeacon, WhoType.ACTIONARY_UPPER) + " not have an active rainbow beacon");

		player().teleportAsync(rainbowBeacon.getLocation(), TeleportCause.COMMAND);
	}

	@Path("list")
	@Permission(value = "group.seniorstaff", absolute = true)
	void list() {
		if (service.getCache().values().size() == 0)
			error("No active rainbow beacons");

		send(PREFIX + "Active beacons:");
		for (RainbowBeacon rainbowBeacon : service.getCache().values())
			send("&e" + rainbowBeacon.getNickname() + " &7- " + (rainbowBeacon.getTaskId() == null ? "Inactive" : "&aActive"));
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		for (RainbowBeacon rainbowBeacon : service.getCache().values())
			if (event.getBlock().getLocation().equals(rainbowBeacon.getLocation())) {
				event.setCancelled(true);
				break;
			}
	}

	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		handlePiston(event, event.getBlocks(), event.getDirection());
	}

	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		handlePiston(event, event.getBlocks(), event.getDirection());
	}

	private void handlePiston(Cancellable event, List<Block> blocks, BlockFace direction) {
		for (RainbowBeacon rainbowBeacon : service.getCache().values()) {
			final Location location = rainbowBeacon.getLocation();
			for (Block block : blocks) {
				if (block.getLocation().equals(location)) {
					event.setCancelled(true);
					return;
				}

				if (block.getRelative(direction).getLocation().equals(location)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	static {
		Tasks.wait(1, () -> {
			RainbowBeaconService service = new RainbowBeaconService();
			List<RainbowBeacon> beacons = service.getAll();
			for (RainbowBeacon rainbowBeacon : beacons) {
				startTask(rainbowBeacon);
				service.cache(rainbowBeacon);
			}
		});
	}

	@Override
	public void _shutdown() {
		final RainbowBeaconService service = new RainbowBeaconService();
		for (RainbowBeacon rainbowBeacon : service.getCache().values()) {
			final Location location = rainbowBeacon.getLocation();
			if (location != null && location.isChunkLoaded())
				location.getBlock().setType(Material.AIR);
		}
	}

	private static final List<Material> colors = new ArrayList<>() {{
		add(Material.RED_STAINED_GLASS_PANE);
		add(Material.ORANGE_STAINED_GLASS_PANE);
		add(Material.YELLOW_STAINED_GLASS_PANE);
		add(Material.LIME_STAINED_GLASS_PANE);
		add(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		add(Material.BLUE_STAINED_GLASS_PANE);
		add(Material.PURPLE_STAINED_GLASS_PANE);
		add(Material.MAGENTA_STAINED_GLASS_PANE);
	}};

	public static void startTask(RainbowBeacon rainbowBeacon) {
		Location location = rainbowBeacon.getLocation();
		AtomicInteger i = new AtomicInteger(0);
		rainbowBeacon.setTaskId(Tasks.repeat(0, TickTime.SECOND, () -> {
			if (location == null)
				return;
			if (!location.isChunkLoaded())
				return;

			if (location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEACON) {
				Tasks.cancel(rainbowBeacon.getTaskId());
				rainbowBeacon.setTaskId(null);
				return;
			}

			location.getBlock().setType(colors.get(i.getAndIncrement()));
			if (i.get() == 8)
				i.set(0);
		}));
	}

}
