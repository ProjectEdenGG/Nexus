package me.pugabyte.bncore.features.votes.vps;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage;
import me.pugabyte.bncore.features.votes.vps.VPSMenu.VPSPage.VPSSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class VPSProvider implements InventoryProvider {
	private VPSPage page;

	public VPSProvider(VPSPage page) {
		this.page = page;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		page.getItems().forEach((slot, item) -> {
			ItemStack display = setItemName(item);
			contents.set(slot, ClickableItem.from(display, e -> {}));
		});
	}

	@NotNull
	public ItemStack setItemName(VPSSlot item) {
		ItemStack display = item.getDisplay();
		ItemMeta itemMeta = display.getItemMeta();
		itemMeta.setDisplayName(item.getName());
		display.setItemMeta(itemMeta);
		return display;
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
