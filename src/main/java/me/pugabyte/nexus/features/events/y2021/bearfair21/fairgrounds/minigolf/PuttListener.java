package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;

public class PuttListener implements Listener {

	public PuttListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPutt(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		Player player = event.getPlayer();
		if (!BearFair21.isAtBearFair(event.getPlayer().getLocation())) {
			return;
		}

		if (isInteracting(event)) {
			return;
		}

		ItemStack item = event.getItem();
		if (ItemUtils.isNullOrAir(item)) {
			return;
		}

		// quick fix
		ItemStack clone = item.clone();
		clone.setAmount(1);
		boolean stop = true;
		for (ItemStack _item : MiniGolf.getKit()) {
			if (ItemUtils.isFuzzyMatch(clone, _item))
				stop = false;
		}
		if (stop) {
			return;
		}
		//

		event.setCancelled(true);

		MiniGolfUser user = MiniGolf.getUser(player.getUniqueId());
		if (user == null) {
			MiniGolf.error(player, "User is null");
			return;
		}

		// Get info
		World world = player.getWorld();
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		ItemMeta meta = item.getItemMeta();

		// Get type of golf club
		boolean putter = MiniGolf.hasKey(meta, MiniGolf.getPutterKey());
		boolean wedge = MiniGolf.hasKey(meta, MiniGolf.getWedgeKey());

		if (putter || wedge) {
			// Find entities
			List<Entity> entities = player.getNearbyEntities(5.5, 5.5, 5.5);

			Location eye = player.getEyeLocation();
			Vector dir = eye.getDirection();
			Vector loc = eye.toVector();

			if (user.getSnowball() == null) {
				MiniGolf.error(player, "Ball is null (1)");
				return;
			}

			for (Entity entity : entities) {

				// Are we allowed to hit this ball?
				if (entity instanceof Snowball) {
					Snowball ball = (Snowball) entity;

					// Check this again, cuz NPE for some reason
					if (user.getSnowball() == null)
						return;

					if (!user.getSnowball().equals(ball))
						continue;

					// Is golf ball in player's view?
					Location entityLoc = ball.getLocation();
					Vector vec = entityLoc.toVector().subtract(loc);

					if (dir.angle(vec) < 0.15f) {

						// Are we hitting or picking up the golf ball?
						if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
							// Hit golf ball
							dir.setY(0).normalize();

							double power = player.getExp();
							if (power >= 0.90)
								power = 1.0;
							else if (power < 0.16)
								power = 0.16;

							dir.multiply(power);
							if (wedge)
								dir.setY(0.25);

							ActionBarUtils.sendActionBar(player, "&6Power: " + getPowerDisplay(power), Time.SECOND.x(3));
							ball.setVelocity(dir);

							// Update stroke
							ball.setCustomName(user.getColor().getChatColor() + "Stroke " + user.incrementStrokes());

							// Update last pos
							PersistentDataContainer c = entity.getPersistentDataContainer();
							c.set(MiniGolf.getXKey(), PersistentDataType.DOUBLE, entityLoc.getX());
							c.set(MiniGolf.getYKey(), PersistentDataType.DOUBLE, entityLoc.getY());
							c.set(MiniGolf.getZKey(), PersistentDataType.DOUBLE, entityLoc.getZ());

							// Add to user
							user.setSnowball((Snowball) entity);
							ball.setTicksLived(1);

							world.playSound(entityLoc, Sound.BLOCK_METAL_HIT, 0.75f, 1.25f);

						} else if (ball.isValid()) {
							// Give golf ball
							ball.remove();
							MiniGolf.giveBall(player);
							user.setSnowball(null);
						}
					}
				}
			}
		} else if (MiniGolf.hasKey(meta, MiniGolf.getBallKey())) {
			// Is player placing golf ball?
			if (action == Action.RIGHT_CLICK_BLOCK) {
				// Has already placed a ball
				if (user.getSnowball() != null) {
					MiniGolf.error(player, "You already have a ball placed");
					return;
				}

				// Is placing on start position
				if (BlockUtils.isNullOrAir(block) || block.getType() != Material.GREEN_WOOL) {
					MiniGolf.error(player, "You can only place golf balls on green wool");
					return;
				}

				// Is on a valid hole
				Integer hole = MiniGolf.getHole(block.getLocation());
				if (hole == null) {
					MiniGolf.error(player, "That is not a valid hole");
					return;
				}

				user.setCurrentHole(hole);
				user.setCurrentStrokes(0);

				// Get spawn location
				Location loc;
				if (MiniGolf.isBottomSlab(block))
					loc = block.getLocation().add(0.5, 0.5 + MiniGolf.getFloorOffset(), 0.5);
				else
					loc = block.getLocation().add(0.5, 1 + MiniGolf.getFloorOffset(), 0.5);

				// Spawn golf ball and set data
				Snowball ball = (Snowball) world.spawnEntity(loc, EntityType.SNOWBALL);

				ball.setGravity(false);

				PersistentDataContainer c = ball.getPersistentDataContainer();
				c.set(MiniGolf.getXKey(), PersistentDataType.DOUBLE, loc.getX());
				c.set(MiniGolf.getYKey(), PersistentDataType.DOUBLE, loc.getY());
				c.set(MiniGolf.getZKey(), PersistentDataType.DOUBLE, loc.getZ());

				ball.setCustomName(user.getColor().getChatColor() + "Stroke " + user.getCurrentStrokes());
				ball.setCustomNameVisible(true);

				user.setSnowball(ball);

				// Remove golf ball from inventory
				ItemStack itemInHand = event.getItem();
				itemInHand.setAmount(itemInHand.getAmount() - 1);

				// Add user
//				MiniGolf.getUsers().add(user);
			}
		} else if (MiniGolf.hasKey(meta, MiniGolf.getWhistleKey())) {
			// Return ball
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				// Get last player ball
				Snowball ball = user.getSnowball();
				if (ball == null || !ball.isValid()) {
					MiniGolf.error(player, "Ball is null (3)");
					return;
				}

				PersistentDataContainer container = ball.getPersistentDataContainer();

				// Read persistent data
				double x = container.get(MiniGolf.getXKey(), PersistentDataType.DOUBLE);
				double y = container.get(MiniGolf.getYKey(), PersistentDataType.DOUBLE);
				double z = container.get(MiniGolf.getZKey(), PersistentDataType.DOUBLE);

				// Move ball to last location
				ball.setVelocity(new Vector(0, 0, 0));
				ball.teleport(new Location(world, x, y, z));
				ball.setGravity(false);

				// Sound
				world.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.9f, 1.9f);
			}
		}

	}

	private String getPowerDisplay(double power) {
		int result = (int) (power * 100);

		String color = "&a";
		if (result >= 70)
			color = "&c";
		else if (result >= 50)
			color = "&e";

		return color + result;
	}

	// TODO: better way?
	public boolean isInteracting(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking())
			return false;

		Block block = event.getClickedBlock();
		return MaterialTag.INTERACTABLES.isTagged(block != null ? block.getType() : null);
	}

}
