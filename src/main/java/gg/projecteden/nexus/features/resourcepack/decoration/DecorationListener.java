package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.MultiBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;

public class DecorationListener implements Listener {

	public DecorationListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player)) return;
		if (!(event.getVehicle() instanceof ArmorStand armorStand)) return;
		if (Seat.isSeat(armorStand)) {
			event.getVehicle().remove();
		}
	}

	@EventHandler
	public void on(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (!(event.getDismounted() instanceof ArmorStand armorStand)) return;
		if (Seat.isSeat(armorStand)) {
			event.getDismounted().remove();
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		EquipmentSlot slot = event.getHand();
		if (slot == null || !slot.equals(EquipmentSlot.HAND)) return;

		// TODO: Remove
		Player player = event.getPlayer();
		if (!Dev.WAKKA.is(player)) return;
		//

		player.sendMessage("");
		Block clicked = event.getClickedBlock();
		if (clicked == null) {
			return;
		}

		if (!new CooldownService().check(player, "decoration-interact", TickTime.SECOND.x(1)))
			return;

		ItemStack tool = ItemUtils.getTool(player);

		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) rightClick(event, player, clicked, tool);
		else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) leftClick(event, player, clicked, tool);

	}

	private void leftClick(PlayerInteractEvent event, Player player, Block clicked, ItemStack tool) {
		player.sendMessage("left clicking");
		if (!ItemUtils.isNullOrAir(tool))
			return;

		ItemFrame itemFrame = getItemFrame(player, clicked);
		if (itemFrame == null) return;

		Decoration decoration = getDecoration(player, itemFrame);
		if (decoration == null) return;

		if (decoration instanceof MultiBlock) {
			player.sendMessage("this decoration is currently unsupported");
			return;
		}

		event.setCancelled(true);

		decoration.destroy(player, itemFrame);
	}

	private void rightClick(PlayerInteractEvent event, Player player, Block clicked, ItemStack tool) {
		player.sendMessage("right clicking");
		if (ItemUtils.isNullOrAir(tool)) {

			ItemFrame itemFrame = getItemFrame(player, clicked);
			if (itemFrame == null) return;

			Decoration decoration = getDecoration(player, itemFrame);
			if (decoration == null) return;

			// Interact
			event.setCancelled(true);
			decoration.interact(player, itemFrame);
		} else {
			// Place
			Decorations _decorations = Decorations.of(tool);
			if (_decorations == null) return;

			event.setCancelled(true);
			_decorations.getDecoration().place(player, clicked, event.getBlockFace());
		}
	}

	@Nullable
	private ItemFrame getItemFrame(Player player, Block clicked) {
		Entity entity = EntityUtils.getNearbyEntities(clicked.getLocation().toCenterLocation(), 1.0).keySet().stream().findFirst().orElse(null);
		if (!(entity instanceof ItemFrame itemFrame)) {
			player.sendMessage("no item frame found");
			return null;
		}
		return itemFrame;
	}

	@Nullable
	private Decoration getDecoration(Player player, ItemFrame itemFrame) {
		ItemStack item = itemFrame.getItem();
		if (ItemUtils.isNullOrAir(item))
			return null;

		Decorations decorations = Decorations.of(item);
		if (decorations == null) {
			player.sendMessage("decoration is null");
			return null;
		}
		return decorations.getDecoration();
	}


}
