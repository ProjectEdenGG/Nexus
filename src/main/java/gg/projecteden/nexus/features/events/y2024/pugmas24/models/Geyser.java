package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Geyser implements Listener {

	@Getter
	private static boolean animating = false;
	private static boolean hurtPlayers = false;
	private static GeyserStatus status = GeyserStatus.INACTIVE;
	private static List<Location> base;
	//
	private static final int maxHeight = 8;
	private static final Levelled splashData = (Levelled) Material.WATER.createBlockData();
	private static final String geyserPoolsRegion = Pugmas24.get().getRegionName() + "_geyser";
	public static final Location geyserOrigin = Pugmas24.get().location(-501, 93, -3046); // TODO: FINAL LOC, NORTH WEST CORNER

	public static void reset() {
		status = GeyserStatus.INACTIVE;
		hurtPlayers = false;
		animating = false;
		smokeFailChance = 99;
	}

	public static void animate() {
		if (animating)
			return;

		base = new ArrayList<>(List.of(geyserOrigin, relativeLoc(BlockFace.EAST),
				relativeLoc(BlockFace.SOUTH), relativeLoc(BlockFace.SOUTH_EAST)));
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

			if (!hurtPlayers)
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
		hurtPlayers = true;
		smokeFailChance = 95;
		long wait = 0;
		// TODO: INTRO SOUND

		wait += TickTime.SECOND.x(2);

		int teaseHeight = RandomUtils.randomInt(2, 4);
		if (RandomUtils.chanceOf(50)) {
			int teaseSpeed = 6;
			for (int relativeY = 0; relativeY < teaseHeight; relativeY++) {
				long subWait = wait;
				for (Location location : base) {
					Location waterLoc = location.clone().add(0, relativeY, 0);

					Tasks.wait(subWait, () ->
							Tasks.sync(() ->
									waterLoc.getBlock().setType(Material.WATER, false)));

					subWait += 1;
				}

				wait += teaseSpeed;
			}

			wait += TickTime.SECOND.x(2);

			for (int relativeY = teaseHeight; relativeY >= 0; relativeY--) {
				long subWait = wait;
				for (Location location : base) {
					Location waterLoc = location.clone().add(0, relativeY, 0);
					Tasks.wait(subWait, () ->
							Tasks.sync(() ->
									waterLoc.getBlock().setType(Material.AIR, false)));

					subWait += 2;
				}

				wait += teaseSpeed;
			}

			wait += teaseSpeed;

			Tasks.wait(wait, () -> hurtPlayers = false);

			wait += TickTime.SECOND.x(3);
		}

		int speedTicks = 2;
		Tasks.wait(wait, () -> hurtPlayers = true);
		for (int relativeY = 0; relativeY < maxHeight; relativeY++) {
			for (Location location : base) {
				Location waterLoc = location.clone().add(0, relativeY, 0);

				Tasks.wait(wait, () ->
						Tasks.sync(() ->
								waterLoc.getBlock().setType(Material.WATER, false)));
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

		// TODO: SOUNDS
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
			reset();
			return;
		}

		Tasks.async(Geyser::incrementGeyser);
	}

	private enum GeyserStatus {
		START_INTRO,
		START_RUNNING,
		START_OUTRO,
		ANIMATING,
		ENDING,
		INACTIVE,
		;
	}

	//

	private static Set<Location> waterLocations = new HashSet<>();
	private static int smokeFailChance = 99;

	public static void animateSmoke() {
		if (Nullables.isNullOrEmpty(waterLocations)) {
			waterLocations = getWaterLocations();
		}

		for (Location location : waterLocations) {
			if (Nexus.isMaintenanceQueued())
				return;

			if (RandomUtils.chanceOf(smokeFailChance))
				continue;

			new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
					.location(location.clone().toCenterLocation().add(0, 0.4, 0))
					.count(0)
					.offset(0, RandomUtils.randomInt(3, 4), 0)
					.extra(RandomUtils.randomDouble(0.01, 0.03))
					.spawn();
		}
	}

	private static Location relativeLoc(BlockFace blockFace) {
		return Geyser.geyserOrigin.getBlock().getRelative(blockFace).getLocation();
	}

	private static Set<Location> getWaterLocations() {
		WorldEditUtils worldedit = Pugmas24.get().worldedit();
		WorldGuardUtils worldguard = Pugmas24.get().worldguard();

		ProtectedRegion protectedRegion = worldguard.getProtectedRegion(geyserPoolsRegion);
		Polygonal2DRegion polyRegion = (Polygonal2DRegion) worldguard.convert(protectedRegion);

		return worldedit.getBlocks(polyRegion, block -> polyRegion.contains(worldedit.toBlockVector3(block.getLocation()))).stream()
				.filter(block -> !Nullables.isNullOrAir(block))
				.filter(block -> block.getType() == Material.WATER)
				.filter(block -> block.getRelative(BlockFace.UP).getType() == Material.AIR)
				.map(Block::getLocation)
				.collect(Collectors.toSet());
	}

}
