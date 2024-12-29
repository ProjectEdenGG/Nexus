package gg.projecteden.nexus.features.events.aeveonproject.sets.sialia;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APRegions;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSet;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.features.events.annotations.Region;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Region("sialia")
public class Sialia implements Listener, APSet {
	public static boolean active = false;
	@Getter
	public static Player nearbyPlayer = null;
	List<String> openDoors = new ArrayList<>();
	public static final Location shipRobot = APUtils.APLoc(-1314, 85, -1080);

	public Sialia() {
		Nexus.registerListener(this);

		new Particles();
		new Sounds();

		Tasks.repeat(0, TickTime.TICK.x(5), () -> {
			List<Player> nearbyPlayers = new ArrayList<>(AeveonProject.worldguard().getPlayersInRegion(APSetType.SIALIA.get().getRegion()));
			if (nearbyPlayers.size() > 0)
				nearbyPlayer = nearbyPlayers.get(0);
		});
	}

	@EventHandler
	public void onEnterRegion_Bulkhead(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("sensor")) return;

		String sensor = event.getRegion().getId();
		String doorRg = sensor.replaceAll("_sensor", "");
		if (openDoors.contains(doorRg)) return;

		openDoors.add(doorRg);
		WorldGuardUtils worldguard = AeveonProject.worldguard();
		ProtectedRegion door = worldguard.getProtectedRegion(doorRg);

		String folder = AeveonProject.ROOT + "Bulkhead/";
		Location loc = worldguard.toLocation(door.getMinimumPoint());
		AeveonProject.getWorld().playSound(loc, Sound.BLOCK_PISTON_EXTEND, SoundCategory.MASTER, 0.5F, 0.7F);
		for (int i = 0; i <= 2; i++) {
			int frame = i;
			Tasks.wait(TickTime.TICK.x(2 * i), () -> {
				String file = folder + "Bulkhead_" + frame;
				AeveonProject.worldedit().paster().file(file).at(door.getMinimumPoint()).pasteAsync();
			});
		}
	}

	@EventHandler
	public void onExitRegion_Bulkhead(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("sensor")) return;

		ProtectedRegion sensorRg = event.getRegion();
		String doorRg = sensorRg.getId().replaceAll("_sensor", "");
		if (!openDoors.contains(doorRg)) return;
		WorldGuardUtils worldguard = AeveonProject.worldguard();
		if (worldguard.getPlayersInRegion(sensorRg).size() != 0) return;

		ProtectedRegion door = worldguard.getProtectedRegion(doorRg);
		String folder = AeveonProject.ROOT + "Bulkhead/";

		Location loc = worldguard.toLocation(door.getMinimumPoint());
		AeveonProject.getWorld().playSound(loc, Sound.BLOCK_PISTON_CONTRACT, SoundCategory.MASTER, 0.5F, 0.7F);
		for (int i = 2; i >= 0; i--) {
			int frame = 2 - i;
			Tasks.wait(TickTime.TICK.x(2 * i), () -> {
				String file = folder + "Bulkhead_" + frame;
				AeveonProject.worldedit().paster().file(file).at(door.getMinimumPoint()).pasteAsync();
				if (frame == 0) {
					openDoors.remove(doorRg);
				}
			});
		}
	}

	@Override
	public List<String> getUpdateRegions() {
		return Arrays.asList(APRegions.sialia_shipColor, APRegions.sialia_dockingport_1, APRegions.sialia_dockingport_2);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean bool) {
		active = bool;
	}
}
