package gg.projecteden.nexus.features.leashable;

import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.EntityUnleashEvent.UnleashReason;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Leashable extends Feature implements Listener {
	List<EntityType> ignoreType = List.of(EntityType.PLAYER, EntityType.NPC, EntityType.BAT, EntityType.WITHER,
		EntityType.ENDER_DRAGON, EntityType.WARDEN, EntityType.ELDER_GUARDIAN);

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
		boolean hasLeash = !isNullOrAir(tool) && tool.getType() == Material.LEAD;

		if (hasLeash && ignoreType.contains(clickedEntity.getType())) {
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

			vehicle.removePassenger(passenger);
			livingEntity = (LivingEntity) passenger;
		}

		if (livingEntity != null)
			leashEntity(event, player, tool, livingEntity, hasLeash);
	}


}