package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Map.Entry;
import java.util.UUID;

public class ProjectileListener implements Listener {

	public ProjectileListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Snowball) {
			// Check if golf ball
			PersistentDataContainer c = entity.getPersistentDataContainer();
			if (!c.has(MiniGolf.getParKey(), PersistentDataType.INTEGER)) {
				return;
			}

			// Get info
			Location loc = entity.getLocation();
			Vector vel = entity.getVelocity();
			World world = entity.getWorld();

			// Spawn new golf ball
			Snowball ball = (Snowball) world.spawnEntity(loc, EntityType.SNOWBALL);
			ball.setGravity(entity.hasGravity());
			MiniGolf.getGolfBalls().add(ball);

			// Update last player ball
			for (Entry<UUID, Snowball> entry : MiniGolf.getLastPlayerBall().entrySet()) {
				// If same ball
				if (entry.getValue().equals(entity)) {
					// Update to new ball
					entry.setValue(ball);
					break;
				}
			}

			// Par
			int par = c.get(MiniGolf.getParKey(), PersistentDataType.INTEGER);
			PersistentDataContainer b = ball.getPersistentDataContainer();
			b.set(MiniGolf.getParKey(), PersistentDataType.INTEGER, par);
			ball.setCustomName("Par " + par);
			ball.setCustomNameVisible(true);

			// Last pos
			double x = c.get(MiniGolf.getXKey(), PersistentDataType.DOUBLE);
			double y = c.get(MiniGolf.getYKey(), PersistentDataType.DOUBLE);
			double z = c.get(MiniGolf.getZKey(), PersistentDataType.DOUBLE);
			b.set(MiniGolf.getXKey(), PersistentDataType.DOUBLE, x);
			b.set(MiniGolf.getYKey(), PersistentDataType.DOUBLE, y);
			b.set(MiniGolf.getZKey(), PersistentDataType.DOUBLE, z);

			// Golf ball hit entity
			if (event.getHitBlockFace() == null) {
				event.setCancelled(true);
				return;
			}

			// Bounce off surfaces
			if (!BlockUtils.isNullOrAir(event.getHitBlock())) {
				Material mat = event.getHitBlock().getType();
				switch (event.getHitBlockFace()) {
					case NORTH:
					case SOUTH:
						if (mat == Material.SOUL_SOIL)
							vel.setZ(0);
						else if (mat == Material.SLIME_BLOCK)
							vel.setZ(Math.copySign(0.25, -vel.getZ()));
						else
							vel.setZ(-vel.getZ());
						break;

					case EAST:
					case WEST:
						if (mat == Material.SOUL_SOIL)
							vel.setX(0);
						else if (mat == Material.SLIME_BLOCK)
							vel.setX(Math.copySign(0.25, -vel.getX()));
						else
							vel.setX(-vel.getX());
						break;

					case UP:
					case DOWN:
						Material _mat = loc.getBlock().getType();
						if (mat == Material.CRIMSON_HYPHAE || _mat == Material.WATER || _mat == Material.LAVA) {
							// Ball hit out of bounds
							MiniGolf.respawnBall(ball);
							return;
						}

						if (vel.getY() >= 0 && vel.length() <= 0.01 && !MiniGolf.getInBounds().contains(mat)) {
							// Ball stopped in out of bounds
							MiniGolf.respawnBall(ball);
							return;
						}

						vel.setY(-vel.getY());
						vel.multiply(0.7);

						if (vel.getY() < 0.1) {
							vel.setY(0);
							loc.setY(Math.floor(loc.getY() * 2) / 2 + MiniGolf.getFloorOffset());
							ball.teleport(loc);
							ball.setGravity(false);
						}
						break;

					default:
						break;
				}
			}

			// Friction
			ball.setVelocity(vel);
		}
	}
}
