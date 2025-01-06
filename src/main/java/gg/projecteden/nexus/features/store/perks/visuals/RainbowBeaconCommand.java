package gg.projecteden.nexus.features.store.perks.visuals;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeacon;
import gg.projecteden.nexus.models.rainbowbeacon.RainbowBeaconService;
import gg.projecteden.nexus.utils.Tasks;
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

import java.util.List;

@NoArgsConstructor
@Permission(RainbowBeaconCommand.PERMISSION)
@WikiConfig(rank = "Store", feature = "Visuals")
public class RainbowBeaconCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "rainbow.beacon";
	private final RainbowBeaconService service = new RainbowBeaconService();

	public RainbowBeaconCommand(CommandEvent event) {
		super(event);
	}

	@Path("start [player]")
	@Description("Activate a rainbow beacon")
	void activate(@Arg(value = "self", permission = Group.SENIOR_STAFF) RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getTaskId() != null)
			error(formatWho(rainbowBeacon, WhoType.POSSESSIVE_UPPER) + " rainbow beacon is already activated");

		if (rainbowBeacon.getLocation() == null)
			if (location().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.BEACON)) {
				rainbowBeacon.setLocation(location().getBlock().getLocation());
				service.save(rainbowBeacon);
			} else
				error("You must be standing on a beacon");

		rainbowBeacon.start();
		send(PREFIX + "Activated " + formatWho(rainbowBeacon, WhoType.POSSESSIVE_LOWER) + " rainbow beacon");
	}

	@Path("stop [player]")
	@Description("Deactivate a rainbow beacon")
	void stop(@Arg(value = "self", permission = Group.SENIOR_STAFF) RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getTaskId() == null)
			error(formatWho(rainbowBeacon, WhoType.ACTIONARY_UPPER) + " not have a running rainbow beacon");
		rainbowBeacon.stop();
		send(PREFIX + "Successfully deactivated " +  formatWho(rainbowBeacon, WhoType.POSSESSIVE_LOWER) + " rainbow beacon");
	}

	@Path("delete [player]")
	@Description("Delete a rainbow beacon")
	void delete(@Arg(value = "self", permission = Group.SENIOR_STAFF) RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getLocation() == null)
			error(formatWho(rainbowBeacon, WhoType.ACTIONARY_UPPER) + " not have a rainbow beacon set");

		rainbowBeacon.stop();
		service.delete(rainbowBeacon);
		send(PREFIX + "Successfully deleted " + formatWho(rainbowBeacon, WhoType.POSSESSIVE_LOWER) + " rainbow beacon");
	}

	@Path("tp [player]")
	@Description("Teleport to your rainbow beacon")
	void tp(@Arg(value = "self", permission = Group.STAFF) RainbowBeacon rainbowBeacon) {
		if (rainbowBeacon.getLocation() == null)
			error(formatWho(rainbowBeacon, WhoType.ACTIONARY_UPPER) + " not have an active rainbow beacon");

		player().teleportAsync(rainbowBeacon.getLocation(), TeleportCause.COMMAND);
	}

	@Path("list")
	@Permission(Group.STAFF)
	@Description("List active rainbow beacons")
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
				rainbowBeacon.start();
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

}
