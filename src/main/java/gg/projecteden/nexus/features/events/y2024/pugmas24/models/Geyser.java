package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: SOUNDS
public class Geyser implements Listener {

	@Getter
	private static boolean animating = false;
	private static GeyserStatus status = GeyserStatus.INACTIVE;
	private static List<Location> base;
	//
	private static final int maxHeight = 8;
	private static final Levelled splashData = (Levelled) Material.WATER.createBlockData();
	private static final String geyserPoolsRegion = Pugmas24.get().getRegionName() + "_geyser";
	public static final Location geyserOrigin = Pugmas24.get().location(-501, 93, -3046); // TODO: FINAL LOC, NORTH WEST CORNER

	public static void stopAnimating() {
		status = GeyserStatus.INACTIVE;
		animating = false;
	}

	public static void animate() {
		if (!Pugmas24.get().hasPlayers())
			return;

		if (animating)
			return;

		base = new ArrayList<>(List.of(geyserOrigin, relativeLoc(geyserOrigin, BlockFace.EAST),
				relativeLoc(geyserOrigin, BlockFace.SOUTH), relativeLoc(geyserOrigin, BlockFace.SOUTH_EAST)));
		Collections.shuffle(base);

		animating = true;
		status = GeyserStatus.START_INTRO;
		splashData.setLevel(7);
		hurtPlayers();

		Tasks.async(Geyser::incrementGeyser);
	}

	private static void hurtPlayers() {
		Tasks.repeat(0, 5, () -> {
			if (!animating)
				return;

			for (Player player : Pugmas24.get().getPlayersIn(geyserPoolsRegion)) {
				Block block = player.getLocation().getBlock();
				if (Nullables.isNullOrAir(block))
					continue;

				if (block.getType() != Material.WATER) {
					if (!(block.getBlockData() instanceof Waterlogged waterlogged))
						continue;

					if (!waterlogged.isWaterlogged())
						continue;
				}

				float damageAmount = 0.5f;
				double newHealth = player.getHealth() - damageAmount;
				if (newHealth <= 0)
					continue; // TODO: INSTEAD FAKE KILL THE PLAYER - RESPAWN SOMEWHERE IN PUGMAS?

				NMSUtils.hurtPlayer(player, NMSUtils.getDamageSources(player).hotFloor(), damageAmount);
			}
		});
	}

	private static void startIntro() {
		status = GeyserStatus.ANIMATING;
		int speedTicks = 4;

		long wait = 0;
		for (int relativeY = 0; relativeY < maxHeight; relativeY++) {
			long subWait = wait;
			for (Location location : base) {
				Location waterLoc = location.clone().add(0, relativeY, 0);

				Tasks.wait(subWait, () ->
						Tasks.sync(() ->
								waterLoc.getBlock().setType(Material.WATER, false)));

				subWait += 1;
			}

			wait += speedTicks;
		}

		Tasks.wait(wait, () -> {
			status = GeyserStatus.START_RUNNING;
			Tasks.async(Geyser::incrementGeyser);
		});
	}

	private static void startRunning() {
		status = GeyserStatus.ANIMATING;

		long wait = TickTime.SECOND.x(10);

		// TODO: ANIMATION

		Tasks.wait(wait, () -> {
			status = GeyserStatus.START_OUTRO;
			Tasks.async(Geyser::incrementGeyser);
		});
	}

	private static void startOutro() {
		status = GeyserStatus.ANIMATING;
		int speedTicks = 5;

		long wait = 0;
		for (int relativeY = maxHeight; relativeY >= 0; relativeY--) {
			long subWait = wait;
			for (Location location : base) {
				Location waterLoc = location.clone().add(0, relativeY, 0);
				Tasks.wait(subWait, () ->
						Tasks.sync(() ->
								waterLoc.getBlock().setType(Material.AIR, false)));

				subWait += 2;
			}

			wait += speedTicks;
		}

		wait += speedTicks;
		Tasks.wait(wait, () -> {
			status = GeyserStatus.ENDING;
			Tasks.async(Geyser::incrementGeyser);
		});
	}

	private static void incrementGeyser() {
		if (status == GeyserStatus.ANIMATING)
			return;

		if (status == GeyserStatus.START_INTRO) {
			startIntro();
			return;
		}

		if (status == GeyserStatus.START_RUNNING) {
			startRunning();
			return;
		}

		if (status == GeyserStatus.START_OUTRO) {
			startOutro();
			return;
		}

		if (status == GeyserStatus.ENDING) {
			stopAnimating();
			return;
		}

		Tasks.async(Geyser::incrementGeyser);
	}

	//

	private enum GeyserStatus {
		START_INTRO,
		START_RUNNING,
		START_OUTRO,
		ANIMATING,
		ENDING,
		INACTIVE,
		;
	}

	private static Location relativeLoc(Location origin, BlockFace blockFace) {
		return origin.getBlock().getRelative(blockFace).getLocation();
	}

}
