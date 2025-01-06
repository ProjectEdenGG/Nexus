package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public enum BoatType {
	OAK(Material.OAK_BOAT, Material.OAK_CHEST_BOAT),
	SPRUCE(Material.SPRUCE_BOAT, Material.SPRUCE_CHEST_BOAT),
	BIRCH(Material.BIRCH_BOAT, Material.BIRCH_CHEST_BOAT),
	JUNGLE(Material.JUNGLE_BOAT, Material.JUNGLE_CHEST_BOAT),
	ACACIA(Material.ACACIA_BOAT, Material.ACACIA_CHEST_BOAT),
	CHERRY(Material.CHERRY_BOAT, Material.CHERRY_CHEST_BOAT),
	DARK_OAK(Material.DARK_OAK_BOAT, Material.DARK_OAK_CHEST_BOAT),
	MANGROVE(Material.MANGROVE_BOAT, Material.MANGROVE_CHEST_BOAT),
	BAMBOO(Material.BAMBOO_RAFT, Material.BAMBOO_CHEST_RAFT),
	;

	private final Material boatMaterial;
	private final Material chestBoatMaterial;

	public Boat.Type getInternalBoatType() {
		return Boat.Type.values()[this.ordinal()];
	}

	public static @Nullable BoatType from(Material material) {
		for (BoatType boatType : values()) {
			if (boatType.getBoatMaterial() == material || boatType.getChestBoatMaterial() == material)
				return boatType;
		}
		return null;
	}
}
