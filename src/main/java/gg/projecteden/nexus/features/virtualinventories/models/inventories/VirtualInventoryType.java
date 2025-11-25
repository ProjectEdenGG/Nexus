package gg.projecteden.nexus.features.virtualinventories.models.inventories;

import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualBarrel;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualChest;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualFurnace;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualPersonalBarrel;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualPersonalChest;
import gg.projecteden.nexus.features.virtualinventories.models.inventories.impl.VirtualPersonalFurnace;
import gg.projecteden.nexus.features.virtualinventories.models.properties.VirtualInventoryProperties;
import gg.projecteden.nexus.features.virtualinventories.models.properties.impl.BarrelProperties;
import gg.projecteden.nexus.features.virtualinventories.models.properties.impl.ChestProperties;
import gg.projecteden.nexus.features.virtualinventories.models.properties.impl.FurnaceProperties;
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
	CHEST(Material.CHEST, VirtualChest::new, VirtualPersonalChest::new, new ChestProperties()),
	BARREL(Material.BARREL, VirtualBarrel::new, VirtualPersonalBarrel::new, new BarrelProperties()),
	FURNACE(Material.FURNACE, VirtualFurnace::new, VirtualPersonalFurnace::new, FurnaceProperties.FURNACE),
	BLAST_FURNACE(Material.BLAST_FURNACE, VirtualFurnace::new, VirtualPersonalFurnace::new, FurnaceProperties.BLAST_FURNACE),
	SMOKER(Material.SMOKER, VirtualFurnace::new, VirtualPersonalFurnace::new, FurnaceProperties.SMOKER),
	;

	private final Material material;
	private final Function<VirtualInventoryType, VirtualInventory<?>> sharedConstructor;
	private final TriFunction<VirtualInventoryType, Location, Player, VirtualPersonalInventory> personalConstructor;
	private final VirtualInventoryProperties properties;

	public static VirtualInventoryType of(Block block) {
		for (VirtualInventoryType type : values())
			if (type.material == block.getType())
				return type;

		return null;
	}
}
