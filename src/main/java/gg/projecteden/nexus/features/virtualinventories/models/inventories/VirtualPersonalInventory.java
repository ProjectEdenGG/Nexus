package gg.projecteden.nexus.features.virtualinventories.models.inventories;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface VirtualPersonalInventory {
	Location getLocation();
	Player getPlayer();
}
