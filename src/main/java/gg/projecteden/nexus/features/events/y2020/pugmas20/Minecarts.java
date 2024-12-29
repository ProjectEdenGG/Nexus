package gg.projecteden.nexus.features.events.y2020.pugmas20;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Minecarts {
	private static final Location spawnLoc_empty = Pugmas20.location(858, 53, 531, 0, 180);
	private static final Location spawnLoc_full_1 = Pugmas20.location(745, 52, 500, 0, 0);
	private static final Location spawnLoc_full_2 = Pugmas20.location(738, 42, 539, 0, 0);
	private static final List<Location> spawnLoc_full = Arrays.asList(spawnLoc_full_1, spawnLoc_full_2);
	private static final List<Entity> minecarts = new ArrayList<>();

	public Minecarts() {
		Tasks.repeat(TickTime.SECOND.x(5), TickTime.SECOND.x(30), () -> {
			// Empty
			Location loc = spawnLoc_empty;
			Entity minecart = loc.getWorld().spawnEntity(loc, EntityType.MINECART);

			Entity finalMinecart = minecart;
			Location finalLoc = loc;
			Tasks.wait(5, () -> {
				finalMinecart.setVelocity((finalMinecart.getVelocity().add(finalLoc.getDirection().multiply(1))).setY(0));
				minecarts.add(finalMinecart);
			});

			// Full
			loc = RandomUtils.randomElement(spawnLoc_full);
			minecart = loc.getWorld().spawnEntity(loc, EntityType.MINECART_CHEST);

			Entity finalMinecart1 = minecart;
			Location finalLoc1 = loc;
			Tasks.wait(5, () -> {
				finalMinecart1.setVelocity((finalMinecart1.getVelocity().add(finalLoc1.getDirection().multiply(1))).setY(0));
				minecarts.add(finalMinecart1);
			});

		});

		Tasks.repeat(TickTime.SECOND.x(5), TickTime.TICK.x(10), () -> {
			for (Entity minecart : new ArrayList<>(minecarts)) {
				if (minecart == null || minecart.isDead()) {
					minecarts.remove(minecart);
					continue;
				}

				if (isOnStraightRail(minecart)) {
					Vector direction = minecart.getLocation().getDirection();
					minecart.setVelocity((minecart.getVelocity().add(direction.multiply(1))).setY(0));
				}
			}
		});
	}

	private boolean isOnStraightRail(Entity minecart) {
		Block block = getRail(minecart.getLocation());
		if (block == null)
			return false;

		Rail rail = (Rail) block.getBlockData();
		return EnumUtils.valuesExcept(Shape.class, Shape.NORTH_EAST, Shape.NORTH_WEST, Shape.SOUTH_EAST, Shape.SOUTH_WEST).contains(rail.getShape());
	}

	private Block getRail(Location loc) {
		Block block = loc.getBlock();
		if (block.getType().equals(Material.RAIL))
			return block;
		else if (block.getRelative(BlockFace.DOWN).getType().equals(Material.RAIL))
			return block.getRelative(BlockFace.DOWN);

		return null;
	}

//	public void doThing() {
//		Location spawnLoc = location(858, 53, 531, 0, 180);
//		Entity minecart = spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.MINECART);
//		Tasks.wait(5, () -> {
//			minecart.setVelocity((minecart.getVelocity().add(spawnLoc.getDirection().multiply(1))).setY(0));
//
//			for (int i = 1; i < 30; i++) {
//				Tasks.wait(i * 10, () -> {
//					Vector direction = minecart.getLocation().getDirection();
//					minecart.setVelocity((minecart.getVelocity().add(direction.multiply(1))).setY(0));
//				});
//			}
//		});
//	}
}
