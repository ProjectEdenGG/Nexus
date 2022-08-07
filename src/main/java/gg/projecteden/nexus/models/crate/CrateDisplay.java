package gg.projecteden.nexus.models.crate;

import org.bukkit.inventory.ItemStack;

public interface CrateDisplay {

	String getDisplayName();

	ItemStack getDisplayItem();

	double getWeight();

	boolean isActive();

}
