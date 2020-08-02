package me.pugabyte.bncore.features.holidays.aeveonproject.commands;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.*;

@Aliases("ap")
@Permission("group.staff")
@NoArgsConstructor
public class AeveonProjectCommand extends CustomCommand implements Listener {

	List<String> openDoors = new ArrayList<>();

	public AeveonProjectCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void run() {
		send("TODO");
	}

	@EventHandler
	public void onEnterRegion_Bulkhead(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("sensor")) return;

		String sensor = event.getRegion().getId();
		String doorRg = sensor.replaceAll("_sensor", "");
		if (openDoors.contains(doorRg)) return;

		openDoors.add(doorRg);
		ProtectedRegion door = WGUtils.getProtectedRegion(doorRg);

		String folder = ROOT + "Bulkhead/";
		Location loc = WGUtils.toLocation(door.getMinimumPoint());
		getWorld().playSound(loc, Sound.BLOCK_PISTON_EXTEND, SoundCategory.MASTER, 0.5F, 0.7F);
		for (int i = 0; i <= 2; i++) {
			int frame = i;
			Tasks.wait(Time.TICK.x(2 * i), () -> {
				String file = folder + "Bulkhead_" + frame;
				AeveonProject.WEUtils.paster().file(file).at(door.getMinimumPoint()).paste();
			});
		}
	}

	@EventHandler
	public void onExitRegion_Bulkhead(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("sensor")) return;

		ProtectedRegion sensorRg = event.getRegion();
		String doorRg = sensorRg.getId().replaceAll("_sensor", "");
		if (!openDoors.contains(doorRg)) return;
		if (WGUtils.getPlayersInRegion(sensorRg).size() != 0) return;

		ProtectedRegion door = WGUtils.getProtectedRegion(doorRg);
		String folder = ROOT + "Bulkhead/";

		Location loc = WGUtils.toLocation(door.getMinimumPoint());
		getWorld().playSound(loc, Sound.BLOCK_PISTON_CONTRACT, SoundCategory.MASTER, 0.5F, 0.7F);
		for (int i = 2; i >= 0; i--) {
			int frame = 2 - i;
			Tasks.wait(Time.TICK.x(2 * i), () -> {
				String file = folder + "Bulkhead_" + frame;
				AeveonProject.WEUtils.paster().file(file).at(door.getMinimumPoint()).paste();
				if (frame == 0) {
					openDoors.remove(doorRg);
				}
			});
		}
	}

}
