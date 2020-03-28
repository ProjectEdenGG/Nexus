package me.pugabyte.bncore.features.atp;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

public class ATPMenu {

	public void open(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.size(5, 9)
				.title("Animal Teleport Pens")
				.provider(new ATPMenuProvider())
				.build();
		INV.open(player);
	}

}
