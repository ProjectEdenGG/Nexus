package gg.projecteden.nexus.features.minigolf.listeners;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import gg.projecteden.nexus.features.minigolf.models.MiniGolfUser;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlock;
import gg.projecteden.nexus.features.minigolf.models.blocks.ModifierBlockType;
import gg.projecteden.nexus.features.minigolf.models.events.MiniGolfBallModifierBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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

		Location location = oldBall.getLocation();
		Vector velocity = oldBall.getVelocity();
		World world = oldBall.getWorld();
		Material material = location.getBlock().getType();

		// spawn a new ball
		Snowball newBall = (Snowball) world.spawnEntity(location, EntityType.SNOWBALL, CreatureSpawnEvent.SpawnReason.CUSTOM, _entity -> ((Snowball) _entity).setItem(golfBall.getDisplayItem()));

		golfBall.setSnowball(newBall);
		golfBall.setShooter(golfBall.getShooter());
		golfBall.setGravity(entity.hasGravity());
		golfBall.setName(MiniGolfUtils.getStrokeString(user));
		golfBall.setTicksLived(entity.getTicksLived());
		golfBall.setVelocity(velocity);
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
				velocity.multiply(-1).multiply(0.25);
			}
		}

		// Bounce off surfaces
		Block hitBlock = event.getHitBlock();
		if (isNullOrAir(hitBlock)) {
			user.debug("golfball hit null or air block");
			return;
		}

		golfBall.setVelocity(velocity);

		Material hitMaterial = hitBlock.getType();
		BlockFace blockFace = event.getHitBlockFace();
		for (ModifierBlockType modifierBlockType : ModifierBlockType.values()) {
			ModifierBlock modifierBlock = modifierBlockType.getModifierBlock();
			if (modifierBlockType.equals(ModifierBlockType.DEFAULT) || modifierBlock.getMaterials().contains(hitMaterial)) {
				MiniGolfBallModifierBlockEvent modifierBlockEvent = new MiniGolfBallModifierBlockEvent(golfBall, modifierBlockType);
				if (modifierBlockEvent.callEvent()) {
					modifierBlock.handleBounce(golfBall, hitBlock, blockFace);
					break;
				}
			}
		}
	}
}
