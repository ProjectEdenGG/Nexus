package gg.projecteden.nexus.features.leashable;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Leashable extends Feature implements Listener {
	@Getter
	private static final Set<UUID> debuggers = new HashSet<>();

	List<EntityType> buggedTypes = List.of(EntityType.BAT, EntityType.GHAST);
	List<EntityType> bossTypes = List.of(EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.WARDEN, EntityType.ELDER_GUARDIAN);
	List<EntityType> ignoreTypes = new ArrayList<>() {{
		addAll(buggedTypes);
		addAll(bossTypes);
		addAll(List.of(EntityType.PLAYER, EntityType.NPC));
	}};

	@EventHandler
	public void on(EntityUnleashEvent event) {
		if (event.getReason().equals(UnleashReason.PLAYER_UNLEASH)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClickEntity(PlayerInteractEntityEvent event) {
		if (event.isCancelled())
			return;

		if (!event.getHand().equals(EquipmentSlot.HAND))
			return;

		Player player = event.getPlayer();
		Entity clickedEntity = event.getRightClicked();
		ItemStack tool = ItemUtils.getTool(player);
		boolean hasLeash = !Nullables.isNullOrAir(tool) && tool.getType() == Material.LEAD;

		if (hasLeash && ignoreTypes.contains(clickedEntity.getType())) {
			debug(player, "leashEntity -> cancelled");
			event.setCancelled(true);
			return;
		}

		if (clickedEntity instanceof LivingEntity livingEntity)
			leashEntity(event, player, tool, livingEntity, hasLeash);
		else if (clickedEntity instanceof Vehicle vehicle)
			leashEntityInVehicle(event, player, tool, vehicle, hasLeash);
	}

	private void leashEntity(PlayerInteractEntityEvent event, Player player, ItemStack tool, LivingEntity livingEntity, boolean hasLeash) {
		if (livingEntity.isLeashed()) {
			Entity leashHolder = livingEntity.getLeashHolder();
			if (!leashHolder.equals(player))
				return;

			event.setCancelled(true);
			livingEntity.setLeashHolder(null);
			livingEntity.getLocation().getWorld().dropItemNaturally(livingEntity.getLocation(), new ItemStack(Material.LEAD));
			return;
		}

		if (!hasLeash)
			return;

		debug(player, "leashEntity");

		event.setCancelled(true);
		ItemUtils.subtract(player, tool);
		livingEntity.setLeashHolder(player);
	}

	private void leashEntityInVehicle(PlayerInteractEntityEvent event, Player player, ItemStack tool, Vehicle vehicle, boolean hasLeash) {
		LivingEntity livingEntity = null;
		if (!vehicle.getPassengers().isEmpty()) {
			Entity passenger = vehicle.getPassengers().get(0);
			Entity targetEntity = player.getTargetEntity(5);
			if (targetEntity != null && vehicle.getPassengers().size() > 1) {
				for (Entity _passenger : vehicle.getPassengers()) {
					if (!(_passenger instanceof LivingEntity))
						continue;

					if (_passenger.equals(targetEntity)) {
						passenger = _passenger;
						break;
					}
				}
			}

			if (!(passenger instanceof LivingEntity))
				return;

			livingEntity = (LivingEntity) passenger;
		}

		if (livingEntity != null) {
			debug(player, "leashEntityInVehicle");
			if (hasLeash)
				vehicle.removePassenger(livingEntity);

			leashEntity(event, player, tool, livingEntity, hasLeash);

		}
	}

	private void debug(Player player, String message) {
		if (player == null)
			return;

		if (debuggers.contains(player.getUniqueId()))
			PlayerUtils.send(player, message);
	}


}
