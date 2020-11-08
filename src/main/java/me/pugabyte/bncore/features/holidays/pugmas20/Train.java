package me.pugabyte.bncore.features.holidays.pugmas20;

import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Train {
	// Options
	private static final World world = Bukkit.getWorld("buildadmin");
	private static final Location origin = new Location(world, 1045, 5, -922);
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
	private static boolean crossing1_closed = false;
	private static boolean crossing2_closed = false;
	// Location Stuff
	private static final int x = origin.getBlockX();
	private static final int y = origin.getBlockY();
	private static final int z = origin.getBlockZ();
	private static final Location trainEnter = new Location(world, x + 13, y, z);
	private static final Location trainExit = new Location(world, x, y, z);
	private static final Location trainStart = new Location(world, x + 14, y, z);
	private static final Location crossingSouthEast = new Location(world, x + 71, y, z + 6);
	private static final Location crossingNorthEast = new Location(world, x + 79, y, z - 6);
	private static final Location crossingSouthWest = new Location(world, x - 47, y, z + 6);
	private static final Location crossingNorthWest = new Location(world, x - 39, y, z - 6);
	//
	private static final WorldEditUtils WEUtils = new WorldEditUtils(world);

	public Train() {
		// Task here on a random interval, to call animate
	}

	public static boolean animate() {
		if (animating)
			return false;

		animating = true;
		int wait = 0;
		AtomicInteger trackNdx = new AtomicInteger(0);
		crossing1_closed = false;
		crossing2_closed = false;

		for (int i = 1; i <= 109; i++) {
			int finalI = i;
			Tasks.wait(frameTime * i, () -> {
				WEUtils.paster().file("Animations/Pugmas20/Train/Enter/TrainEnter_" + finalI).air(true).at(trainEnter).paste();

				trackNdx.getAndIncrement();
				if (trackNdx.get() >= crossing1Ndx && !crossing1_closed)
					animateCrossing1(false);
				if (trackNdx.get() >= crossing2Ndx && !crossing2_closed)
					animateCrossing2(false);
			});

		}
		wait += (frameTime * 109);

		Tasks.wait(wait, () -> {
			AtomicReference<Location> temp = new AtomicReference<>(trainStart.clone());
			for (int i = 1; i <= 95; i++) {
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file("Animations/Pugmas20/Train/Train").air(true).at(temp.get()).paste();
					temp.set(temp.get().getBlock().getRelative(1, 0, 0).getLocation());

					trackNdx.getAndIncrement();
					if (trackNdx.get() >= crossing1Ndx + crossingThreshold + trainLength + 5) {
						if (crossing1_closed)
							animateCrossing1(true);
					} else if (trackNdx.get() >= crossing1Ndx && !crossing1_closed) {
						animateCrossing1(false);
					}

					if (trackNdx.get() >= crossing2Ndx && !crossing2_closed)
						animateCrossing2(false);
				});
			}
		});
		wait += (frameTime * 95);

		Tasks.wait(wait, () -> {
			for (int i = 1; i <= 110; i++) {
				int finalI = i;
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file("Animations/Pugmas20/Train/Exit/TrainExit_" + finalI).air(true).at(trainExit).paste();

					trackNdx.getAndIncrement();
					if (trackNdx.get() >= crossing2Ndx + crossingThreshold + trainLength + 5) {
						if (crossing2_closed)
							animateCrossing2(true);
					} else if (trackNdx.get() >= crossing2Ndx && !crossing2_closed)
						animateCrossing2(false);
				});

			}
		});
		wait += (frameTime * 110);

		Tasks.wait(wait + frameTime, () -> {
			trackNdx.set(0);

			// Just in case
			if (crossing1_closed)
				animateCrossing1(true);
			if (crossing2_closed)
				animateCrossing2(true);

			animating = false;
		});

		return true;
	}

	private static void animateCrossing1(boolean open) {
		if (open) {
			crossing1_closed = false;

			for (int i = 1; i <= 7; i++) {
				int finalI = i;
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/North/North_Opening_" + finalI).air(true).at(crossingNorthWest).paste();
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/South/South_Opening_" + finalI).air(true).at(crossingSouthWest).paste();
				});
			}
		} else {
			crossing1_closed = true;

			for (int i = 1; i <= 7; i++) {
				int finalI = i;
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/North/North_Closing_" + finalI).air(true).at(crossingNorthWest).paste();
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/South/South_Closing_" + finalI).air(true).at(crossingSouthWest).paste();
				});
			}
		}
	}

	private static void animateCrossing2(boolean open) {
		if (open) {
			crossing2_closed = false;

			for (int i = 1; i <= 7; i++) {
				int finalI = i;
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/North/North_Opening_" + finalI).air(true).at(crossingNorthEast).paste();
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/South/South_Opening_" + finalI).air(true).at(crossingSouthEast).paste();
				});
			}
		} else {
			crossing2_closed = true;

			for (int i = 1; i <= 7; i++) {
				int finalI = i;
				Tasks.wait(frameTime * i, () -> {
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/North/North_Closing_" + finalI).air(true).at(crossingNorthEast).paste();
					WEUtils.paster().file("Animations/Pugmas20/Train/Crossing/South/South_Closing_" + finalI).air(true).at(crossingSouthEast).paste();
				});
			}
		}
	}


}
