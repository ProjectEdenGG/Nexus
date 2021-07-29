package gg.projecteden.nexus.features.survival.mobequipment;

import gg.projecteden.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.World.Environment;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public enum BiomeModifier {
	OCEAN(Dimension.OVERWORLD, Difficulty.EASY, Elevation.UNDERWATER, Moisture.DAMP),
	PLAINS(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	DESERT(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DRY),
	MOUNTAINS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.MOUNTAINS, Moisture.SNOWY),
	FOREST(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	TAIGA(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DAMP),
	SWAMP(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	RIVER(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DAMP),
	NETHER_WASTES(Dimension.NETHER, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	THE_END(Dimension.END, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	FROZEN_OCEAN(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.UNDERWATER, Moisture.FROZEN),
	FROZEN_RIVER(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.FROZEN),
	SNOWY_TUNDRA(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.SNOWY),
	SNOWY_MOUNTAINS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.MOUNTAINS, Moisture.SNOWY),
	MUSHROOM_FIELDS(Dimension.OVERWORLD, Difficulty.PEACEFUL, Elevation.GROUNDLEVEL, Moisture.DAMP),
	MUSHROOM_FIELD_SHORE(Dimension.OVERWORLD, Difficulty.PEACEFUL, Elevation.GROUNDLEVEL, Moisture.DAMP),
	BEACH(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DAMP),
	DESERT_HILLS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DRY),
	WOODED_HILLS(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	TAIGA_HILLS(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DAMP),
	MOUNTAIN_EDGE(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.SNOWY),
	JUNGLE(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	JUNGLE_HILLS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	JUNGLE_EDGE(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	DEEP_OCEAN(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.UNDERWATER, Moisture.DAMP),
	STONE_SHORE(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DAMP),
	SNOWY_BEACH(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.SNOWY),
	BIRCH_FOREST(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	BIRCH_FOREST_HILLS(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	DARK_FOREST(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	SNOWY_TAIGA(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.SNOWY),
	SNOWY_TAIGA_HILLS(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.SNOWY),
	GIANT_TREE_TAIGA(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	GIANT_TREE_TAIGA_HILLS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	WOODED_MOUNTAINS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.SNOWY),
	SAVANNA(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	SAVANNA_PLATEAU(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	BADLANDS(Dimension.OVERWORLD, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	WOODED_BADLANDS_PLATEAU(Dimension.OVERWORLD, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	BADLANDS_PLATEAU(Dimension.OVERWORLD, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	SMALL_END_ISLANDS(Dimension.END, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	END_MIDLANDS(Dimension.END, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	END_HIGHLANDS(Dimension.END, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	END_BARRENS(Dimension.END, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	WARM_OCEAN(Dimension.OVERWORLD, Difficulty.EASY, Elevation.UNDERWATER, Moisture.DAMP),
	LUKEWARM_OCEAN(Dimension.OVERWORLD, Difficulty.EASY, Elevation.UNDERWATER, Moisture.DAMP),
	COLD_OCEAN(Dimension.OVERWORLD, Difficulty.EASY, Elevation.UNDERWATER, Moisture.DAMP),
	DEEP_WARM_OCEAN(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.UNDERWATER, Moisture.DAMP),
	DEEP_LUKEWARM_OCEAN(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.UNDERWATER, Moisture.DAMP),
	DEEP_COLD_OCEAN(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.UNDERWATER, Moisture.FROZEN),
	DEEP_FROZEN_OCEAN(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.UNDERWATER, Moisture.FROZEN),
	THE_VOID(),
	SUNFLOWER_PLAINS(Dimension.OVERWORLD, Difficulty.PEACEFUL, Elevation.GROUNDLEVEL, Moisture.DRY),
	DESERT_LAKES(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DRY),
	GRAVELLY_MOUNTAINS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.MOUNTAINS, Moisture.SNOWY),
	FLOWER_FOREST(Dimension.OVERWORLD, Difficulty.PEACEFUL, Elevation.GROUNDLEVEL, Moisture.DRY),
	TAIGA_MOUNTAINS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.MOUNTAINS, Moisture.SNOWY),
	SWAMP_HILLS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	ICE_SPIKES(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.FROZEN),
	MODIFIED_JUNGLE(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	MODIFIED_JUNGLE_EDGE(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	TALL_BIRCH_FOREST(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	TALL_BIRCH_HILLS(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	DARK_FOREST_HILLS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	SNOWY_TAIGA_MOUNTAINS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.MOUNTAINS, Moisture.SNOWY),
	GIANT_SPRUCE_TAIGA(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	GIANT_SPRUCE_TAIGA_HILLS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	MODIFIED_GRAVELLY_MOUNTAINS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.MOUNTAINS, Moisture.SNOWY),
	SHATTERED_SAVANNA(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	SHATTERED_SAVANNA_PLATEAU(Dimension.OVERWORLD, Difficulty.EASY, Elevation.GROUNDLEVEL, Moisture.DRY),
	ERODED_BADLANDS(Dimension.OVERWORLD, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	MODIFIED_WOODED_BADLANDS_PLATEAU(Dimension.OVERWORLD, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	MODIFIED_BADLANDS_PLATEAU(Dimension.OVERWORLD, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	BAMBOO_JUNGLE(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	BAMBOO_JUNGLE_HILLS(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.GROUNDLEVEL, Moisture.DAMP),
	SOUL_SAND_VALLEY(Dimension.NETHER, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	CRIMSON_FOREST(Dimension.NETHER, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	WARPED_FOREST(Dimension.NETHER, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	BASALT_DELTAS(Dimension.NETHER, Difficulty.HARD, Elevation.GROUNDLEVEL, Moisture.DRY),
	DRIPSTONE_CAVES(Dimension.OVERWORLD, Difficulty.MEDIUM, Elevation.UNDERGROUND, Moisture.DAMP),
	LUSH_CAVES(Dimension.OVERWORLD, Difficulty.PEACEFUL, Elevation.UNDERGROUND, Moisture.DAMP),
	CUSTOM(),
	;

	@Getter
	Dimension dimension = null;
	@Getter
	Difficulty difficulty = null;
	@Getter
	Elevation elevation = null;
	@Getter
	Moisture moisture = null;

	public static List<BiomeModifier> valuesBy(Modifier modifier) {
		List<BiomeModifier> result = new ArrayList<>();
		for (BiomeModifier biomeModifier : values()) {
			if (biomeModifier.getModifiers().contains(modifier))
				result.add(biomeModifier);
		}
		return result;
	}

	private List<Modifier> getModifiers() {
		List<Modifier> modifiers = new ArrayList<>();
		for (BiomeModifier value : values()) {
			if (value.dimension != null)
				modifiers.add(Modifier.DIMENSION);
			if (value.difficulty != null)
				modifiers.add(Modifier.DIFFICULTY);
			if (value.elevation != null)
				modifiers.add(Modifier.ELEVATION);
			if (value.moisture != null)
				modifiers.add(Modifier.MOISTURE);
		}
		return modifiers;
	}

	public String getName() {
		return StringUtils.camelCase(this);
	}

	enum Modifier {
		DIMENSION,
		DIFFICULTY,
		ELEVATION,
		MOISTURE,
		;
	}


	@AllArgsConstructor
	enum Dimension {
		OVERWORLD(Environment.NORMAL),
		NETHER(Environment.NETHER),
		END(Environment.THE_END),
		;

		Environment dimension;
		@Getter
		static Modifier modifier = Modifier.DIMENSION;
	}

	enum Difficulty {
		PEACEFUL,
		EASY,
		MEDIUM,
		HARD,
		;

		@Getter
		static Modifier modifier = Modifier.DIFFICULTY;
	}

	enum Elevation {
		UNDERGROUND,
		UNDERWATER,
		GROUNDLEVEL,
		MOUNTAINS,
		;

		@Getter
		final static Modifier modifier = Modifier.ELEVATION;
	}

	enum Moisture {
		DRY,
		DAMP,
		FROZEN,
		SNOWY,
		;

		@Getter
		final static Modifier modifier = Modifier.MOISTURE;
	}

	/*
		if mob has custom enchanted item, don't drop the item
	 */


}
