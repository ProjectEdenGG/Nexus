package gg.projecteden.nexus.features.events.aeveonproject.sets.sialiaCrashing;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APRegions;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSet;
import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.features.events.annotations.Region;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

@Region("sialia_crashing")
public class SialiaCrashing implements Listener, APSet {
	public static boolean active = false;
	public static final Location shipRobot = APUtils.APLoc(-843, 85, -1088);
	//
	// sialia -> crashing = ~471 ~ ~-8
	//
	private final List<Location> light1 = Arrays.asList(APUtils.APLoc(-824, 90, -1177), APUtils.APLoc(-823, 90, -1177));
	private final List<Location> light2 = Arrays.asList(APUtils.APLoc(-823, 90, -1173), APUtils.APLoc(-824, 90, -1173));
	private final List<Location> light3 = Arrays.asList(APUtils.APLoc(-823, 90, -1164), APUtils.APLoc(-824, 90, -1164));
	private final List<Location> light4 = Arrays.asList(APUtils.APLoc(-823, 90, -1160), APUtils.APLoc(-824, 90, -1160));
	private final List<Location> light5 = Arrays.asList(APUtils.APLoc(-823, 90, -1125), APUtils.APLoc(-824, 90, -1125));
	private final List<Location> light6 = Arrays.asList(APUtils.APLoc(-823, 90, -1121), APUtils.APLoc(-824, 90, -1121));
	private final List<Location> light7 = Arrays.asList(APUtils.APLoc(-823, 90, -1117), APUtils.APLoc(-824, 90, -1117));
	private final List<Location> light8 = Arrays.asList(APUtils.APLoc(-823, 90, -1113), APUtils.APLoc(-824, 90, -1113));
	private final List<Location> light9 = Arrays.asList(APUtils.APLoc(-823, 90, -1109), APUtils.APLoc(-824, 90, -1109));
	private final List<Location> light10 = Arrays.asList(APUtils.APLoc(-826, 89, -1103), APUtils.APLoc(-821, 89, -1103));
	private final List<Location> light11 = Arrays.asList(APUtils.APLoc(-826, 89, -1098), APUtils.APLoc(-821, 89, -1098));
	private final List<Location> light12 = Arrays.asList(APUtils.APLoc(-824, 75, -1169), APUtils.APLoc(-824, 75, -1168),
			APUtils.APLoc(-823, 75, -1169), APUtils.APLoc(-823, 75, -1168));
	List<List<Location>> lights = Arrays.asList(light1, light2, light3, light4, light5, light6, light7, light8, light9, light10, light11, light12);
	//

	public SialiaCrashing() {
		Nexus.registerListener(this);

		new Sounds();
		new Particles();

		flickeringLightsTask();
	}

	private void flickeringLightsTask() {
		final Material boneBlock = Material.BONE_BLOCK;
		final Material seaLantern = Material.SEA_LANTERN;
		BlockData blockData = Material.BONE_BLOCK.createBlockData();

		Orientable orientable = (Orientable) blockData;
		orientable.setAxis(Axis.X);
		blockData = orientable;
		BlockData finalBlockData = blockData;

		Tasks.repeat(0, TickTime.TICK.x(10), () -> {
			if (!APSetType.SIALIA_CRASHING.get().isActive())
				return;

			for (List<Location> locs : lights) {
				if (RandomUtils.chanceOf(60)) {

					int wait = RandomUtils.randomInt(3, 10);
					for (Location loc : locs) {
						loc.getBlock().setType(seaLantern);

						Tasks.wait(TickTime.TICK.x(wait), () -> {
							loc.getBlock().setType(boneBlock);

							if (light10.contains(loc) || light11.contains(loc))
								loc.getBlock().setBlockData(finalBlockData);
						});
					}
				}
			}
		});
	}

	@Override
	public List<String> getUpdateRegions() {
		return Arrays.asList(APRegions.sialiaCrashing_shipColor, APRegions.sialiaCrashing_dockingport_1, APRegions.sialiaCrashing_dockingport_2, APRegions.sialiaCrashing_vent_door);
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
