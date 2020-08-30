package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaCrashing;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.APLoc;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

@Region("sialia_crashing")
public class SialiaCrashing implements Listener, APSet {
	@Getter
	static boolean active = true;
	//
	List<Location> light1 = Arrays.asList(new APLoc(-824, 90, -1177), new APLoc(-823, 90, -1177));
	List<Location> light2 = Arrays.asList(new APLoc(-823, 90, -1173), new APLoc(-824, 90, -1173));
	List<Location> light3 = Arrays.asList(new APLoc(-823, 90, -1164), new APLoc(-824, 90, -1164));
	List<Location> light4 = Arrays.asList(new APLoc(-823, 90, -1160), new APLoc(-824, 90, -1160));
	List<Location> light5 = Arrays.asList(new APLoc(-823, 90, -1125), new APLoc(-824, 90, -1125));
	List<Location> light6 = Arrays.asList(new APLoc(-823, 90, -1121), new APLoc(-824, 90, -1121));
	List<Location> light7 = Arrays.asList(new APLoc(-823, 90, -1117), new APLoc(-824, 90, -1117));
	List<Location> light8 = Arrays.asList(new APLoc(-823, 90, -1113), new APLoc(-824, 90, -1113));
	List<Location> light9 = Arrays.asList(new APLoc(-823, 90, -1109), new APLoc(-824, 90, -1109));
	//

	// sialia -> crashing = ~471 ~ ~-8

	public SialiaCrashing() {
		BNCore.registerListener(this);

		new Sounds();
		new Particles();

		flickeringLights();
	}

	private void flickeringLights() {
		Tasks.repeat(0, Time.TICK.x(10), () -> {
			if (!SialiaCrashing.isActive())
				return;

			flickerLight(light1);
			flickerLight(light2);
			flickerLight(light3);
			flickerLight(light4);
			flickerLight(light5);
			flickerLight(light6);
			flickerLight(light7);
			flickerLight(light8);
			flickerLight(light9);
		});
	}

	private void flickerLight(List<Location> locs) {
		if (RandomUtils.chanceOf(60)) {
			int wait = RandomUtils.randomInt(3, 10);
			for (Location loc : locs) {
				loc.getBlock().setType(Material.SEA_LANTERN);
				Tasks.wait(Time.TICK.x(wait), () -> loc.getBlock().setType(Material.BONE_BLOCK));
			}
		}

	}
}
