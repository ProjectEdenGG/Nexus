package gg.projecteden.nexus.features.events.y2020.pugmas20;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.pugmas20.Pugmas20User;
import gg.projecteden.nexus.models.pugmas20.Pugmas20UserService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils.Paster;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20.location;
import static gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20.worldedit;

/*
-890, -2186
 935,   482
1826  2666
 */
public class Pugmas20Train {
	// Options
	private static final Location origin = location(900, 52, 375);
	private static final int trainFrameTime = 0;
	private static final int crossingFrameTime = 4;
	private static final int crossingThreshold = 30;
	//private static final int stationStopTime = TickTime.SECOND.x(15);
	// Don't change anything below this
	//
	@Getter
	@Accessors(fluent = true)
	private static boolean animating = false;
	private static boolean stopAtStation = false;
	private static boolean stopped = false;
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
	private static final Location trackStart = location(x - 94, y, z);
	private static final Location trainEnter = location(x + 13, y, z);
	private static final Location trainExit = location(x, y, z);
	private static final Location trainStart = location(x + 13, y, z);
	private static final Location crossingSE = location(x + 71, y, z + 6);
	private static final Location crossingNE = location(x + 79, y, z - 6);
	private static final Location crossingSW = location(x - 47, y, z + 6);
	private static final Location crossingNW = location(x - 39, y, z - 6);
	// Lights
	private static final int lightY = crossingSW.getBlockY() + 5;
	private static final List<Location> crossingLights1_1 = Arrays.asList(
			location(crossingSW.getBlockX(), lightY, crossingSW.getBlockZ() - 1),
			location(crossingNW.getBlockX(), lightY, crossingNW.getBlockZ() + 1));
	private static final List<Location> crossingLights1_2 = Arrays.asList(
			location(crossingSW.getBlockX() + 2, lightY, crossingSW.getBlockZ() - 1),
			location(crossingNW.getBlockX() - 2, lightY, crossingNW.getBlockZ() + 1));
	private static final List<Location> crossingLights2_1 = Arrays.asList(
			location(crossingSE.getBlockX(), lightY, crossingSE.getBlockZ() - 1),
			location(crossingNE.getBlockX(), lightY, crossingNE.getBlockZ() + 1));
	private static final List<Location> crossingLights2_2 = Arrays.asList(
			location(crossingSE.getBlockX() + 2, lightY, crossingSE.getBlockZ() - 1),
			location(crossingNE.getBlockX() - 2, lightY, crossingNE.getBlockZ() + 1));
	// Smoke
	private static final AtomicReference<Block> trackLoc = new AtomicReference<>(trackStart.clone().getBlock());
	private static final AtomicReference<Block> smokeLoc = new AtomicReference<>(trackLoc.get().getRelative(-9, 8, 0));
	// Misc
	private static final String animationPath = "Animations/Pugmas20/Train";

	public Pugmas20Train() {
//		Tasks.repeat(TickTime.SECOND.x(30), TickTime.MINUTE.x(5), () -> {
//			if (Pugmas20.worldguard().getPlayersInRegion("pugmas20").size() == 0)
//				return;
//
//			if (RandomUtils.chanceOf(50))
//				animate();
//		});
//
//		lightsTask();
//		soundsTask();
	}

	public static void animate() {
		if (animating)
			return;

		if (Bukkit.getTPS()[0] < 19)
			return;

		animating = true;
		stopAtStation = false;
		crossing1_closed = false;
		crossing2_closed = false;

		Queue<Paster> pasters = new LinkedList<>();

		for (int i = 1; i <= 109; i++)
			pasters.add(worldedit().paster().file(animationPath + "/Enter/TrainEnter_" + i).at(trainEnter));

		for (int i = 1; i <= 95; i++)
			pasters.add(worldedit().paster().file(animationPath + "/Train").at(trainStart.getBlock().getRelative(i, 0, 0).getLocation()));

		for (int i = 1; i <= 110; i++)
			pasters.add(worldedit().paster().file(animationPath + "/Exit/TrainExit_" + i).at(trainExit));

		Tasks.async(() -> animate(pasters));
	}

	private static void animate(Queue<Paster> pasters) {
		Paster paster = pasters.poll();
		if (paster == null) {
			Tasks.waitAsync(trainFrameTime, Pugmas20Train::resetTrain);
			return;
		}

		paster.pasteAsync();
		incrementTrain();
		Tasks.waitAsync(trainFrameTime, () -> animate(pasters));
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
		stopped = false;
		stopAtStation = false;
		switchLightsOff();
	}

	private static void incrementTrain() {
		trackNdx.getAndIncrement();
		trackLoc.set(trackLoc.get().getRelative(1, 0, 0));

		smokeLoc.set(smokeLoc.get().getRelative(1, 0, 0));
		World world = Pugmas20.getWorld();
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
		} else if (trackNdx.get() >= crossing2Ndx && !crossing2_closed && !stopAtStation)
			animateCrossing(2, false);

	}

	private static Location getSmokeLoc() {
		Location loc = LocationUtils.getCenteredLocation(smokeLoc.get().getLocation());
		loc.setY(loc.getY() - 0.5);
		loc.setX(loc.getX() + RandomUtils.randomDouble(-0.25, 0.25));
		loc.setZ(loc.getZ() + RandomUtils.randomDouble(-0.25, 0.25));
		return loc;
	}

	private static void animateCrossings(Queue<Paster> pasters, Runnable onComplete) {
		if (!animateCrossing(pasters, onComplete)) return;
		if (!animateCrossing(pasters, onComplete)) return;

		Tasks.waitAsync(crossingFrameTime, () -> animateCrossings(pasters, onComplete));
	}

	private static boolean animateCrossing(Queue<Paster> pasters, Runnable onComplete) {
		Paster paster = pasters.poll();
		if (paster == null) {
			if (onComplete != null)
				onComplete.run();
			return false;
		}

		paster.build();
		return true;
	}

	private static void animateCrossing(int crossing, boolean open) {
		Queue<Paster> pasters = new LinkedList<>();
		Runnable onComplete = null;

		if (crossing == 1) {
			if (open) {
				crossing1_closed = false;
				onComplete = () -> animateLights1 = false;

				for (int i = 1; i <= 7; i++) {
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/North_Opening_" + i).at(crossingNW));
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/South_Opening_" + i).at(crossingSW));
				}
			} else {
				crossing1_closed = true;
				animateLights1 = true;

				for (int i = 1; i <= 7; i++) {
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/North_Closing_" + i).at(crossingNW));
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/South_Closing_" + i).at(crossingSW));
				}
			}
		} else if (crossing == 2) {
			if (open) {
				crossing2_closed = false;
				onComplete = () -> animateLights2 = false;

				for (int i = 1; i <= 7; i++) {
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/North_Opening_" + i).at(crossingNE));
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/South_Opening_" + i).at(crossingSE));
				}
			} else {
				crossing2_closed = true;
				animateLights2 = true;

				for (int i = 1; i <= 7; i++) {
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/North_Closing_" + i).at(crossingNE));
					pasters.add(worldedit().paster().file(animationPath + "/Crossing/South_Closing_" + i).at(crossingSE));
				}
			}
		}

		animateCrossings(pasters, onComplete);
	}

	private static void lightsTask() {
		Tasks.repeatAsync(0, TickTime.TICK.x(20), () -> {
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
		location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BELL, .5F, pitch);
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
		Tasks.repeatAsync(0, TickTime.SECOND.x(2), () -> {
			if (!animating)
				return;

			Block origin = trackLoc.get();
			Location front = origin.getLocation();
			Location middle = origin.getRelative(-(trainLength / 2), 0, 0).getLocation();
			Location back = origin.getRelative(-trainLength, 0, 0).getLocation();
			Collection<Player> players = Pugmas20.worldguard().getPlayersInRegion("pugmas20_trainsound");

			if (stopped) {
				playStationSound(front, players);
				playStationSound(middle, players);
				playStationSound(back, players);
			} else {
				if (stopAtStation) {
					if (trackNdx.get() <= 165 - (trainFrameTime * 10))
						playTrainSound(front, players);
				} else {
//					if (trackNdx.get() <= (202 + (trainFrameTime * 10)))
					if (trackNdx.get() <= (202 + (trainLength - (trainLength / 3))))
						playTrainSound(front, players);
				}
			}
		});
	}

	private static void playTrainSound(Location location, Collection<Player> players) {
		float volume = .1F;
		float pitch = 0.1F;
		players.forEach(player -> {
			if (isTrainMuted(player))
				return;

			player.playSound(location, Sound.ENTITY_MINECART_INSIDE, SoundCategory.AMBIENT, volume, pitch);
		});
	}

	private static void playStationSound(Location location, Collection<Player> players) {
		float volume = 6F;
		float pitch = 0.1F;
		players.forEach(player -> {
			if (isTrainMuted(player))
				return;

			player.playSound(location, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.AMBIENT, volume, pitch);
		});
	}

	private static boolean isTrainMuted(Player player) {
		Pugmas20UserService service = new Pugmas20UserService();
		Pugmas20User pugmasUser = service.get(player);
		return pugmasUser.isMuteTrain();
	}

}
