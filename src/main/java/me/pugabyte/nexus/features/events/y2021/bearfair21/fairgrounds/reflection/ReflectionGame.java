package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.reflection;

import eden.utils.TimeUtils.Time;
import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.particles.effects.DotEffect;
import me.pugabyte.nexus.models.bearfair21.BearFair21User.BF21PointSource;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.NORTH_EAST;
import static org.bukkit.block.BlockFace.NORTH_WEST;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.SOUTH_EAST;
import static org.bukkit.block.BlockFace.SOUTH_WEST;
import static org.bukkit.block.BlockFace.WEST;

// TODO BF21
public class ReflectionGame {
	@Getter
	private static final String gameRg = BearFair21.getRegion() + "_reflection";
	@Getter
	private static final String powderRg = gameRg + "_powder";
	@Getter
	private static final String prefix = "&8&l[&eReflection&8&l] &f";
	@Getter
	private static final Location center = new Location(BearFair21.getWorld(), 49, 132, -93);
	@Getter
	private static final List<BlockFace> angles = Arrays.asList(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST);
	@Getter
	private static final BF21PointSource SOURCE = BF21PointSource.REFLECTION;
	//
	@Getter
	@Setter
	private static int laserTaskId;
	@Getter
	@Setter
	private static int soundTaskId;
	@Getter
	@Setter
	private static Location laserStart;
	@Getter
	@Setter
	private static Location laserSoundLoc;
	@Getter
	@Setter
	private static Player buttonPresser;
	//
	@Getter
	@Setter
	private static ReflectionGameLamp lamp = RandomUtils.randomElement(Arrays.asList(ReflectionGameLamp.values()));
	//	@Getter @Setter private static boolean max = false;
	@Getter
	@Setter
	private static int reflections;
	@Getter
	@Setter
	private static String message;
	@Getter
	@Setter
	private static boolean active = false;


	public ReflectionGame() {
		new Listeners();

		// New Objective
		ReflectionGameUtils.newObjective();
	}

	static void startLaser(Player player, BlockFace startFace) {
		active = true;
		ReflectionGameUtils.clearLamps();
		AtomicInteger cooldown = new AtomicInteger(5);
		AtomicInteger lifespan = new AtomicInteger(750);
		final BlockFace[] blockFace = {startFace};
		final Location[] loc = {laserStart.clone()};
		AtomicReference<Color> laserColor = new AtomicReference<>(Color.RED);
		AtomicInteger reflections = new AtomicInteger(0);
		BearFair21.getWorld().playSound(laserStart, Sound.BLOCK_BEACON_ACTIVATE, 2F, 1F);
		laserSound();

		laserTaskId = Tasks.repeat(0, 1, () -> {
			if (active) {
				laserSoundLoc = loc[0].clone();
				DotEffect.builder().player(player).location(loc[0].clone()).speed(0.1).ticks(10).color(laserColor.get()).start();
				Block block = loc[0].getBlock();
				Material blockType = block.getType();

				double middle = loc[0].getX() - loc[0].getBlockX();
				if (middle == 0.5 && !blockType.equals(Material.AIR) && cooldown.get() == 0) {
					boolean broadcast = true;
					if (blockType.equals(Material.REDSTONE_LAMP)) {
						if (ReflectionGameUtils.checkObjective(reflections.get(), block.getRelative(0, 1, 0).getType())) {
							BlockData blockData = block.getBlockData();
							Lightable lightable = (Lightable) blockData;
							lightable.setLit(true);
							block.setBlockData(lightable);

							Block block1 = block.getRelative(0, -6, 0);
							BlockData blockData1 = block1.getBlockData();
							Lightable lightable1 = (Lightable) blockData1;
							lightable1.setLit(true);
							block1.setBlockData(lightable1);
							ReflectionGameUtils.win(reflections.get());
							broadcast = false;
						}
					}
					if (broadcast)
						ReflectionGameUtils.broadcastObjective();
					endLaser();
					return;
				}

				Block below = block.getRelative(0, -1, 0);
				Material bannerType = below.getType();
				if (middle == 0.5 && MaterialTag.BANNERS.isTagged(bannerType) && cooldown.get() == 0) {
					loc[0] = LocationUtils.getCenteredLocation(loc[0]);
					loc[0].setY(loc[0].getY() + 0.25);
					Rotatable rotatable = (Rotatable) below.getBlockData();
					BlockFace newFace = ReflectionLaser.getReflection(blockFace[0], rotatable.getRotation());
					if (newFace == null) {
						endLaser();
						return;
					}
					if (!blockFace[0].equals(newFace))
						reflections.incrementAndGet();
					blockFace[0] = newFace;
					cooldown.set(5);
				}

				loc[0] = loc[0].clone().add(blockFace[0].getDirection().multiply(0.25));
				lifespan.getAndDecrement();

				if (cooldown.get() > 0) {
					cooldown.getAndDecrement();
				}

				int currentLife = lifespan.get();
				if (currentLife <= 0) {
					endLaser();
				}

				if (currentLife <= 300) {
					if (currentLife <= 100)
						laserColor.set(Color.YELLOW);
					else
						laserColor.set(Color.ORANGE);
				}
			} else {
				endLaser();
			}
		});
	}

	private static void laserSound() {
		soundTaskId = Tasks.repeat(0, Time.SECOND.x(5), () -> {
			Collection<Player> players = BearFair21.getWGUtils().getPlayersInRegion(gameRg);
			for (Player player : players)
				player.playSound(laserSoundLoc, Sound.BLOCK_BEACON_AMBIENT, 1F, 1F);
		});
	}

	private static void endLaser() {
		Tasks.cancel(laserTaskId);
		Tasks.cancel(soundTaskId);

		Collection<Player> players = BearFair21.getWGUtils().getPlayersInRegion(gameRg);
		for (Player player : players)
			player.stopSound(Sound.BLOCK_BEACON_AMBIENT);

		BearFair21.getWorld().playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
		Tasks.wait(Time.SECOND.x(2), () -> active = false);
	}
}
