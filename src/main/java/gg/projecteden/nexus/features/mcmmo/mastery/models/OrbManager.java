package gg.projecteden.nexus.features.mcmmo.mastery.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.cooldown.Cooldown;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public abstract class OrbManager<T extends Entity> implements Listener {

	public static final CooldownService COOLDOWN_SERVICE = new CooldownService();

	public OrbManager() {
		Nexus.registerListener(this);
	}

	public abstract ItemStack getItem();

	public abstract ColorType getColor();

	public abstract double getRadius();

	public abstract long getDurationTicks();

	public abstract String getCooldownType();

	public abstract long getCooldownTicks();

	public abstract void handle(T entity);

	public abstract Class<T> getEntityClass();

	public T getEntity(Entity entity) {
		if (!getEntityClass().isInstance(entity))
			return null;

		return getEntityClass().cast(entity);
	}

	public Orb getNewOrbInstance(Location location, Player owner) {
		return new Orb(location, owner, this);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClick(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (!event.getAction().isRightClick()) return;
		if (!ItemUtils.isModelMatch(event.getItem(), getItem())) return;

		event.setCancelled(true);
		Location placeLoc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation().toCenterLocation();
		for (int i = 0; i < 5; i++) {
			Material mat = placeLoc.clone().add(0, i, 0).getBlock().getType();
			if (mat != Material.AIR && mat != Material.WATER) {
				new JsonBuilder("&cThere is not enough space to place an orb here").send(event.getPlayer());
				return;
			}
		}

		Cooldown cooldown = COOLDOWN_SERVICE.get(event.getPlayer());
		if (cooldown.isOnCooldown(getCooldownType()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			new JsonBuilder("&cThis orb is still on cooldown (%s)".formatted(COOLDOWN_SERVICE.getDiff(event.getPlayer().getUniqueId(), getCooldownType()))).send(event.getPlayer());
			return;
		}

		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.getItem().subtract();

		getNewOrbInstance(placeLoc, event.getPlayer()).spawn();
		event.getPlayer().swingHand(event.getHand());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();

		RayTraceResult result = player.rayTraceBlocks(5, FluidCollisionMode.ALWAYS);
		if (result == null || result.getHitBlock() == null) return;

		Block block = result.getHitBlock();
		if (block.getType() != Material.WATER)
			return;

		if (!ItemUtils.isModelMatch(event.getItem(), getItem())) return;

		event.setCancelled(true);
		Cooldown cooldown = COOLDOWN_SERVICE.get(event.getPlayer());
		if (cooldown.isOnCooldown(getCooldownType()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			new JsonBuilder("&cThis orb is still on cooldown (%s)".formatted(COOLDOWN_SERVICE.getDiff(event.getPlayer().getUniqueId(), getCooldownType()))).send(event.getPlayer());
			return;
		}

		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.getItem().subtract();

		getNewOrbInstance(block.getLocation().clone().toCenterLocation().add(0, 1, 0), event.getPlayer()).spawn();
		event.getPlayer().swingHand(event.getHand());
	}

}
