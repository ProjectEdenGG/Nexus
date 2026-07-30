package gg.projecteden.nexus.features.mcmmo.resetnew;

import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public abstract class ItemAttributeHandler implements Listener {

	public abstract MaterialTag getApplicableItems();

	public abstract String getPermission();

	public abstract void apply(Player player);

	public abstract void remove(Player player);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemHeld(PlayerItemHeldEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		update(event.getPlayer(), item);
	}

	@EventHandler
	public void onInventorySlotChange(PlayerInventorySlotChangeEvent event) {
		Player player = event.getPlayer();

		if (event.getSlot() != player.getInventory().getHeldItemSlot())
			return;

		update(player, player.getInventory().getItemInMainHand());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSwapHands(PlayerSwapHandItemsEvent event) {
		Tasks.wait(1, () -> update(event.getPlayer(), event.getMainHandItem()));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		update(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		update(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
	}

	private void update(Player player, ItemStack item) {
		if (!player.hasPermission(getPermission())) {
			remove(player);
			return;
		}
		if (getApplicableItems().isTagged(item))
			apply(player);
		else
			remove(player);
	}

}
