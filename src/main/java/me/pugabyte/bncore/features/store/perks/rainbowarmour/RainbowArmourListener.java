package me.pugabyte.bncore.features.store.perks.rainbowarmour;

import me.pugabyte.bncore.BNCore;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RainbowArmourListener implements Listener {

	RainbowArmourListener() {
		BNCore.registerListener(this);
	}

	private boolean isLeatherArmour(Material material) {
		return material == Material.LEATHER_HELMET ||
				material == Material.LEATHER_CHESTPLATE ||
				material == Material.LEATHER_LEGGINGS ||
				material == Material.LEATHER_BOOTS;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode() != GameMode.SURVIVAL) return;
		if (RainbowArmour.getEnabledPlayers().containsKey(player) && RainbowArmour.getEnabledPlayers().get(player).isEnabled()) {
			ItemStack item = event.getCurrentItem();
			if (event.getSlotType() == InventoryType.SlotType.ARMOR && isLeatherArmour(item.getType())) {
				RainbowArmourCommand.removeColor(item);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (RainbowArmour.getEnabledPlayers().containsKey(player) && RainbowArmour.getEnabledPlayers().get(player).isEnabled()) {
			for (ItemStack itemStack : event.getDrops()) {
				if (isLeatherArmour(itemStack.getType())) {
					LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
					meta.setColor(null);
					itemStack.setItemMeta(meta);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (RainbowArmour.getEnabledPlayers().containsKey(player) && RainbowArmour.getEnabledPlayers().get(player).isEnabled())
			RainbowArmourCommand.removeColor(player.getInventory());
	}
}