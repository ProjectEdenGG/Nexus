package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
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

public class ProjectileListener implements Listener {

	public ProjectileListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();
		if (!BearFair21.isAtBearFair(entity.getLocation()))
			return;

		// Check if golf ball
		if (entity instanceof Snowball) {
			// Get info
			Location loc = entity.getLocation();
			Vector vel = entity.getVelocity();
			World world = entity.getWorld();

			// Spawn new golf ball
			Snowball ball = (Snowball) world.spawnEntity(loc, EntityType.SNOWBALL);
			ball.setGravity(entity.hasGravity());

			// Update last player ball
			MiniGolfUser user = null;
			for (MiniGolfUser _user : MiniGolf.getUsers()) {
				if (_user.getSnowball() == null)
					continue;

				if (_user.getSnowball().equals(entity)) {
					_user.setSnowball(ball);
					user = _user;
					break;
				}
			}

			if (user == null)
				return;

			// Stroke
			ball.setCustomName(user.getColor().getChatColor() + "Stroke " + user.getCurrentStrokes());
			ball.setCustomNameVisible(true);

			PersistentDataContainer old = entity.getPersistentDataContainer();
			PersistentDataContainer current = ball.getPersistentDataContainer();
			// Last pos
			double x = old.get(MiniGolf.getXKey(), PersistentDataType.DOUBLE);
			double y = old.get(MiniGolf.getYKey(), PersistentDataType.DOUBLE);
			double z = old.get(MiniGolf.getZKey(), PersistentDataType.DOUBLE);
			current.set(MiniGolf.getXKey(), PersistentDataType.DOUBLE, x);
			current.set(MiniGolf.getYKey(), PersistentDataType.DOUBLE, y);
			current.set(MiniGolf.getZKey(), PersistentDataType.DOUBLE, z);

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
						if (mat == Material.SOUL_SOIL) {
							vel.setY(0);
						} else {
							Material _mat = loc.getBlock().getType();
							if (mat == Material.CRIMSON_HYPHAE || mat == Material.PURPLE_STAINED_GLASS || _mat == Material.WATER || _mat == Material.LAVA) {
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
						}

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
