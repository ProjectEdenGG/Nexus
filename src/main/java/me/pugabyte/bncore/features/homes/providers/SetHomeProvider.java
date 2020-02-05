package me.pugabyte.bncore.features.homes.providers;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.models.homes.HomeOwner;
import org.bukkit.entity.Player;

public class SetHomeProvider implements InventoryProvider {
	private HomeOwner homeOwner;

	public SetHomeProvider(HomeOwner homeOwner) {
		this.homeOwner = homeOwner;
	}

	@Override
	public void init(Player player, InventoryContents contents) {

	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}
