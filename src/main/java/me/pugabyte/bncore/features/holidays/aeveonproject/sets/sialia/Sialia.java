package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialia;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.APUtils;
import me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.Regions;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.*;

@Region("sialia")
public class Sialia implements Listener, APSet {
	@Getter
	public static boolean active = true;
	@Getter
	public static Player nearbyPlayer = null;
	List<String> openDoors = new ArrayList<>();
	public static final Location shipRobot = APUtils.APLoc(-1314, 85, -1080);

	public Sialia() {
		BNCore.registerListener(this);

		new Particles();
		new Sounds();

		Tasks.repeat(0, Time.TICK.x(5), () -> {
			List<Player> nearbyPlayers = new ArrayList<>(WGUtils.getPlayersInRegion(APSetType.SIALIA.get().getRegion()));
			if (nearbyPlayers.size() > 0)
				nearbyPlayer = nearbyPlayers.get(0);
		});
	}


	@EventHandler
	public void onEnterRegion_Bulkhead(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("sensor")) return;

		String sensor = event.getRegion().getId();
		String doorRg = sensor.replaceAll("_sensor", "");
		if (openDoors.contains(doorRg)) return;

		openDoors.add(doorRg);
		ProtectedRegion door = WGUtils.getProtectedRegion(doorRg);

		String folder = ROOT + "Bulkhead/";
		Location loc = WGUtils.toLocation(door.getMinimumPoint());
		getWORLD().playSound(loc, Sound.BLOCK_PISTON_EXTEND, SoundCategory.MASTER, 0.5F, 0.7F);
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
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("sensor")) return;

		ProtectedRegion sensorRg = event.getRegion();
		String doorRg = sensorRg.getId().replaceAll("_sensor", "");
		if (!openDoors.contains(doorRg)) return;
		if (WGUtils.getPlayersInRegion(sensorRg).size() != 0) return;

		ProtectedRegion door = WGUtils.getProtectedRegion(doorRg);
		String folder = ROOT + "Bulkhead/";

		Location loc = WGUtils.toLocation(door.getMinimumPoint());
		getWORLD().playSound(loc, Sound.BLOCK_PISTON_CONTRACT, SoundCategory.MASTER, 0.5F, 0.7F);
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

	@Override
	public List<String> getUpdateRegions() {
		return Arrays.asList(Regions.sialia_shipColor, Regions.sialia_dockingport_1, Regions.sialia_dockingport_2);
	}
}
