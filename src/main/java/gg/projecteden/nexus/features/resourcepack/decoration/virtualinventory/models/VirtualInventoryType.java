package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum VirtualInventoryType {
	FURNACE(FurnaceProperties.FURNACE),
	BLAST_FURNACE(FurnaceProperties.BLAST_FURNACE),
	SMOKER(FurnaceProperties.SMOKER),
	;

	@Getter
	private final Properties properties;
}
