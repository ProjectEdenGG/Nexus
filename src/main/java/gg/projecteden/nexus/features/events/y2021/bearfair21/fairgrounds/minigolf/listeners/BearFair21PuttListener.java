package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.listeners;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolf;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolfUtils;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfColor;
import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.BearFair21MiniGolfHole;
import gg.projecteden.nexus.models.bearfair21.MiniGolf21User;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;

public class BearFair21PuttListener implements Listener {

	public BearFair21PuttListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPutt(PlayerInteractEvent event) {
		if (BearFair21.isNotAtBearFair(event))
			return;

		Player player = event.getPlayer();
		MiniGolf21User user = BearFair21MiniGolfUtils.getUser(player.getUniqueId());
		if (!BearFair21MiniGolfUtils.isInMiniGolf(player.getLocation())) {
			user.debug("PuttListener > user is not in minigolf");
			return;
		}
		user.debug("PuttListener > user is in minigolf region");

		if (isInteracting(event)) {
			user.debug("PuttListener > user is interacting, returning");
			return;
		}

		ItemStack item = event.getItem();
		if (Nullables.isNullOrAir(item)) {
			user.debug("PuttListener > item is null or air, returning");
			return;
		}

		// quick fix
		ItemStack clone = item.clone();
		clone.setAmount(1);
		ItemMeta itemMeta = clone.getItemMeta();
		itemMeta.setCustomModelData(null);
		clone.setItemMeta(itemMeta);
		boolean stop = true;
		for (ItemStack _item : BearFair21MiniGolf.getItems()) {
			if (ItemUtils.isFuzzyMatch(clone, _item))
				stop = false;
		}
		if (stop) {
			user.debug("PuttListener > doesn't have a kit item, returning");
			return;
		}
		//

		user.debug("PuttListener > user is using a kit item");
		event.setCancelled(true);

		if (!user.isPlaying()) {
			user.debug("PuttListener > user is not playing, returning");
			return;
		}
		user.debug("PuttListener > user is playing minigolf");

		// Get info
		World world = player.getWorld();
		Action action = event.getAction();
		Block block = event.getClickedBlock();

		// Get type of golf club
		boolean putter = ItemUtils.isFuzzyMatch(item, BearFair21MiniGolf.getPutter());
		boolean wedge = ItemUtils.isFuzzyMatch(item, BearFair21MiniGolf.getWedge());

		if (putter || wedge) {
			user.debug("PuttListener > Using putter or wedge...");
			// Find entities
			List<Entity> entities = player.getNearbyEntities(5.5, 5.5, 5.5);

			Location eye = player.getEyeLocation();
			Vector dir = eye.getDirection();
			Vector loc = eye.toVector();

			if (user.getSnowball() == null) {
				user.debug("PuttListener > user snow ball is null, returning");
				return;
			}

			for (Entity entity : entities) {

				// Are we allowed to hit this ball?
				if (entity instanceof Snowball ball) {

					// Check this again, cuz NPE for some reason
					if (user.getSnowball() == null) {
						user.debug("PuttListener > Ball is null (2)");
						return;
					}

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

							double power = BearFair21MiniGolf.getPowerMap().getOrDefault(player.getUniqueId(), .0f);
							if (power >= 0.90)
								power = 1.0;
							else if (power < 0.16)
								power = 0.16;

							dir.multiply(power);
							if (wedge)
								dir.setY(0.25);

							// Update stroke
							user.incStrokes();
							ball.setCustomName(BearFair21MiniGolfUtils.getStrokeString(user));

							// Update last pos
							if (BearFair21MiniGolfUtils.isInBounds(user, entityLoc))
								user.setBallLocation(entityLoc.add(0, BearFair21MiniGolf.getFloorOffset(), 0));

							ball.setTicksLived(1);
							ball.setVelocity(dir);
							new SoundBuilder(Sound.BLOCK_METAL_HIT).location(entityLoc).volume(0.75).pitch(1.25).play();

							ActionBarUtils.sendActionBar(player, "&6Power: " + getPowerDisplay(power), TickTime.SECOND.x(3));
							user.setSnowball(ball);
						} else if (ball.isValid()) {
							// Give golf ball
							user.debug("picking up golfball, removing");
							user.removeBall();
							BearFair21MiniGolfUtils.giveBall(user);
						}
					}
				}
			}
		} else if (item.getType().equals(Material.SNOWBALL) && Model.hasModel(item)) {
			// Is player placing golf ball?
			if (action == Action.RIGHT_CLICK_BLOCK) {
				user.debug("PuttListener > Placing golf ball...");
				// Has already placed a ball
				if (user.getSnowball() != null) {
					BearFair21MiniGolfUtils.error(user, "You already have a ball placed");
					return;
				}

				// Is placing on start position
				if (Nullables.isNullOrAir(block) || block.getType() != Material.GREEN_WOOL) {
					BearFair21MiniGolfUtils.error(user, "You can only place golf balls on green wool");
					return;
				}

				// Is on a valid hole
				BearFair21MiniGolfHole hole = BearFair21MiniGolfUtils.getHole(block.getLocation());
				if (hole == null) {
					BearFair21MiniGolfUtils.error(user, "That is not a valid hole");
					return;
				}

				user.setCurrentHole(hole);
				user.setCurrentStrokes(0);

				// Get spawn location
				Location loc = block.getLocation().add(0.5, 1 + BearFair21MiniGolf.getFloorOffset(), 0.5);
				user.setBallLocation(loc);

				// Spawn golf ball and set data
				Snowball ball = (Snowball) world.spawnEntity(loc, EntityType.SNOWBALL);
				ball.setItem(BearFair21MiniGolf.getGolfBall().clone().model(user.getMiniGolfColor().getModel()).build());

				ball.setGravity(false);
				ball.setCustomName(BearFair21MiniGolfUtils.getStrokeString(user));
				ball.setCustomNameVisible(true);

				user.setSnowball(ball);
				if (!user.getMiniGolfColor().equals(BearFair21MiniGolfColor.RAINBOW))
					GlowUtils.glow(user.getSnowball())
						.color(user.getGlowColor())
						.receivers(user.getOnlinePlayer())
						.run();

				// Remove golf ball from inventory
				ItemStack itemInHand = event.getItem();
				itemInHand.setAmount(itemInHand.getAmount() - 1);
			}
		} else if (ItemUtils.isFuzzyMatch(item, BearFair21MiniGolf.getWhistle())) {
			// Return ball
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				user.debug("PuttListener > Using whistle...");
				// Get last player ball
				Snowball ball = user.getSnowball();
				if (ball == null || !ball.isValid()) {
					BearFair21MiniGolfUtils.sendActionBar(user, "&cYou don't have an active golf ball");
					return;
				}

				// Move ball to last location
				ball.setVelocity(new Vector(0, 0, 0));
				ball.setGravity(false);
				ball.teleport(user.getBallLocation().add(0, BearFair21MiniGolf.getFloorOffset(), 0));

				// Sound
				new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_CHIME).location(player.getLocation()).volume(0.9).pitch(1.9).play();
			}
		} else if (ItemUtils.isFuzzyMatch(item, BearFair21MiniGolf.getScoreBook())) {
			user.debug("PuttListener > Using score book");
			PlayerUtils.runCommand(player, "minigolf score 1");
		} else {
			user.debug("PuttListener > Unknown kit item, do nothing");
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

	public boolean isInteracting(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking())
			return false;

		Block block = event.getClickedBlock();
		return MaterialTag.INTERACTABLES.isTagged(block != null ? block.getType() : null);
	}

}
