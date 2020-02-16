package me.pugabyte.bncore.features.listeners;

import lombok.Data;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Fetch implements Listener {

	public static boolean enabled = false;
	public static List<UUID> fetchers = new ArrayList<>();
	public static List<Arrow> arrows = new ArrayList<>();

	public Fetch() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onBoneToss(PlayerInteractEvent event) {
		if (!enabled) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getPlayer().getInventory().getItemInMainHand() == null) return;
		if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.BONE) return;
		if (!fetchers.contains(event.getPlayer().getUniqueId())) return;
		event.setCancelled(true);
		Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
		arrow.setVelocity(arrow.getVelocity().multiply(.75));
		arrows.add(arrow);
		event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.BONE,
				event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1));
	}

	@EventHandler
	public void onArrowHit(ProjectileHitEvent event) {
		if (!enabled) return;
		if (!(event.getEntity() instanceof Arrow)) return;
		Arrow arrow = (Arrow) event.getEntity();
		if (!arrows.contains(arrow)) return;
		arrow.getLocation().getWorld().dropItem(arrow.getLocation(), new ItemStack(Material.BONE));
		arrows.remove(arrow);
		arrow.remove();
	}

}
