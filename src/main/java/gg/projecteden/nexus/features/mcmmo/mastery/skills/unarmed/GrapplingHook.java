package gg.projecteden.nexus.features.mcmmo.mastery.skills.unarmed;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class GrapplingHook implements Listener {

	public static final ItemStack ITEM = new ItemBuilder(Material.FISHING_ROD)
		.model(ItemModelType.GRAPPLING_HOOK)
		.name("&eGrappling Hook")
		.unbreakable()
		.build();

	static {
		Nexus.registerListener(new GrapplingHook());
	}

	private static final double PULL_SPEED = 2.0;
	private static final NamespacedKey GRAPPLE_KEY = new NamespacedKey(Nexus.getInstance(), "grappling_hook");
	private final Map<Player, BukkitTask> fallProtection = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFish(PlayerFishEvent event) {
		FishHook hook = event.getHook();
		Player player = event.getPlayer();

		if (event.getState() == State.FISHING) {
			if (isGrapplingRod(player, event.getHand()))
				hook.getPersistentDataContainer().set(GRAPPLE_KEY, PersistentDataType.BOOLEAN, true);
			return;
		}

		if (!isGrapplingBobber(hook))
			return;

		switch (event.getState()) {
			case BITE -> event.setCancelled(true);
			case REEL_IN, IN_GROUND, CAUGHT_ENTITY, CAUGHT_FISH -> {
				if (event.getState() == State.CAUGHT_FISH || event.getState() == State.CAUGHT_ENTITY)
					event.setCancelled(true);
				if (isGrapplingRod(player, event.getHand()))
					pull(player, hook);

				if (event.isCancelled())
					hook.remove();
			}
			default -> {}
		}
	}

	@EventHandler
	public void onHookHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof FishHook fishHook)) return;
		if (!isGrapplingBobber(fishHook)) return;
		if (event.getHitEntity() == null) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFallDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player player && clearFallProtection(player))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		clearFallProtection(event.getPlayer());
	}

	private boolean isGrapplingRod(Player player, EquipmentSlot hand) {
		if (hand != EquipmentSlot.HAND && hand != EquipmentSlot.OFF_HAND)
			return false;

		ItemStack item = player.getInventory().getItem(hand);
		return item.getType() == Material.FISHING_ROD && ItemUtils.isModelMatch(item, ITEM);
	}

	private boolean isGrapplingBobber(FishHook hook) {
		return hook.getPersistentDataContainer().has(GRAPPLE_KEY, PersistentDataType.BOOLEAN);
	}

	private void pull(Player player, FishHook hook) {
		Vector direction = hook.getLocation().toVector().subtract(player.getLocation().toVector());
		if (direction.lengthSquared() <= 0.01)
			return;

		player.setVelocity(direction.normalize().multiply(PULL_SPEED));
		clearFallProtection(player);

		// Give the launch time to move the player before checking for a landing.
		fallProtection.put(player, new BukkitRunnable() {
			@Override
			public void run() {
				if (!player.isOnline() || player.isDead() || player.isOnGround() || player.isInWater())
					clearFallProtection(player);
			}
		}.runTaskTimer(Nexus.getInstance(), 2, 1));
	}

	private boolean clearFallProtection(Player player) {
		BukkitTask task = fallProtection.remove(player);
		if (task == null)
			return false;

		task.cancel();
		return true;
	}

}
