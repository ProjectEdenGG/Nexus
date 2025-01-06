package gg.projecteden.nexus.features.events.y2020.bearfair20.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Laser {
	private Player player;
	private Location startLoc;
	private BlockFace startFace;
	private Color laserColor;
	private int taskId;
	private Material target;
	private boolean active = false;

	public Laser(Player player, Location startLoc, BlockFace startFace, Color laserColor, Material target) {
		this.player = player;
		this.startLoc = startLoc;
		this.startFace = startFace;
		this.laserColor = laserColor;
		this.target = target;
		start();
	}

	public void start() {
		AtomicReference<Location> curLoc = new AtomicReference<>(startLoc.clone());
		AtomicReference<BlockFace> curFace = new AtomicReference<>(startFace);
		AtomicInteger cooldown = new AtomicInteger(5);
		AtomicInteger lifeSpan = new AtomicInteger(750);
		active = true;

		taskId = Tasks.Countdown.builder()
				.duration(lifeSpan.get())
				.onStart(() -> {
					startLoc.getWorld().playSound(startLoc, Sound.BLOCK_BEACON_ACTIVATE, 2F, 1F);
					curLoc.set(startLoc.clone());
					curFace.set(startFace);
				})
				.onTick(i -> {
					if (!active) stopLaser(curLoc.get());
					if (i % TickTime.SECOND.x(5) == 0)
						curLoc.get().getWorld().playSound(curLoc.get(), Sound.BLOCK_BEACON_AMBIENT, 1F, 1F);
					DotEffect.builder().player(player).location(curLoc.get()).speed(0.1).ticks(10).color(laserColor).start();

					Block block = curLoc.get().getBlock();
					Material blockType = block.getType();

					// When the laser is in the middle of the block, with no cooldown
					double middle = curLoc.get().getX() - curLoc.get().getBlockX();
					if (middle == 0.5 && cooldown.get() == 0) {

						// If the laser hits a block
						if (!blockType.equals(Material.AIR)) {

							// If the laser hits its target
							if (blockType.equals(target)) {
								FireworkLauncher.random(block.getLocation()).detonateAfter(10L).launch();
							}

							stopLaser(curLoc.get());
							return;

							// If the laser has not hit a block
						} else {

							// If the block under the laser is a banner
							Block below = block.getRelative(0, -1, 0);
							Material bannerType = below.getType();
							if (MaterialTag.STANDING_BANNERS.isTagged(bannerType)) {
								curLoc.set(LocationUtils.getCenteredLocation(curLoc.get()));
								curLoc.get().setY(curLoc.get().getY() + 0.25);
								Rotatable rotatable = (Rotatable) below.getBlockData();
								BlockFace newFace = getReflection(curFace.get(), rotatable.getRotation());

								if (newFace == null) {
									stopLaser(curLoc.get());
									return;
								}

								curFace.set(newFace);
								cooldown.set(5);
							}
						}
					}

					curLoc.set(curLoc.get().add(curFace.get().getDirection().multiply(0.5)));
					lifeSpan.decrementAndGet();

					if (cooldown.get() > 0)
						cooldown.decrementAndGet();
				})
				.onComplete(() -> curLoc.get().getWorld().playSound(curLoc.get(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F))
				.start()
				.getTaskId();
	}

	public void stopLaser(Location location) {
		location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
		active = false;
		Tasks.cancel(taskId);
	}

	public static BlockFace getReflection(BlockFace from, BlockFace bannerFace) {
		if (bannerFace.name().toLowerCase().contains(from.name().toLowerCase())) {
			return null;
		}

		if (from.getOppositeFace().equals(bannerFace))
			return from.getOppositeFace();

		if (from.equals(BlockFace.NORTH)) {
			if (bannerFace.equals(BlockFace.WEST) || bannerFace.equals(BlockFace.EAST))
				return from;

			if (bannerFace.equals(BlockFace.SOUTH_WEST))
				return BlockFace.WEST;
			else
				return BlockFace.EAST;

		} else if (from.equals(BlockFace.SOUTH)) {
			if (bannerFace.equals(BlockFace.WEST) || bannerFace.equals(BlockFace.EAST))
				return from;

			if (bannerFace.equals(BlockFace.NORTH_WEST))
				return BlockFace.WEST;
			else
				return BlockFace.EAST;

		} else if (from.equals(BlockFace.EAST)) {
			if (bannerFace.equals(BlockFace.SOUTH) || bannerFace.equals(BlockFace.NORTH))
				return from;

			if (bannerFace.equals(BlockFace.SOUTH_WEST))
				return BlockFace.SOUTH;
			else
				return BlockFace.NORTH;

		} else if (from.equals(BlockFace.WEST)) {
			if (bannerFace.equals(BlockFace.SOUTH) || bannerFace.equals(BlockFace.NORTH))
				return from;

			if (bannerFace.equals(BlockFace.NORTH_EAST))
				return BlockFace.NORTH;
			else
				return BlockFace.SOUTH;
		}

		return from;
	}
}
