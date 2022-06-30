package gg.projecteden.nexus.features.events.y2020.bearfair20.fairgrounds;

import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Interactables {
	private static boolean strengthTest_active = false;

	public Interactables() {

	}

	public static void strengthTest() {
		if (strengthTest_active) return;
		strengthTest_active = true;
		Location one = new Location(BearFair20.getWorld(), -924, 136, -1578);
		Location two = new Location(BearFair20.getWorld(), -923, 136, -1578);
		Location three = new Location(BearFair20.getWorld(), -921, 138, -1578);
		Location four = new Location(BearFair20.getWorld(), -921, 139, -1578);
		Location five = new Location(BearFair20.getWorld(), -921, 140, -1578);
		Location six = new Location(BearFair20.getWorld(), -921, 141, -1578);
		Location seven = new Location(BearFair20.getWorld(), -921, 142, -1578);
		List<Location> lights = Arrays.asList(one, two, three, four, five, six, seven);

		int max = 7;
		int limit = RandomUtils.randomInt(1, 7);
		AtomicInteger count = new AtomicInteger();
		AtomicLong wait = new AtomicLong(0);
		AtomicBoolean breakBool = new AtomicBoolean(false);
		for (Location location : lights) {
			Tasks.wait(wait.get(), () -> {
				if (!breakBool.get()) {
					location.getBlock().setType(Material.REDSTONE_BLOCK);
					count.getAndIncrement();
					if (count.get() == limit) {
						if (count.get() == max)
							one.getWorld().playSound(one, Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 2F);
						else
							one.getWorld().playSound(one, Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.1F);
						breakBool.set(true);
					}
				}
			});
			if (breakBool.get())
				break;
			wait.getAndAdd(7);
		}

		wait.getAndAdd(TickTime.SECOND.x(1));

		Tasks.wait(wait.get(), () -> {
			for (Location light : lights) {
				light.getBlock().setType(Material.AIR);
			}
			strengthTest_active = false;
		});
	}
}
