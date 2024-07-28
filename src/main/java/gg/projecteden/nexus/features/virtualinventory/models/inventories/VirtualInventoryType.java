package gg.projecteden.nexus.features.virtualinventory.models.inventories;

import gg.projecteden.nexus.features.virtualinventory.models.inventories.impl.VirtualFurnace;
import gg.projecteden.nexus.features.virtualinventory.models.inventories.impl.VirtualPersonalFurnace;
import gg.projecteden.nexus.features.virtualinventory.models.properties.VirtualInventoryProperties;
import gg.projecteden.nexus.features.virtualinventory.models.properties.impl.FurnaceProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum VirtualInventoryType {
	FURNACE(Material.FURNACE, VirtualFurnace::new, VirtualPersonalFurnace::new, FurnaceProperties.FURNACE),
	BLAST_FURNACE(Material.BLAST_FURNACE, VirtualFurnace::new, VirtualPersonalFurnace::new, FurnaceProperties.BLAST_FURNACE),
	SMOKER(Material.SMOKER, VirtualFurnace::new, VirtualPersonalFurnace::new, FurnaceProperties.SMOKER),
	;

	private final Material material;
	private final Function<VirtualInventoryType, VirtualInventory<?>> sharedConstructor;
	private final TriFunction<VirtualInventoryType, Location, Player, VirtualPersonalInventory> personalConstructor;
	private final VirtualInventoryProperties properties;

	public VirtualInventoryType of(Block block) {
		for (VirtualInventoryType type : values())
			if (type.material == block.getType())
				return type;

		return null;
	}
}
