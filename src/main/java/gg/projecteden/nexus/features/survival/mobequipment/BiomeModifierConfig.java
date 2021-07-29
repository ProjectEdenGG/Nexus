package gg.projecteden.nexus.features.survival.mobequipment;

import gg.projecteden.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World.Environment;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeDifficultyModifier.EASY;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeDifficultyModifier.HARD;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeDifficultyModifier.MEDIUM;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeDifficultyModifier.PEACEFUL;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeDimensionModifier.END;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeDimensionModifier.NETHER;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeDimensionModifier.OVERWORLD;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeElevationModifier.GROUNDLEVEL;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeElevationModifier.MOUNTAIN;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeElevationModifier.UNDERGROUND;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeElevationModifier.UNDERWATER;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeMoistureModifier.DAMP;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeMoistureModifier.DRY;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeMoistureModifier.FROZEN;
import static gg.projecteden.nexus.features.survival.mobequipment.BiomeModifierConfig.BiomeMoistureModifier.SNOWY;

@Getter
@AllArgsConstructor
public enum BiomeModifierConfig {
	// @formatter:off;
	OCEAN                            (  OVERWORLD,  EASY,      UNDERWATER,   DAMP    ),
	PLAINS                           (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	DESERT                           (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DRY     ),
	MOUNTAINS                        (  OVERWORLD,  MEDIUM,    MOUNTAIN,     SNOWY   ),
	FOREST                           (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	TAIGA                            (  OVERWORLD,  EASY,      GROUNDLEVEL,  DAMP    ),
	SWAMP                            (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	RIVER                            (  OVERWORLD,  EASY,      GROUNDLEVEL,  DAMP    ),
	NETHER_WASTES                    (  NETHER,     HARD,      GROUNDLEVEL,  DRY     ),
	THE_END                          (  END,        HARD,      GROUNDLEVEL,  DRY     ),
	FROZEN_OCEAN                     (  OVERWORLD,  MEDIUM,    UNDERWATER,   FROZEN  ),
	FROZEN_RIVER                     (  OVERWORLD,  EASY,      GROUNDLEVEL,  FROZEN  ),
	SNOWY_TUNDRA                     (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  SNOWY   ),
	SNOWY_MOUNTAINS                  (  OVERWORLD,  MEDIUM,    MOUNTAIN,     SNOWY   ),
	MUSHROOM_FIELDS                  (  OVERWORLD,  PEACEFUL,  GROUNDLEVEL,  DAMP    ),
	MUSHROOM_FIELD_SHORE             (  OVERWORLD,  PEACEFUL,  GROUNDLEVEL,  DAMP    ),
	BEACH                            (  OVERWORLD,  EASY,      GROUNDLEVEL,  DAMP    ),
	DESERT_HILLS                     (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DRY     ),
	WOODED_HILLS                     (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	TAIGA_HILLS                      (  OVERWORLD,  EASY,      GROUNDLEVEL,  DAMP    ),
	MOUNTAIN_EDGE                    (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  SNOWY   ),
	JUNGLE                           (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	JUNGLE_HILLS                     (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	JUNGLE_EDGE                      (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	DEEP_OCEAN                       (  OVERWORLD,  MEDIUM,    UNDERWATER,   DAMP    ),
	STONE_SHORE                      (  OVERWORLD,  EASY,      GROUNDLEVEL,  DAMP    ),
	SNOWY_BEACH                      (  OVERWORLD,  EASY,      GROUNDLEVEL,  SNOWY   ),
	BIRCH_FOREST                     (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	BIRCH_FOREST_HILLS               (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	DARK_FOREST                      (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	SNOWY_TAIGA                      (  OVERWORLD,  EASY,      GROUNDLEVEL,  SNOWY   ),
	SNOWY_TAIGA_HILLS                (  OVERWORLD,  EASY,      GROUNDLEVEL,  SNOWY   ),
	GIANT_TREE_TAIGA                 (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	GIANT_TREE_TAIGA_HILLS           (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	WOODED_MOUNTAINS                 (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  SNOWY   ),
	SAVANNA                          (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	SAVANNA_PLATEAU                  (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	BADLANDS                         (  OVERWORLD,  HARD,      GROUNDLEVEL,  DRY     ),
	WOODED_BADLANDS_PLATEAU          (  OVERWORLD,  HARD,      GROUNDLEVEL,  DRY     ),
	BADLANDS_PLATEAU                 (  OVERWORLD,  HARD,      GROUNDLEVEL,  DRY     ),
	SMALL_END_ISLANDS                (  END,        HARD,      GROUNDLEVEL,  DRY     ),
	END_MIDLANDS                     (  END,        HARD,      GROUNDLEVEL,  DRY     ),
	END_HIGHLANDS                    (  END,        HARD,      GROUNDLEVEL,  DRY     ),
	END_BARRENS                      (  END,        HARD,      GROUNDLEVEL,  DRY     ),
	WARM_OCEAN                       (  OVERWORLD,  EASY,      UNDERWATER,   DAMP    ),
	LUKEWARM_OCEAN                   (  OVERWORLD,  EASY,      UNDERWATER,   DAMP    ),
	COLD_OCEAN                       (  OVERWORLD,  EASY,      UNDERWATER,   DAMP    ),
	DEEP_WARM_OCEAN                  (  OVERWORLD,  MEDIUM,    UNDERWATER,   DAMP    ),
	DEEP_LUKEWARM_OCEAN              (  OVERWORLD,  MEDIUM,    UNDERWATER,   DAMP    ),
	DEEP_COLD_OCEAN                  (  OVERWORLD,  MEDIUM,    UNDERWATER,   FROZEN  ),
	DEEP_FROZEN_OCEAN                (  OVERWORLD,  MEDIUM,    UNDERWATER,   FROZEN  ),
	THE_VOID                         (                                               ),
	SUNFLOWER_PLAINS                 (  OVERWORLD,  PEACEFUL,  GROUNDLEVEL,  DRY     ),
	DESERT_LAKES                     (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DRY     ),
	GRAVELLY_MOUNTAINS               (  OVERWORLD,  MEDIUM,    MOUNTAIN,     SNOWY   ),
	FLOWER_FOREST                    (  OVERWORLD,  PEACEFUL,  GROUNDLEVEL,  DRY     ),
	TAIGA_MOUNTAINS                  (  OVERWORLD,  MEDIUM,    MOUNTAIN,     SNOWY   ),
	SWAMP_HILLS                      (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	ICE_SPIKES                       (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  FROZEN  ),
	MODIFIED_JUNGLE                  (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	MODIFIED_JUNGLE_EDGE             (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	TALL_BIRCH_FOREST                (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	TALL_BIRCH_HILLS                 (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	DARK_FOREST_HILLS                (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	SNOWY_TAIGA_MOUNTAINS            (  OVERWORLD,  MEDIUM,    MOUNTAIN,     SNOWY   ),
	GIANT_SPRUCE_TAIGA               (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	GIANT_SPRUCE_TAIGA_HILLS         (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	MODIFIED_GRAVELLY_MOUNTAINS      (  OVERWORLD,  MEDIUM,    MOUNTAIN,     SNOWY   ),
	SHATTERED_SAVANNA                (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	SHATTERED_SAVANNA_PLATEAU        (  OVERWORLD,  EASY,      GROUNDLEVEL,  DRY     ),
	ERODED_BADLANDS                  (  OVERWORLD,  HARD,      GROUNDLEVEL,  DRY     ),
	MODIFIED_WOODED_BADLANDS_PLATEAU (  OVERWORLD,  HARD,      GROUNDLEVEL,  DRY     ),
	MODIFIED_BADLANDS_PLATEAU        (  OVERWORLD,  HARD,      GROUNDLEVEL,  DRY     ),
	BAMBOO_JUNGLE                    (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	BAMBOO_JUNGLE_HILLS              (  OVERWORLD,  MEDIUM,    GROUNDLEVEL,  DAMP    ),
	SOUL_SAND_VALLEY                 (  NETHER,     HARD,      GROUNDLEVEL,  DRY     ),
	CRIMSON_FOREST                   (  NETHER,     HARD,      GROUNDLEVEL,  DRY     ),
	WARPED_FOREST                    (  NETHER,     HARD,      GROUNDLEVEL,  DRY     ),
	BASALT_DELTAS                    (  NETHER,     HARD,      GROUNDLEVEL,  DRY     ),
	DRIPSTONE_CAVES                  (  OVERWORLD,  MEDIUM,    UNDERGROUND,  DAMP    ),
	LUSH_CAVES                       (  OVERWORLD,  PEACEFUL,  UNDERGROUND,  DAMP    ),
	CUSTOM                           (                                               ),
	// @formatter:on;
	;

	private final BiomeDimensionModifier dimension;
	private final BiomeDifficultyModifier difficulty;
	private final BiomeElevationModifier elevation;
	private final BiomeMoistureModifier moisture;

	BiomeModifierConfig() {
		this.dimension = null;
		this.difficulty = null;
		this.elevation = null;
		this.moisture = null;
	}

	public static List<BiomeModifierConfig> valuesBy(IBiomeModifier modifier) {
		return new ArrayList<>() {{
			for (BiomeModifierConfig biomeModifierConfig : values())
				if (modifier.equals(biomeModifierConfig.getModifier(modifier.getModifierType())))
					add(biomeModifierConfig);
		}};
	}

	public IBiomeModifier getModifier(BiomeModifierType modifierType) {
		return switch (modifierType) {
			case DIMENSION -> dimension;
			case DIFFICULTY -> difficulty;
			case ELEVATION -> elevation;
			case MOISTURE -> moisture;
		};
	}

	public String getName() {
		return StringUtils.camelCase(this);
	}

	@Getter
	@AllArgsConstructor
	enum BiomeModifierType {
		DIMENSION(BiomeDimensionModifier.class),
		DIFFICULTY(BiomeDifficultyModifier.class),
		ELEVATION(BiomeElevationModifier.class),
		MOISTURE(BiomeMoistureModifier.class),
		;

		private final Class<? extends IBiomeModifier> modifierClass;

		static BiomeModifierType of(IBiomeModifier modifier) {
			for (BiomeModifierType value : values())
				if (value.getModifierClass().equals(modifier.getClass()))
					return value;

			return null;
		}
	}

	interface IBiomeModifier {

		String name();

		default BiomeModifierType getModifierType() {
			return BiomeModifierType.of(this);
		}

	}

	@AllArgsConstructor
	enum BiomeDimensionModifier implements IBiomeModifier {
		OVERWORLD(Environment.NORMAL),
		NETHER(Environment.NETHER),
		END(Environment.THE_END),
		;

		Environment dimension;
	}

	enum BiomeDifficultyModifier implements IBiomeModifier {
		PEACEFUL,
		EASY,
		MEDIUM,
		HARD,
		;
	}

	enum BiomeElevationModifier implements IBiomeModifier {
		UNDERGROUND,
		UNDERWATER,
		GROUNDLEVEL,
		MOUNTAIN,
		;
	}

	enum BiomeMoistureModifier implements IBiomeModifier {
		DRY,
		DAMP,
		FROZEN,
		SNOWY,
		;
	}

	/*
		if mob has custom enchanted item, don't drop the item
	 */


}
