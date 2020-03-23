package me.pugabyte.bncore.features.votes.vps;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

public class VPSProvider implements InventoryProvider {
	private VPSMenu menu;

	public VPSProvider(VPSMenu menu) {
		this.menu = menu;
	}

	@Override
	public void init(Player player, InventoryContents inventoryContents) {

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {}

}
