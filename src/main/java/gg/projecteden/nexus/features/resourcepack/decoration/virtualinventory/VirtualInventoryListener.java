package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.events.VirtualFurnaceExtractEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.VirtualFurnace;
import gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models.VirtualInventory;
import gg.projecteden.nexus.utils.Nullables;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class VirtualInventoryListener implements Listener {
	private final Map<HumanEntity, VirtualInventory> openInventories = new HashMap<>();

	public VirtualInventoryListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	private void onInventoryClick(InventoryClickEvent event) {
		HumanEntity clicker = event.getWhoClicked();
		if (openInventories.containsKey(clicker) && clicker instanceof Player player) {
			VirtualFurnace virtualFurnace = (VirtualFurnace) openInventories.get(clicker);
			int slot = event.getRawSlot();

			// Give XP to player when they extract from the furnace
			if (slot == 2) {
				ItemStack output = virtualFurnace.getOutput();
				if (Nullables.isNotNullOrAir(output)) {
					int exp = (int) virtualFurnace.extractExperience();
					VirtualFurnaceExtractEvent extractEvent = new VirtualFurnaceExtractEvent(virtualFurnace, player, output, exp);
					extractEvent.callEvent();

					((Player) clicker).giveExp(extractEvent.getExperience());
					event.setCurrentItem(extractEvent.getItemStack());
				}
			}
		}
	}
}
