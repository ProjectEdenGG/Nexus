package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.mechanics.common.SpleefMechanic;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
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

import static gg.projecteden.nexus.utils.LocationUtils.getBlockHit;

public final class Splegg extends SpleefMechanic {

	@Override
	public @NotNull String getName() {
		return "Splegg";
	}

	@Override
	public @NotNull String getDescription() {
		return "Destroy the blocks underneath other players using your egg gun to make them fall to their death";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.EGG);
	}

	@Override
	public void playBlockBreakSound(Location location) {
		location.getWorld().playSound(location, Sound.ENTITY_CHICKEN_EGG, 1.0F, 0.7F);
	}

	@Override
	public boolean useScoreboardNumbers(Match match) {
		return false;
	}

	@Override
	public void onPlayerInteract(Minigamer minigamer, PlayerInteractEvent event) {
		super.onPlayerInteract(minigamer, event);
		if (!minigamer.getMatch().isBegun())
			return;

		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		if (!new CooldownService().check(minigamer.getUuid(), "splegg_throw_" + minigamer.getNickname(), TickTime.TICK.x(2)))
			return;

		Material hand = minigamer.getOnlinePlayer().getInventory().getItemInMainHand().getType();
		if (MaterialTag.SHOVELS.isTagged(hand))
			throwEgg(minigamer);
	}

	private void throwEgg(Minigamer minigamer) {
		Player player = minigamer.getOnlinePlayer();
		Location playerLocation = player.getLocation();

		Location eggLocation = playerLocation.add(0, 1.5, 0);
		eggLocation.add(playerLocation.getDirection());
		Egg egg = (Egg) player.getWorld().spawnEntity(eggLocation, EntityType.EGG);
		egg.setVelocity(eggLocation.getDirection().multiply(1.75));
		egg.setShooter(player);

		player.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 0.5F, 2F);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!(projectile instanceof Egg)) return;

		ProjectileSource source = projectile.getShooter();
		if (!(source instanceof Player)) return;

		Minigamer minigamer = Minigamer.of((Player) source);
		if (!minigamer.isPlaying(this)) return;

		projectile.remove();
		Block blockHit = getBlockHit(event);
		if (blockHit == null) return;

		breakBlock(minigamer.getMatch(), blockHit.getLocation());
	}

	@EventHandler
	public void onProjectileCollide(ProjectileCollideEvent event) {
		if (!(event.getCollidedWith() instanceof Player player)) return;
		Minigamer minigamer = Minigamer.of(player);
		if (minigamer.isPlaying(this))
			event.setCancelled(true);
	}

}
