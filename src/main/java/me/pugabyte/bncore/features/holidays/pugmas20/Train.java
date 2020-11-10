package me.pugabyte.bncore.features.holidays.pugmas20;

import me.pugabyte.bncore.utils.LocationUtils;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.SoundUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Train {
	// Options
	private static final World world = Bukkit.getWorld("buildadmin");
	private static final Location origin = pugmasLoc(-926, 17, -2294);
	private static final int frameTime = 3;
	private static final int crossingThreshold = 30;
	// Don't change anything below this
	//
	private static boolean animating = false;
	//
	// Track Stuff
	private static final int trainLength = 108;
	private static final int crossing1Ndx = 52 - crossingThreshold;
	private static final int crossing2Ndx = 170 - crossingThreshold;
	private static final AtomicInteger trackNdx = new AtomicInteger(0);
	private static boolean crossing1_closed = false;
	private static boolean crossing2_closed = false;
	private static boolean animateLights1 = false;
	private static boolean animateLights2 = false;
	// Location Stuff
	private static final int x = origin.getBlockX();
	private static final int y = origin.getBlockY();
	private static final int z = origin.getBlockZ();
	private static final Location trackStart = pugmasLoc(x - 94, y, z);
	private static final Location trainEnter = pugmasLoc(x + 13, y, z);
	private static final Location trainExit = pugmasLoc(x, y, z);
	private static final Location trainStart = pugmasLoc(x + 14, y, z);
	private static final Location crossingSE = pugmasLoc(x + 71, y, z + 6);
	private static final Location crossingNE = pugmasLoc(x + 79, y, z - 6);
	private static final Location crossingSW = pugmasLoc(x - 47, y, z + 6);
	private static final Location crossingNW = pugmasLoc(x - 39, y, z - 6);
	// Lights
	private static final int lightY = crossingSW.getBlockY() + 5;
	private static final List<Location> crossingLights1_1 = Arrays.asList(
			pugmasLoc(crossingSW.getBlockX(), lightY, crossingSW.getBlockZ() - 1),
			pugmasLoc(crossingNW.getBlockX(), lightY, crossingNW.getBlockZ() + 1));
	private static final List<Location> crossingLights1_2 = Arrays.asList(
			pugmasLoc(crossingSW.getBlockX() + 2, lightY, crossingSW.getBlockZ() - 1),
			pugmasLoc(crossingNW.getBlockX() - 2, lightY, crossingNW.getBlockZ() + 1));
	private static final List<Location> crossingLights2_1 = Arrays.asList(
			pugmasLoc(crossingSE.getBlockX(), lightY, crossingSE.getBlockZ() - 1),
			pugmasLoc(crossingNE.getBlockX(), lightY, crossingNE.getBlockZ() + 1));
	private static final List<Location> crossingLights2_2 = Arrays.asList(
			pugmasLoc(crossingSE.getBlockX() + 2, lightY, crossingSE.getBlockZ() - 1),
			pugmasLoc(crossingNE.getBlockX() - 2, lightY, crossingNE.getBlockZ() + 1));
	// Smoke
	private static final AtomicReference<Block> trackLoc = new AtomicReference<>(trackStart.clone().getBlock());
	private static final AtomicReference<Block> smokeLoc = new AtomicReference<>(trackLoc.get().getRelative(-8, 8, 0));
	// Misc
	private static final WorldEditUtils WEUtils = new WorldEditUtils(world);
	private static final String animationPath = "Animations/Pugmas20/Train";

	public Train() {
		// Task here on a random interval, to call animate
		lightsTask();
		soundsTask();
	}

	private static Location pugmasLoc(int x, int y, int z) {
		return new Location(world, x, y, z);
	}

	public static boolean animate() {
		if (animating)
			return false;

		animating = true;
		int wait = 0;
		crossing1_closed = false;
		crossing2_closed = false;

		for (int i = 1; i <= 109; i++) {
			int finalI = i;
			Tasks.wait(frameTime * i, () -> {
				WEUtils.paster().file(animationPath + "/Enter/TrainEnter_" + finalI).at(trainEnter).pasteAsync();

				incrementTrain();
			});

		}
		wait += (frameTime * 109);

		// TODO PUGMAS - add stop animation branch

		Tasks.wait(wait, () -> {
			AtomicReference<Location> temp = new AtomicReference<>(trainStart.clone());
			for (int i = 1; i <= 95; i++) {
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file(animationPath + "/Train").at(temp.get()).pasteAsync();
					temp.set(temp.get().getBlock().getRelative(1, 0, 0).getLocation());

					incrementTrain();
				});
			}
		});
		wait += (frameTime * 95);

		Tasks.wait(wait, () -> {
			for (int i = 1; i <= 110; i++) {
				int finalI = i;
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file(animationPath + "/Exit/TrainExit_" + finalI).at(trainExit).pasteAsync();

					incrementTrain();
				});

			}
		});
		wait += (frameTime * 110);

		Tasks.wait(wait + frameTime, Train::resetTrain);

		return true;
	}

	private static void resetTrain() {
		trackNdx.set(0);
		trackLoc.set(trackStart.clone().getBlock());
		smokeLoc.set(trackLoc.get().getRelative(-8, 8, 0));

		if (crossing1_closed)
			animateCrossing(1, true);
		if (crossing2_closed)
			animateCrossing(2, true);

		animating = false;
		switchLightsOff();
	}

	private static void incrementTrain() {
		trackNdx.getAndIncrement();
		trackLoc.set(trackLoc.get().getRelative(1, 0, 0));

		smokeLoc.set(smokeLoc.get().getRelative(1, 0, 0));
		if (world != null) {
			double x = 0;
			double y = 3;
			double z = 0;
			double speed = 0.01;
			int count = 0;
			Particle smoke = Particle.CAMPFIRE_COSY_SMOKE;

			world.spawnParticle(smoke, getSmokeLoc(), count, x, y, z, speed);
			world.spawnParticle(smoke, getSmokeLoc(), count, x, y, z, speed);
			Tasks.wait(1, () -> {
				world.spawnParticle(smoke, getSmokeLoc(), count, x, y, z, speed);
				world.spawnParticle(smoke, getSmokeLoc(), count, x, y, z, speed);
				Tasks.wait(1, () -> {
					world.spawnParticle(smoke, getSmokeLoc(), count, x, y, z, speed);
					world.spawnParticle(smoke, getSmokeLoc(), count, x, y, z, speed);
				});
			});
		}

		if (trackNdx.get() >= crossing1Ndx + crossingThreshold + trainLength + 5) {
			if (crossing1_closed)
				animateCrossing(1, true);
		} else if (trackNdx.get() >= crossing1Ndx && !crossing1_closed) {
			animateCrossing(1, false);
		}

		if (trackNdx.get() >= crossing2Ndx + crossingThreshold + trainLength + 5) {
			if (crossing2_closed)
				animateCrossing(2, true);
		} else if (trackNdx.get() >= crossing2Ndx && !crossing2_closed)
			animateCrossing(2, false);

	}

	private static Location getSmokeLoc() {
		Location loc = LocationUtils.getCenteredLocation(smokeLoc.get().getLocation());
		loc.setY(loc.getY() - 0.5);
		loc.setX(loc.getX() + RandomUtils.randomDouble(-0.25, 0.25));
		loc.setZ(loc.getZ() + RandomUtils.randomDouble(-0.25, 0.25));
		return loc;
	}

	private static void animateCrossing(int crossing, boolean open) {
		if (crossing == 1) {
			if (open) {
				crossing1_closed = false;
				Tasks.wait(frameTime * 7, () -> animateLights1 = false);

				for (int i = 1; i <= 7; i++) {
					int finalI = i;
					Tasks.wait(frameTime * i, () -> {
						WEUtils.paster().file(animationPath + "/Crossing/North_Opening_" + finalI).at(crossingNW).pasteAsync();
						WEUtils.paster().file(animationPath + "/Crossing/South_Opening_" + finalI).at(crossingSW).pasteAsync();
					});
				}

			} else {
				crossing1_closed = true;
				animateLights1 = true;

				for (int i = 1; i <= 7; i++) {
					int finalI = i;
					Tasks.wait(frameTime * i, () -> {
						WEUtils.paster().file(animationPath + "/Crossing/North_Closing_" + finalI).at(crossingNW).pasteAsync();
						WEUtils.paster().file(animationPath + "/Crossing/South_Closing_" + finalI).at(crossingSW).pasteAsync();
					});
				}
			}
		} else if (crossing == 2) {
			if (open) {
				crossing2_closed = false;
				Tasks.wait(frameTime * 7, () -> animateLights2 = false);

				for (int i = 1; i <= 7; i++) {
					int finalI = i;
					Tasks.wait(frameTime * i, () -> {
						WEUtils.paster().file(animationPath + "/Crossing/North_Opening_" + finalI).at(crossingNE).pasteAsync();
						WEUtils.paster().file(animationPath + "/Crossing/South_Opening_" + finalI).at(crossingSE).pasteAsync();
					});
				}
			} else {
				crossing2_closed = true;
				animateLights2 = true;

				for (int i = 1; i <= 7; i++) {
					int finalI = i;
					Tasks.wait(frameTime * i, () -> {
						WEUtils.paster().file(animationPath + "/Crossing/North_Closing_" + finalI).at(crossingNE).pasteAsync();
						WEUtils.paster().file(animationPath + "/Crossing/South_Closing_" + finalI).at(crossingSE).pasteAsync();
					});
				}
			}
		}
	}

	private static void lightsTask() {
		Tasks.repeatAsync(0, Time.TICK.x(20), () -> {
			if (!animating)
				return;

			if (animateLights1) {
				switchLights(true, crossingLights1_1);
				switchLights(false, crossingLights1_2);
				lightSound(crossingNW, SoundUtils.getPitch(1));
				lightSound(crossingSW, SoundUtils.getPitch(1));

				Tasks.wait(10, () -> {
					switchLights(false, crossingLights1_1);
					switchLights(true, crossingLights1_2);
					lightSound(crossingNW, SoundUtils.getPitch(2));
					lightSound(crossingSW, SoundUtils.getPitch(2));
				});
			} else {
				switchLights(false, crossingLights1_1);
				switchLights(false, crossingLights1_2);
			}

			if (animateLights2) {
				switchLights(true, crossingLights2_1);
				switchLights(false, crossingLights2_2);
				lightSound(crossingNE, SoundUtils.getPitch(1));
				lightSound(crossingSE, SoundUtils.getPitch(1));

				Tasks.wait(10, () -> {
					switchLights(false, crossingLights2_1);
					switchLights(true, crossingLights2_2);
					lightSound(crossingNE, SoundUtils.getPitch(2));
					lightSound(crossingSE, SoundUtils.getPitch(2));
				});
			} else {
				switchLights(false, crossingLights2_1);
				switchLights(false, crossingLights2_2);
			}
		});
	}

	private static void lightSound(Location location, float pitch) {
		location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BELL, 1.5F, pitch);
	}

	private static void switchLightsOff() {
		List<Location> locs = new ArrayList<>(crossingLights1_1);
		locs.addAll(crossingLights1_2);
		locs.addAll(crossingLights2_1);
		locs.addAll(crossingLights2_2);

		for (Location loc : locs) {
			loc.getBlock().setType(Material.REDSTONE_LAMP);
		}
	}

	private static void switchLights(boolean powered, List<Location> lights) {
		Tasks.sync(() -> {
			for (Location light : lights) {
				if (powered)
					light.getBlock().setType(Material.SHROOMLIGHT);
				else
					light.getBlock().setType(Material.REDSTONE_LAMP);
			}
		});
	}

	private static void soundsTask() {
		Tasks.repeatAsync(0, Time.SECOND.x(2), () -> {
			if (!animating)
				return;

			Location front = trackLoc.get().getLocation();
			Location middle = front.getBlock().getRelative(-(trainLength / 2), 0, 0).getLocation();
			Location back = front.getBlock().getRelative(-trainLength, 0, 0).getLocation();

			if (trackNdx.get() <= 202)
				playTrainSound(front);

			if (trackNdx.get() <= (202 + (trainLength / 2)))
				playTrainSound(middle);

			if (trackNdx.get() <= (202 + trainLength))
				playTrainSound(back);
		});
	}

	private static void playTrainSound(Location location) {
		float volume = 2F;
		float pitch = 0.5F;
		location.getWorld().playSound(location, Sound.ENTITY_MINECART_INSIDE, SoundCategory.AMBIENT, volume, pitch);
	}
}
