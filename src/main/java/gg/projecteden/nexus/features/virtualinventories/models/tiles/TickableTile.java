package gg.projecteden.nexus.features.virtualinventories.models.tiles;

import gg.projecteden.nexus.features.virtualinventories.models.inventories.TickableVirtualInventory;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class TickableTile<V extends TickableVirtualInventory<?>> extends Tile<V> {

	public TickableTile(@NotNull V virtualInv, @NotNull Location location) {
		super(virtualInv, location);
	}

	public int getTick() {
		return getVirtualInv().getTick();
	}

	public void tick() {
		if (blockDataMatches(getBlock())) {
			virtualInv.tick();
		} else {
			breakTile();
		}
	}

}
