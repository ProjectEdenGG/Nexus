package gg.projecteden.nexus.features.virtualinventories.models.tiles.impl;

import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualChest;
import gg.projecteden.nexus.features.virtualinventories.models.tiles.Tile;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class ChestTile extends Tile<VirtualChest> {

	public ChestTile(@NotNull VirtualChest virtualInv, @NotNull Location location) {
		super(virtualInv, location);
	}

}
