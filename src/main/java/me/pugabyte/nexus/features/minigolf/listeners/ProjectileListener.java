package me.pugabyte.nexus.features.minigolf.listeners;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import me.pugabyte.nexus.features.minigolf.models.MiniGolfUser;
import me.pugabyte.nexus.features.minigolf.models.blocks.ModifierBlock;
import me.pugabyte.nexus.features.minigolf.models.blocks.ModifierBlockType;
import me.pugabyte.nexus.features.minigolf.models.events.MiniGolfBallModifierBlockEvent;
import me.pugabyte.nexus.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class ProjectileListener implements Listener {

	public ProjectileListener() {
		Nexus.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Snowball oldBall))
			return;

		ProjectileSource source = oldBall.getShooter();
		if (!(source instanceof Player player))
			return;

		MiniGolfUser user = MiniGolfUtils.getUser(player.getUniqueId());
		if (user == null)
			return;

		GolfBall golfBall = user.getGolfBall();
		if (golfBall == null || !golfBall.isAlive())
			return;

		if (!golfBall.getSnowball().equals(oldBall))
			return;

		Location loc = oldBall.getLocation();
		Vector vel = oldBall.getVelocity();
		World world = oldBall.getWorld();
		Material material = loc.getBlock().getType();

		// spawn a new ball
		Snowball newBall = (Snowball) world.spawnEntity(loc, oldBall.getType());
		newBall.setGravity(entity.hasGravity());
		newBall.setShooter(golfBall.getShooter());

		newBall.setCustomName(MiniGolfUtils.getStrokeString(user));
		newBall.setCustomNameVisible(true);
		newBall.setTicksLived(entity.getTicksLived());

		golfBall.setSnowball(newBall);
		golfBall.applyDisplayItem();
		//

		user.setGolfBall(golfBall);

		// Golf ball hit entity
		if (event.getHitBlockFace() == null) {
			user.debug("ball hit an entity");

			if (ModifierBlockType.DEATH.getModifierBlock().getMaterials().contains(material)) {
				user.debug("  ball is on a death modifier block");
				golfBall.respawn();
				return;
			} else {
				user.debug("  invert velocity");
				// Bounce off of entity
				vel.multiply(-1).multiply(0.25);
			}
		}

		// Bounce off surfaces
		if (BlockUtils.isNullOrAir(event.getHitBlock())) {
			user.debug("golfball hit null or air block");
			return;
		}

		Material hitMaterial = event.getHitBlock().getType();
		BlockFace blockFace = event.getHitBlockFace();
		for (ModifierBlockType modifierBlockType : ModifierBlockType.values()) {
			ModifierBlock modifierBlock = modifierBlockType.getModifierBlock();
			if (modifierBlockType.equals(ModifierBlockType.DEFAULT) || modifierBlock.getMaterials().contains(hitMaterial)) {
				MiniGolfBallModifierBlockEvent modifierBlockEvent = new MiniGolfBallModifierBlockEvent(golfBall, modifierBlockType);
				if (modifierBlockEvent.callEvent()) {
					modifierBlock.handleBounce(golfBall, blockFace);
					break;
				}
			}
		}
	}
}
