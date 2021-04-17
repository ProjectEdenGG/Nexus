package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.mechanics.common.SpleefMechanic;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import static me.pugabyte.nexus.utils.LocationUtils.getBlockHit;

public final class Splegg extends SpleefMechanic {

	@Override
	public @NotNull String getName() {
		return "Splegg";
	}

	@Override
	public String getDescription() {
		return "Shoot blocks with eggs to break them and spleef players off the map";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.EGG);
	}

	@Override
	public void playBlockBreakSound(Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0F, 0.7F);
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (!minigamer.getMatch().isBegun())
			return;

		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().name().contains("RIGHT_CLICK")) return;

		Material hand = minigamer.getPlayer().getInventory().getItemInMainHand().getType();
		if (MaterialTag.SHOVELS.isTagged(hand))
			throwEgg(minigamer);
	}

	private void throwEgg(Minigamer minigamer) {
		Location location = minigamer.getPlayer().getLocation().add(0, 1.5, 0);
		location.add(minigamer.getPlayer().getLocation().getDirection());
		Egg egg = (Egg) minigamer.getPlayer().getWorld().spawnEntity(location, EntityType.EGG);
		egg.setVelocity(location.getDirection().multiply(1.75));
		egg.setShooter(minigamer.getPlayer());
		minigamer.getPlayer().playSound(minigamer.getPlayer().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5F, 2F);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Egg)) return;

		ProjectileSource source = projectile.getShooter();
		if (!(source instanceof Player)) return;

		Minigamer minigamer = PlayerManager.get((Player) source);
		if (!minigamer.isPlaying(this)) return;

		projectile.remove();
		Block blockHit = getBlockHit(event);
		if (blockHit == null) return;

		breakBlock(minigamer.getMatch(), blockHit.getLocation());
	}

}
