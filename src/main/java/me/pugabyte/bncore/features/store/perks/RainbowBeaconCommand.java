package me.pugabyte.bncore.features.store.perks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ColorType;
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

@Permission("rainbow.beacon")
public class RainbowBeaconCommand extends CustomCommand implements Listener {
	SettingService service = new SettingService();

	static {
		startAll();
	}

	public RainbowBeaconCommand(CommandEvent event) {
		super(event);
	}

	@Path("set")
	void set() {
		Setting setting = service.get(player(), "rainbowBeaconLocation");
		if (setting.getValue() != null)
			error("You can only place one Rainbow Beacon");
		if (!player().getLocation().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.BEACON))
			error("You must be standing on a beacon to set a rainbow beacon");
		setting.setLocation(player().getLocation().getBlock().getLocation());
		service.save(setting);
		send(PREFIX + "Set the location of the rainbow beacon to your location");
	}

	@Path("activate")
	void activate() {
		Setting locationSetting = service.get(player(), "rainbowBeaconLocation");
		Setting rainbowTask = service.get(player(), "rainbowBeaconTaskId");
		if (locationSetting.getValue() == null)
			error("You must first set a rainbow becaon before activating it");
		if (rainbowTask.getValue() != null)
			error("You already have a running rainbow beacon");
		rainbowTask.setValue(startTask(player().getUniqueId()) + "");
		service.save(rainbowTask);
		send(PREFIX + "Activated a rainbow beacon");
	}

	@Path("(stop|halt|cancel)")
	void stop() {
		Setting rainbowTask = service.get(player(), "rainbowBeaconTaskId");
		Setting locationSetting = service.get(player(), "rainbowBeaconLocation");
		if (rainbowTask.getValue() == null)
			error("You do not have a running rainbow beacon");
		Tasks.cancel(Integer.parseInt(rainbowTask.getValue()));
		locationSetting.getLocation().getBlock().setType(Material.AIR);
		service.delete(rainbowTask);
		send("Successfully canceled ended your rainbow beacon");
	}

	@Path("(delete|remove)")
	void delete() {
		Setting locationSetting = service.get(player(), "rainbowBeaconLocation");
		Setting rainbowTask = service.get(player(), "rainbowBeaconTaskId");
		if (locationSetting.getValue() == null)
			error("You do not have a rainbow beacon set");
		if (rainbowTask.getValue() != null)
			Tasks.cancel(Integer.parseInt(rainbowTask.getValue()));
		locationSetting.getLocation().getBlock().setType(Material.AIR);
		service.delete(locationSetting);
		service.delete(rainbowTask);
		send(PREFIX + "Successfully deleted your rainbow beacon");
	}

	public static int startTask(UUID player) {
		SettingService service = new SettingService();
		Setting setting = service.get(player.toString(), "rainbowBeaconLocation");
		Location location = setting.getLocation();
		List<ColorType> colors = new ArrayList<ColorType>() {{
			add(ColorType.RED);
			add(ColorType.ORANGE);
			add(ColorType.YELLOW);
			add(ColorType.LIGHT_GREEN);
			add(ColorType.LIGHT_BLUE);
			add(ColorType.BLUE);
			add(ColorType.PURPLE);
			add(ColorType.MAGENTA);
		}};
		AtomicInteger i = new AtomicInteger(0);
		return Tasks.repeat(0, Time.SECOND.x(1), () -> {
			if (!location.getBlock().getChunk().isLoaded()) return;
			location.getBlock().setType(Material.STAINED_GLASS_PANE);
			location.getBlock().setData(colors.get(i.getAndIncrement()).getDurability().byteValue());
			if (i.get() == 8)
				i.set(0);
		});
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

}
