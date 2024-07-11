package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.reflection;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import lombok.Setter;
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

public class Reflection {
	@Getter
	private static final String prefix = "&8&l[&eReflection&8&l] &f";

	@Getter
	private static final String gameRg = Pugmas24.get().getRegionName() + "_reflection";
	@Getter
	private static final String bannerRg = gameRg + "_banner"; // in gray glass
	@Getter
	private static final Location center = new Location(Pugmas24.get().getWorld(), -744, 75, -2936); // in gray glass
	@Getter
	private static final List<BlockFace> angles = Arrays.asList(NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST);

	// Laser + Sound
	@Getter
	@Setter
	private static boolean active = false;
	@Getter
	@Setter
	private static Location laserStart;
	private static Location laserSoundLoc;
	@Getter
	@Setter
	private static Player buttonPresser;
	private static int laserTaskId;
	private static int soundTaskId;

	// Game
	@Getter
	@Setter
	private static ReflectionLamp lamp = RandomUtils.randomElement(Arrays.asList(ReflectionLamp.values()));
	@Getter
	@Setter
	private static int reflections;
	@Getter
	@Setter
	private static String message;


	public Reflection() {
		Pugmas24.get().worldguard().getRegion(gameRg);
		Pugmas24.get().worldguard().getRegion(bannerRg);
		if (center.getBlock().getType() != Material.NETHERITE_BLOCK)
			throw new InvalidInputException("[Reflection] Center location not set to correct location");
		//

		new ReflectionListeners();

		// New Objective
		ReflectionUtils.newObjective();
	}

	// TODO: 2024
	private static Color getMinigolfUserColor(Player player) {
//		MiniGolf21UserService service = new MiniGolf21UserService();
//		MiniGolf21User user = service.get(player);
//		Color color = user.getColor();
//		if (color.equals(Color.WHITE))
//			return Color.RED;

//		return color;

		return Color.RED;
	}

	protected static void startLaser(Player player, BlockFace startFace) {
		active = true;
		ReflectionUtils.clearLamps();
		AtomicInteger cooldown = new AtomicInteger(5);
		AtomicInteger lifespan = new AtomicInteger(750);
		final BlockFace[] blockFace = {startFace};
		final Location[] loc = {laserStart.clone()};

		AtomicReference<Color> laserColor = new AtomicReference<>(getMinigolfUserColor(player));
		AtomicInteger reflections = new AtomicInteger(0);
		new SoundBuilder(Sound.BLOCK_BEACON_ACTIVATE).location(laserStart).volume(2.0).play();
		laserSound();

		laserTaskId = Tasks.repeat(0, 1, () -> {
			if (active) {
				laserSoundLoc = loc[0].clone();
				DotEffect.builder().player(player).location(loc[0].clone()).speed(0.1).ticks(10).color(laserColor.get()).start();
				DotEffect.builder().player(player).location(loc[0].clone().add(0, -6, 0)).speed(0.1).ticks(10).color(laserColor.get()).start();
				Block block = loc[0].getBlock();
				Material blockType = block.getType();

				double middle = loc[0].getX() - loc[0].getBlockX();
				if (middle == 0.5 && !blockType.equals(Material.AIR) && cooldown.get() == 0) {
					boolean broadcast = true;
					if (blockType.equals(Material.REDSTONE_LAMP)) {
						if (ReflectionUtils.checkObjective(reflections.get(), block.getRelative(0, 1, 0).getType())) {
							BlockData blockData = block.getBlockData();
							Lightable lightable = (Lightable) blockData;
							lightable.setLit(true);
							block.setBlockData(lightable);

							Block block1 = block.getRelative(0, -6, 0);
							BlockData blockData1 = block1.getBlockData();
							Lightable lightable1 = (Lightable) blockData1;
							lightable1.setLit(true);
							block1.setBlockData(lightable1);
							ReflectionUtils.win(reflections.get());
							broadcast = false;
						}
					}
					if (broadcast)
						ReflectionUtils.broadcastObjective();
					endLaser();
					return;
				}

				Block below = block.getRelative(0, -1, 0);
				Material bannerType = below.getType();
				if (middle == 0.5 && MaterialTag.STANDING_BANNERS.isTagged(bannerType) && cooldown.get() == 0) {
					loc[0] = LocationUtils.getCenteredLocation(loc[0]);
					loc[0].setY(loc[0].getY() + 0.25);
					Rotatable rotatable = (Rotatable) below.getBlockData();
					BlockFace newFace = ReflectionUtils.getReflection(blockFace[0], rotatable.getRotation());
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
		soundTaskId = Tasks.repeat(0, TickTime.SECOND.x(5), () -> {
			Collection<Player> players = Pugmas24.get().getPlayersIn(gameRg);
			for (Player player : players)
				new SoundBuilder(Sound.BLOCK_BEACON_AMBIENT).receiver(player).location(laserSoundLoc).play();
		});
	}

	private static void endLaser() {
		Tasks.cancel(laserTaskId);
		Tasks.cancel(soundTaskId);

		Collection<Player> players = Pugmas24.get().getPlayersIn(gameRg);
		for (Player player : players)
			player.stopSound(Sound.BLOCK_BEACON_AMBIENT);

		new SoundBuilder(Sound.BLOCK_BEACON_DEACTIVATE).location(center).play();
		Tasks.wait(TickTime.SECOND.x(2), () -> active = false);
	}


}
