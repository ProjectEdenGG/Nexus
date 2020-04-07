package me.pugabyte.bncore.features.store.perks;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Permission("rainbow.beacon")
public class RainbowBeaconCommand extends CustomCommand implements Listener {
	SettingService service = new SettingService();
	Setting locationSetting;
	Setting rainbowTask;

	static {
		Tasks.wait(1, RainbowBeaconCommand::startAll);
	}

	public RainbowBeaconCommand(CommandEvent event) {
		super(event);
		locationSetting = service.get(player(), "rainbowBeaconLocation");
		rainbowTask = service.get(player(), "rainbowBeaconTaskId");
	}

	@Path("set")
	void set() {
		if (locationSetting.getValue() != null)
			error("You can only place one Rainbow Beacon. &3Use &c/rainbowbeacon reset &3to change locations");
		reset();
	}

	@Path("reset")
	void reset() {
		if (!player().getLocation().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.BEACON))
			error("You must be standing on a beacon");
		locationSetting.setLocation(player().getLocation().getBlock().getLocation());
		service.save(locationSetting);
		send(PREFIX + "Set to your location");
	}

	@Path("start")
	void activate() {
		if (locationSetting.getValue() == null)
			error("You must set your rainbow beacon's location before activating it with /rainbowbeacon set");
		if (rainbowTask.getValue() != null)
			error("Your rainbow beacon is already activated");
		rainbowTask.setValue(startTask(player().getUniqueId()) + "");
		service.save(rainbowTask);
		send(PREFIX + "Activated your rainbow beacon");
	}

	@Path("stop")
	void stop() {
		if (rainbowTask.getValue() == null)
			error("You do not have a running rainbow beacon");
		Tasks.cancel(Integer.parseInt(rainbowTask.getValue()));
		locationSetting.getLocation().getBlock().setType(Material.AIR);
		service.delete(rainbowTask);
		send(PREFIX + "Successfully deactivated your rainbow beacon");
	}

	@Path("delete")
	void delete() {
		if (locationSetting.getValue() == null)
			error("You do not have a rainbow beacon set");
		if (rainbowTask.getValue() != null)
			Tasks.cancel(Integer.parseInt(rainbowTask.getValue()));
		locationSetting.getLocation().getBlock().setType(Material.AIR);
		service.delete(locationSetting);
		service.delete(rainbowTask);
		send(PREFIX + "Successfully deleted your rainbow beacon");
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		List<Setting> settings = new SettingService().getFromType("rainbowBeaconLocation");
		for (Setting setting : settings) {
			if (setting.getLocation().equals(event.getBlock().getLocation())) {
				event.setCancelled(true);
				break;
			}
		}
	}

	public static void startAll() {
		SettingService service = new SettingService();
		List<Setting> settings = new SettingService().getFromType("rainbowBeaconTaskId");
		for (Setting setting : settings) {
			UUID player = UUID.fromString(setting.getId());
			setting.setValue(startTask(player) + "");
			service.save(setting);
		}
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

	public static int startTask(UUID player) {
		SettingService service = new SettingService();
		Setting setting = service.get(player.toString(), "rainbowBeaconLocation");
		Location location = setting.getLocation();
		AtomicInteger i = new AtomicInteger(0);
		return Tasks.repeat(0, Time.SECOND.x(1), () -> {
			if (!location.getBlock().getChunk().isLoaded()) return;
			location.getBlock().setType(colors.get(i.getAndIncrement()));
			if (i.get() == 8)
				i.set(0);
		});
	}

}
