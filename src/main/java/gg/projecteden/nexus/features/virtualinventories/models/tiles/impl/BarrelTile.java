package gg.projecteden.nexus.features.virtualinventories.models.tiles.impl;

import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualBarrel;
import gg.projecteden.nexus.features.virtualinventories.models.tiles.Tile;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class BarrelTile extends Tile<VirtualBarrel> {

	public BarrelTile(@NotNull VirtualBarrel virtualInv, @NotNull Location location) {
		super(virtualInv, location);
	}

}
