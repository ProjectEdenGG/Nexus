package gg.projecteden.nexus.models.crate;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface CrateDisplay {

	String getDisplayName();

	ItemStack getDisplayItem();

	double getWeight();

	default double getWeightForPlayer(Player player) {
		return getWeight();
	}

	boolean isActive();

}
