package gg.projecteden.nexus.features.virtualinventory.models.inventories.impl;

import dev.morphia.annotations.Converters;
import gg.projecteden.nexus.features.virtualinventory.models.inventories.VirtualInventoryType;
import gg.projecteden.nexus.features.virtualinventory.models.inventories.VirtualPersonalInventory;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@NoArgsConstructor
@Converters(LocationConverter.class)
public class VirtualPersonalFurnace extends VirtualFurnace implements VirtualPersonalInventory {
	private Location location;
	private UUID owner;

	public VirtualPersonalFurnace(VirtualInventoryType type, Location location, Player player) {
		super(type);
		this.location = location;
		this.owner = player.getUniqueId();
	}

	@Override
	public Player getPlayer() {
		return PlayerUtils.getPlayer(owner).getPlayer();
	}

}
