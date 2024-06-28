package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Geyser implements Listener {

	@Getter
	private static boolean animating = false;
	private static boolean erupting = false;
	private static boolean hurtPlayers = false;
	private static GeyserStatus status = GeyserStatus.INACTIVE;
	private static List<Location> base;
	//
	public static final Location geyserOrigin = Pugmas24.get().location(-501, 93, -3046); // TODO: FINAL LOC, NORTH WEST CORNER
	private static final int maxHeight = 8;
	private static final Levelled splashData = (Levelled) Material.WATER.createBlockData();
	private static final String geyserPoolsRegion = Pugmas24.get().getRegionName() + "_geyser";
	private static final String geyserInsideRegion = Pugmas24.get().getRegionName() + "_geyser_inside";
	private static final String geyserColumnRegion = Pugmas24.get().getRegionName() + "_geyser_column";
	//
	private static final SoundBuilder geyserSound = new SoundBuilder(CustomSound.AMBIENT_GEYSER).volume(2).location(geyserOrigin.clone());
	private static final SoundBuilder rumbleSound = new SoundBuilder(CustomSound.AMBIENT_GROUND_RUMBLE).volume(4).pitch(2).location(geyserOrigin.clone().subtract(0, 2, 0));
	private static final SoundBuilder bubbleSound = new SoundBuilder(Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT).location(geyserOrigin.clone());
	private static final SoundBuilder splashSound = new SoundBuilder(Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED).pitch(2).location(geyserOrigin.clone());

	public static void reset() {
		erupting = false;
		status = GeyserStatus.INACTIVE;
		hurtPlayers = false;
		smokeFailChance = 99;
		animating = false;
		// TODO: delete any leftover blocks
	}

	public static void animate() {
		if (animating)
			return;

		animating = true;

		base = new ArrayList<>(List.of(geyserOrigin, relativeLoc(BlockFace.EAST),
				relativeLoc(BlockFace.SOUTH), relativeLoc(BlockFace.SOUTH_EAST)));
		Collections.shuffle(base);

		erupting = false;
		hurtPlayers = false;
		status = GeyserStatus.START_INTRO;
		splashData.setLevel(7);
		players();

		Tasks.async(Geyser::incrementGeyser);
	}

	private static void players() {
		AtomicLong ticks = new AtomicLong();
		Tasks.repeat(0, 5, () -> {
			if (!animating)
				return;

			playSounds(ticks.get());

			ticks.addAndGet(5);

			if (hurtPlayers) {
				for (Player player : Pugmas24.get().getPlayersIn(geyserPoolsRegion)) {
					if (!isWater(player.getLocation()))
						continue;

					float damageAmount = 0.5f;
					double newHealth = player.getHealth() - damageAmount;
					if (newHealth <= 0)
						continue; // TODO: INSTEAD FAKE KILL THE PLAYER - RESPAWN SOMEWHERE IN PUGMAS?

					NMSUtils.hurtPlayer(player, NMSUtils.getDamageSources(player).hotFloor(), damageAmount);
				}
			}

			if (erupting) {
				var players = Pugmas24.get().getPlayersIn(geyserInsideRegion);
				players.addAll(Pugmas24.get().getPlayersIn(geyserColumnRegion));

				Location geyserPushLoc = geyserOrigin.clone();
				for (Player player : players) {
					if (!isWater(player.getLocation()))
						continue;

					var fromLoc = geyserPushLoc.clone();
					if (player.getLocation().getBlockY() <= geyserPushLoc.getBlockY())
						fromLoc.setY(player.getLocation().getY() - 1);

					player.setVelocity(EntityUtils.getForcefieldVelocity(player, fromLoc, 1.5));
				}
			}
		});
	}

	private static void playSounds(long ticks) {
		boolean mod3 = ticks % TickTime.SECOND.x(3) == 0;
		boolean mod2 = ticks % TickTime.SECOND.x(2) == 0;

		if (mod3) rumbleSound.play();
		if (mod2) bubbleSound.play();

		if (erupting) {
			if (mod2) geyserSound.play();
			if (mod3) geyserSound.play();
		}
	}

	private static void startIntro() {
		status = GeyserStatus.ANIMATING;
		hurtPlayers = true;
		smokeFailChance = 95;
		long wait = 0;

		wait += TickTime.SECOND.x(7);

		int teaseSpeed = 6;
		int teaseHeight = RandomUtils.randomInt(2, 4);

		if (RandomUtils.chanceOf(50)) {
			Tasks.wait(wait - TickTime.SECOND.x(1), () -> erupting = true);
			wait = geyserRaise(wait, teaseSpeed, teaseHeight, true);

			wait += TickTime.SECOND.x(2);

			wait = geyserLower(wait, teaseSpeed, teaseHeight);

			Tasks.wait(wait - TickTime.SECOND.x(2), () -> erupting = false);
			Tasks.wait(wait, () -> hurtPlayers = false);

			wait += TickTime.SECOND.x(3);
		}

		int speedTicks = 2;
		Tasks.wait(wait - TickTime.SECOND.x(1), () -> erupting = true);
		wait = geyserRaise(wait, speedTicks, maxHeight, false);

		Tasks.wait(wait, () -> {
			status = GeyserStatus.START_RUNNING;
			Tasks.async(Geyser::incrementGeyser);
		});
	}

	private static void startRunning() {
		status = GeyserStatus.ANIMATING;
		erupting = true;

		long wait = TickTime.SECOND.x(10);

		// TODO: ANIMATION

		Tasks.wait(wait, () -> {
			status = GeyserStatus.START_OUTRO;
			Tasks.async(Geyser::incrementGeyser);
		});
	}

	private static void startOutro() {
		status = GeyserStatus.ANIMATING;
		erupting = true;
		int speedTicks = 5;

		long wait = 0;
		wait = geyserLower(wait, speedTicks, maxHeight);

		Tasks.wait(wait - TickTime.SECOND.x(2), () -> erupting = false);
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

	private static long geyserRaise(long wait, int speedTicks, int height, boolean variation) {
		Tasks.wait(wait, () -> {
			hurtPlayers = true;
			splashSound.play();
		});

		for (int relativeY = 0; relativeY < height; relativeY++) {
			long varWait = wait;
			for (Location location : base) {
				Location waterLoc = location.clone().add(0, relativeY, 0);
				long _wait = wait;
				if (variation)
					_wait = varWait;

				Tasks.wait(_wait, () ->
						Tasks.sync(() ->
								waterLoc.getBlock().setType(Material.WATER, false)));


				varWait += 1;
			}

			wait += speedTicks;
		}
		return wait;
	}

	private static long geyserLower(long wait, int speedTicks, int height) {
		for (int relativeY = height; relativeY >= 0; relativeY--) {
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

		return wait;
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

	private static boolean isWater(Location location) {
		Block block = location.getBlock();
		if (Nullables.isNullOrAir(block))
			return false;

		if (block.getType() == Material.WATER)
			return true;

		if (!(block.getBlockData() instanceof Waterlogged waterlogged))
			return false;

		return waterlogged.isWaterlogged();
	}

}
