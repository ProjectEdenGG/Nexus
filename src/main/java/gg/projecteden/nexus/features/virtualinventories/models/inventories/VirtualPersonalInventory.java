package gg.projecteden.nexus.features.virtualinventories.models.inventories;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface VirtualPersonalInventory {
	UUID getOwner();
	Player getPlayer();
	Location getLocation();
}
