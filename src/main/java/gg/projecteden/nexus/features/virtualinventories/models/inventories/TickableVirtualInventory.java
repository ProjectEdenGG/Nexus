package gg.projecteden.nexus.features.virtualinventories.models.inventories;

import gg.projecteden.nexus.features.virtualinventories.models.properties.VirtualInventoryProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

@Data
@NoArgsConstructor
public abstract class TickableVirtualInventory<T extends VirtualInventoryProperties> extends VirtualInventory<T> {
	protected int tick = 0;

	@Override
	public void openInventory(Player player) {
		super.openInventory(player);
		updateInventory();
	}

	public abstract void updateInventory();

	public boolean tick() {
		tick++;
		if (tick > 6000)
			tick = 0;

		return false;
	}

}
