package gg.projecteden.nexus.features.customblocks.models;

import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public enum CustomBlockTab {
	NONE(null),
	MISC("Misc Blocks"),
	COMPACTED("Compacted Blocks"),
	CONCRETE_BRICKS("Concrete Bricks"),
	GENERIC_CRATES("Generic Crates"),
	LANTERNS("Lanterns"),
	COLORED_PLANKS("Colored Planks"),
	CARVED_PLANKS("Carved Planks"),
	VERTICAL_PLANKS("Vertical Planks"),
	QUILTED_WOOL("Quilted Wool"),
	STONE_BRICKS("Stone Bricks"),
	CHISELED_STONE("Chiseled Stone"),
	STONE_PILLARS("Stone Pillars"),
	TERRACOTTA_SHINGLES("Terracotta Shingles"),
	FLORA("Flora"),
	ROCKS("Rocks"),
	;

	@Getter
	private final String menuTitle;

	CustomBlockTab(String title) {
		this.menuTitle = title;
	}

	public static void init() {
	}

	public static LinkedHashSet<CustomBlockTab> getMenuTabs() {
		return Arrays.stream(values()).filter(creativeTab -> creativeTab != NONE).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
