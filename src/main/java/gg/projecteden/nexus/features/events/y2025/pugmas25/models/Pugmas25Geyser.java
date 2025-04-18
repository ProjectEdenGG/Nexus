package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25.Pugmas25DeathCause;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/*
	TODO:
		- GEYSER ERUPTING GIVES BETTER CHANCES TO FISHING?
		- TELL PLAYERS THAT GEYSER HELP WITH ELYTRA
		- MAYBE SOME POINTED DRIPSTONE FALL IN THE DRIPSTONE CAVE BEHIND THE GEYSER, when it's building pressure?
 */
@NoArgsConstructor
public class Pugmas25Geyser {

	private static final Pugmas25 PUGMAS = Pugmas25.get();
	private static final WorldEditUtils worldedit = PUGMAS.worldedit();
	private static final WorldGuardUtils worldguard = PUGMAS.worldguard();

	@Getter
	private static boolean animating = false;
	private static boolean erupting = false;
	private static boolean hurtPlayers = false;
	private static GeyserStatus status = GeyserStatus.INACTIVE;
	private static List<Location> base;

	//
	public static final Location GEYSER_ORIGIN = PUGMAS.location(-501, 93, -3046);
	public static final Location GEYSER_PASTE = PUGMAS.location(-505, 93, -3050);
	private static final int MAX_HEIGHT = 8;
	private static final int FRAME_SPEED = 2;

	private static final String BASE_REGION = PUGMAS.getRegionName() + "_";
	private static final String POOLS_REGION = BASE_REGION + "geyser";
	private static final String EXTRA_SMOKE_REGION_REGEX = "^" + BASE_REGION + "geyser_smoke_[0-9]+";
	private static final String INSIDE_GEYSER_REGION = BASE_REGION + "geyser_inside";
	private static final String GEYSER_COLUMN_REGION = BASE_REGION + "geyser_column";

	private static final String SCHEM_FOLDER = "pugmas25/geyser/";
	private static final String SCHEM_RESET = SCHEM_FOLDER + "empty";
	private static final String SCHEM_RUNNING = SCHEM_FOLDER + "running_";
	private static final String SCHEM_INTRO = SCHEM_FOLDER + "intro_";
	private static final String SCHEM_OUTRO = SCHEM_FOLDER + "outro_";
	//
	private static final SoundBuilder geyserSound = new SoundBuilder(CustomSound.AMBIENT_GEYSER).volume(2).category(SoundCategory.AMBIENT).location(GEYSER_ORIGIN.clone());
	private static final SoundBuilder rumbleSound = new SoundBuilder(CustomSound.AMBIENT_GROUND_RUMBLE).volume(4).pitch(2).category(SoundCategory.AMBIENT).location(GEYSER_ORIGIN.clone().subtract(0, 2, 0));
	private static final SoundBuilder bubbleSound = new SoundBuilder(Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT).category(SoundCategory.AMBIENT).location(GEYSER_ORIGIN.clone());
	private static final SoundBuilder splashSound = new SoundBuilder(Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED).pitch(2).category(SoundCategory.AMBIENT).location(GEYSER_ORIGIN.clone());

	public static void reset() {
		erupting = false;
		status = GeyserStatus.INACTIVE;
		hurtPlayers = false;
		poolSmokeFailChance = 99;
		animating = false;
		worldedit.paster().file(SCHEM_RESET).at(GEYSER_PASTE).pasteAsync();
	}

	public static void animate() {
		if (animating)
			return;

		animating = true;

		base = new ArrayList<>(List.of(GEYSER_ORIGIN, relativeLoc(BlockFace.EAST),
				relativeLoc(BlockFace.SOUTH), relativeLoc(BlockFace.SOUTH_EAST)));
		Collections.shuffle(base);

		erupting = false;
		hurtPlayers = false;
		status = GeyserStatus.START_INTRO;
		players();

		Tasks.async(Pugmas25Geyser::incrementGeyser);
	}

	private static void players() {
		AtomicLong ticks = new AtomicLong();
		Tasks.repeat(0, 5, () -> {
			if (!animating)
				return;

			playSounds(ticks.get());

			ticks.addAndGet(5);

			if (hurtPlayers) {
				for (Player player : PUGMAS.getPlayersIn(POOLS_REGION)) {
					if (!isWater(player.getLocation()))
						continue;

					float damageAmount = 0.5f;
					double newHealth = player.getHealth() - damageAmount;
					if (newHealth <= 0) {
						Pugmas25.get().onDeath(player, Pugmas25DeathCause.GEYSER);
						continue;
					}

					NMSUtils.hurtPlayer(player, NMSUtils.getDamageSources(player).hotFloor(), damageAmount);
				}
			}

			if (erupting) {
				var players = PUGMAS.getPlayersIn(INSIDE_GEYSER_REGION);
				players.addAll(PUGMAS.getPlayersIn(GEYSER_COLUMN_REGION));

				Location geyserPushLoc = GEYSER_ORIGIN.clone();
				geyserPushLoc.add(0.5, 0, 0.5);
				for (Player player : players) {
					if (!isWater(player.getLocation()))
						continue;

					var fromLoc = geyserPushLoc.clone();
					if (player.getLocation().getBlockY() <= geyserPushLoc.getBlockY())
						fromLoc.setY(player.getLocation().getY() - 2);

					ItemStack chestplate = player.getInventory().getChestplate();
					double yVelocity = 2.5;
					if (Nullables.isNotNullOrAir(chestplate) && chestplate.getType() == Material.ELYTRA)
						yVelocity = 4;

					Vector launchVector = EntityUtils.getForcefieldVelocity(player, fromLoc, yVelocity);
					Vector randomDir = new Vector(RandomUtils.randomDouble(-0.5, 0.5), RandomUtils.randomDouble(0.2, 0.6), RandomUtils.randomDouble(-0.5, 0.5));
					launchVector.add(randomDir);
					player.setVelocity(launchVector);
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
		poolSmokeFailChance = 95;
		extraSmokeFailChance = 98;
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

		Tasks.wait(wait - TickTime.SECOND.x(1), () -> erupting = true);
		wait = geyserRaise(wait, FRAME_SPEED, MAX_HEIGHT, false);

		for (int frame = 1; frame <= 5; frame++) {
			int finalFrame = frame;
			Tasks.waitAsync(wait, () ->
				worldedit.paster().file(SCHEM_INTRO + finalFrame).at(GEYSER_PASTE).pasteAsync());

			wait += FRAME_SPEED;
		}

		Tasks.wait(wait, () -> {
			status = GeyserStatus.START_RUNNING;
			Tasks.async(Pugmas25Geyser::incrementGeyser);
		});
	}

	private static void startRunning() {
		status = GeyserStatus.ANIMATING;
		erupting = true;

		long wait = 0;

		int loops = RandomUtils.randomInt(10, 30);
		for (int i = 0; i < loops; i++) {
			for (int frame = 1; frame <= 7; frame++) {
				int finalFrame = frame;
				Tasks.waitAsync(wait, () ->
					worldedit.paster().file(SCHEM_RUNNING + finalFrame).at(GEYSER_PASTE).pasteAsync());

				wait += FRAME_SPEED;
			}
		}

		Tasks.wait(wait, () -> {
			status = GeyserStatus.START_OUTRO;
			Tasks.async(Pugmas25Geyser::incrementGeyser);
		});
	}

	private static void startOutro() {
		status = GeyserStatus.ANIMATING;
		erupting = true;

		long wait = 0;
		for (int frame = 1; frame <= 13; frame++) {
			int finalFrame = frame;
			Tasks.waitAsync(wait, () ->
				worldedit.paster().file(SCHEM_OUTRO + finalFrame).at(GEYSER_PASTE).pasteAsync());

			wait += FRAME_SPEED;
		}

		Tasks.waitAsync(wait, () ->
			worldedit.paster().file(SCHEM_RESET).at(GEYSER_PASTE).pasteAsync());

		Tasks.wait(wait - TickTime.SECOND.x(2), () -> erupting = false);
		Tasks.wait(wait, () -> {
			status = GeyserStatus.ENDING;
			Tasks.async(Pugmas25Geyser::incrementGeyser);
		});
	}

	private static void incrementGeyser() {
		switch (status) {
			case ANIMATING, INACTIVE -> {
				return;
			}

			case START_INTRO -> {
				startIntro();
				return;
			}

			case START_RUNNING -> {
				startRunning();
				return;
			}

			case START_OUTRO -> {
				startOutro();
				return;
			}

			case ENDING -> {
				reset();
				return;
			}
		}

		Tasks.async(Pugmas25Geyser::incrementGeyser);
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

	private static Set<Location> poolSmokeLocations = new HashSet<>();
	private static Set<Location> extraSmokeLocations = new HashSet<>();
	private static int extraSmokeFailChance = 99;
	private static int poolSmokeFailChance = 99;

	public static void animateSmoke() {
		if (Nexus.isMaintenanceQueued())
			return;

		if (Nullables.isNullOrEmpty(poolSmokeLocations)) {
			poolSmokeLocations = getSmokeLocations();
		}

		if (Nullables.isNullOrEmpty(extraSmokeLocations)) {
			extraSmokeLocations = getExtraSmokeLocations();
		}

		playSmoke(extraSmokeLocations, extraSmokeFailChance, 1, 3);
		playSmoke(poolSmokeLocations, poolSmokeFailChance, 3, 4);
	}

	private static void playSmoke(Set<Location> creekSmokeLocations, int smokeFailChance, int heightMin, int heightMax) {
		for (Location location : creekSmokeLocations) {
			if (RandomUtils.chanceOf(smokeFailChance))
				continue;

			new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
				.location(location.clone().toCenterLocation().add(0, 0.4, 0))
				.count(0)
				.offset(0, RandomUtils.randomInt(heightMin, heightMax), 0)
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
		return Pugmas25Geyser.GEYSER_ORIGIN.getBlock().getRelative(blockFace).getLocation();
	}

	private static Set<Location> getExtraSmokeLocations() {
		Set<ProtectedRegion> smokeRegions = worldguard.getRegionsLike(EXTRA_SMOKE_REGION_REGEX);
		List<Block> blocks = new ArrayList<>();
		for (ProtectedRegion region : smokeRegions) {
			if (region.getType() == RegionType.POLYGON)
				blocks.addAll(worldedit.getBlocksPoly(region));
			else
				blocks.addAll(worldedit.getBlocks(region));
		}

		return blocks.stream()
			.filter(block -> block.getType() == Material.WATER)
			.filter(block -> block.getRelative(BlockFace.UP).getType() == Material.AIR)
			.map(Block::getLocation)
			.collect(Collectors.toSet());
	}

	private static Set<Location> getSmokeLocations() {
		return worldedit.getBlocksPoly(worldguard.getRegion(POOLS_REGION)).stream()
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
