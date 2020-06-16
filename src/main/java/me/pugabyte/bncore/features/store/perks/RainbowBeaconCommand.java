package me.pugabyte.bncore.features.store.perks;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.rainbowbeacon.RainbowBeacon;
import me.pugabyte.bncore.models.rainbowbeacon.RainbowBeaconService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Permission("rainbow.beacon")
public class RainbowBeaconCommand extends CustomCommand implements Listener {
	private final RainbowBeaconService service = new RainbowBeaconService();
	private RainbowBeacon rainbowBeacon;

	public RainbowBeaconCommand(CommandEvent event) {
		super(event);
		rainbowBeacon = service.get(player());
	}

	@Path("start")
	void activate() {
		if (rainbowBeacon.getTaskId() != null)
			error("Your rainbow beacon is already activated");

		if (!player().getLocation().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.BEACON))
			error("You must be standing on a beacon");

		rainbowBeacon.setLocation(player().getLocation().getBlock().getLocation());
		startTask(rainbowBeacon);
		send(PREFIX + "Activated your rainbow beacon");
	}

	@Path("stop")
	void stop() {
		if (rainbowBeacon.getTaskId() == null)
			error("You do not have a running rainbow beacon");
		Tasks.cancel(rainbowBeacon.getTaskId());
		rainbowBeacon.getLocation().getBlock().setType(Material.AIR);
		send(PREFIX + "Successfully deactivated your rainbow beacon");
	}

	@Path("delete")
	void delete() {
		if (rainbowBeacon.getLocation() == null)
			error("You do not have a rainbow beacon set");
		if (rainbowBeacon.getTaskId() != null)
			Tasks.cancel(rainbowBeacon.getTaskId());
		rainbowBeacon.getLocation().getBlock().setType(Material.AIR);
		service.delete(rainbowBeacon);
		send(PREFIX + "Successfully deleted your rainbow beacon");
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		for (RainbowBeacon rainbowBeacon : service.getCache().values()) {
			if (rainbowBeacon.getLocation().equals(event.getBlock().getLocation())) {
				event.setCancelled(true);
				break;
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
		for (RainbowBeacon rainbowBeacon : service.getCache().values())
			rainbowBeacon.getLocation().getBlock().setType(Material.AIR);
	}

	private static final List<Material> colors = new ArrayList<Material>() {{
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
		rainbowBeacon.setTaskId(Tasks.repeat(0, Time.SECOND.x(1), () -> {
			if (!location.getBlock().getChunk().isLoaded())
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
