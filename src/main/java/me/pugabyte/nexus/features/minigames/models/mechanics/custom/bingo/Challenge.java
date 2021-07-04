package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo;

import eden.utils.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BreakChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.ConsumeChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CraftChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CustomChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CustomChallenge.CustomTask;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DimensionChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.KillChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.ObtainChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.PlaceChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StatisticIncreaseChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StructureChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import me.pugabyte.nexus.utils.FuzzyItemStack;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.StructureType;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bukkit.Material.*;

@Getter
@AllArgsConstructor
public enum Challenge {
	// Breaking
	BREAK_32_COAL_ORE(new BreakChallenge(new FuzzyItemStack(COAL_ORE, 32))),
	BREAK_16_IRON_ORE(new BreakChallenge(new FuzzyItemStack(IRON_ORE, 16))),
	BREAK_5_DIAMOND_ORE(new BreakChallenge(new FuzzyItemStack(DIAMOND_ORE, 5))),
	BREAK_2_EMERALD_ORE(new BreakChallenge(new FuzzyItemStack(EMERALD_ORE, 2))),
	BREAK_192_STONE(new BreakChallenge(new FuzzyItemStack(STONE, 192))),
	BREAK_16_SUGAR_CANE(new BreakChallenge(new FuzzyItemStack(SUGAR_CANE, 16))),
	BREAK_192_NETHERRACK(new BreakChallenge(new FuzzyItemStack(NETHERRACK, 192))),
	BREAK_128_MAGMA_BLOCKS(new BreakChallenge(new FuzzyItemStack(MAGMA_BLOCK, 128))),
	BREAK_64_GLOWSTONE(new BreakChallenge(new FuzzyItemStack(GLOWSTONE, 64))),
	BREAK_64_NETHER_BRICKS(new BreakChallenge(new FuzzyItemStack(NETHER_BRICKS, 64))),
	BREAK_64_BASALT(new BreakChallenge(new FuzzyItemStack(BASALT, 64))),
	BREAK_64_BLACKSTONE(new BreakChallenge(new FuzzyItemStack(BLACKSTONE, 64))),
	BREAK_32_PODZOL(new BreakChallenge(new FuzzyItemStack(PODZOL, 32))),
	BREAK_32_GRAVEL(new BreakChallenge(new FuzzyItemStack(GRAVEL, 32))),
	BREAK_3_OF_EACH_TULIP(new BreakChallenge(FuzzyItemStack.ofEach(MaterialTag.TULIPS, 1))),
	BREAK_1_MONSTER_SPAWNER(new BreakChallenge(new FuzzyItemStack(SPAWNER, 1))),
	BREAK_32_BAMBOO(new BreakChallenge(new FuzzyItemStack(BAMBOO, 32))),
	BREAK_1_OF_EVERY_ORE(new BreakChallenge(FuzzyItemStack.ofEach(new MaterialTag(MaterialTag.MINERAL_ORES).exclude(EMERALD_ORE), 1))),
	BREAK_64_OF_COMMON_BLOCKS(new BreakChallenge(FuzzyItemStack.ofEach(new MaterialTag(SAND, GRAVEL, STONE, GRASS, DIRT, NETHERRACK), 64))),

	// Placing
	PLACE_6_OF_EACH_RAIL(new PlaceChallenge(FuzzyItemStack.ofEach(new MaterialTag(RAIL, POWERED_RAIL, DETECTOR_RAIL, ACTIVATOR_RAIL), 6))),
	PLACE_9_STACKS_OF_BLOCKS(new PlaceChallenge(new FuzzyItemStack(MaterialTag.BLOCKS, 9 * 64))),
	PLANT_16_OAK_SAPLINGS(new PlaceChallenge(new FuzzyItemStack(OAK_SAPLING, 16))),

	// Crafting
	CRAFT_16_FENCE_GATES(new CraftChallenge(new FuzzyItemStack(MaterialTag.FENCE_GATES, 16))),
	CRAFT_16_FENCES(new CraftChallenge(new FuzzyItemStack(MaterialTag.FENCES, 16))),
	CRAFT_IRON_ARMOR(new CraftChallenge(FuzzyItemStack.ofEach(MaterialTag.ARMOR_IRON, 1))),
	CRAFT_32_WALLS(new CraftChallenge(new FuzzyItemStack(MaterialTag.WALLS, 32))),
	CRAFT_32_POLISHED_BLACKSTONE_BRICKS(new CraftChallenge(new FuzzyItemStack(POLISHED_BLACKSTONE_BRICKS, 32))),
	CRAFT_A_CAKE(new CraftChallenge(new FuzzyItemStack(CAKE, 1))),
	CRAFT_8_DRIED_KELP_BLOCKS(new CraftChallenge(new FuzzyItemStack(DRIED_KELP_BLOCK, 8))),
	CRAFT_16_ITEM_FRAMES(new CraftChallenge(new FuzzyItemStack(ITEM_FRAME, 16))),
	// 1.17 CRAFT_4_GLOW_ITEM_FRAMES(new CraftChallenge(new FuzzyItemStack(GLOW_ITEM_FRAME))),
	CRAFT_64_CHISELED_STONE_BRICKS(new CraftChallenge(new FuzzyItemStack(CHISELED_STONE_BRICKS, 64))), // TODO Do I need to worry about stonecutters?
	CRAFT_3_BLAST_FURNACES(new CraftChallenge(new FuzzyItemStack(BLAST_FURNACE, 3))),
	CRAFT_16_SOUL_CAMPFIRES(new CraftChallenge(new FuzzyItemStack(SOUL_CAMPFIRE, 16))),
	CRAFT_4_LECTERNS(new CraftChallenge(new FuzzyItemStack(LECTERN, 4))),
	CRAFT_2_DAYLIGHT_DETECTORS(new CraftChallenge(new FuzzyItemStack(DAYLIGHT_DETECTOR, 2))),
	CRAFT_8_TARGET_BLOCKS(new CraftChallenge(new FuzzyItemStack(TARGET, 8))),
	CRAFT_DIFFERENT_TYPES_OF_BOOTS(new CraftChallenge(FuzzyItemStack.ofEach(new MaterialTag(MaterialTag.ALL_BOOTS).exclude(NETHERITE_BOOTS), 1))),
	CRAFT_A_GOLDEN_APPLE(new CraftChallenge(new FuzzyItemStack(GOLDEN_APPLE, 1))),
	CRAFT_8_ARMOR_STANDS(new CraftChallenge(new FuzzyItemStack(ARMOR_STAND, 8))),
	CRAFT_A_COMPASS(new CraftChallenge(new FuzzyItemStack(COMPASS, 1))),

	// Obtaining
	OBTAIN_4_OBSIDIAN(new ObtainChallenge(new FuzzyItemStack(OBSIDIAN, 4))),
	OBTAIN_CROPS(new ObtainChallenge(FuzzyItemStack.ofEach(new MaterialTag(BEETROOT, CARROT, WHEAT, POTATO, APPLE), 1))),
	CATCH_8_FISH(new ObtainChallenge(new FuzzyItemStack(MaterialTag.RAW_FISH, 8))),
	CATCH_6_FISH_WITH_A_BUCKET(new ObtainChallenge(new FuzzyItemStack(MaterialTag.FISH_BUCKETS, 6))),
	OBTAIN_1_OF_EVERY_DYE(new ObtainChallenge(FuzzyItemStack.ofEach(MaterialTag.DYES, 1))),
	OBTAIN_1_NETHERITE_INGOT(new ObtainChallenge(new FuzzyItemStack(NETHERITE_INGOT, 1))),
	OBTAIN_1_TOTEM_OF_UNDYING(new ObtainChallenge(new FuzzyItemStack(TOTEM_OF_UNDYING, 1))),
	OBTAIN_2_ENDER_PEARLS(new ObtainChallenge(new FuzzyItemStack(ENDER_PEARL, 2))),

	// Killing
	KILL_6_SKELETONS(new KillChallenge(EntityType.SKELETON, 6)),
	KILL_6_ZOMBIES(new KillChallenge(EntityType.ZOMBIE, 6)),
	KILL_6_DROWNED(new KillChallenge(EntityType.DROWNED, 6)),
	KILL_2_CREEPERS(new KillChallenge(EntityType.CREEPER, 2)),
	KILL_1_ENDERMAN(new KillChallenge(EntityType.ENDERMAN, 1)),
	KILL_8_PIGLINS(new KillChallenge(EntityType.PIGLIN, 8)),
	KILL_8_BLAZES(new KillChallenge(EntityType.BLAZE, 8)),
	KILL_4_HOGLINS(new KillChallenge(EntityType.HOGLIN, 4)),
	KILL_2_PIGLIN_BRUTES(new KillChallenge(EntityType.PIGLIN_BRUTE, 2)),
	KILL_2_MAGMA_CUBES(new KillChallenge(EntityType.MAGMA_CUBE, 2)),
	KILL_1_GHAST(new KillChallenge(EntityType.GHAST, 1)),

	// Consuming
	EAT_16_DRIED_KELP(new ConsumeChallenge(new FuzzyItemStack(DRIED_KELP, 16))),
	// TODO I dont think i can currently support this one
//	EAT_4_DIFFERENT_COOKED_FOODS(new ConsumeChallenge(FuzzyItemStack.ofEach(new MaterialTag("COOKED_", MatchMode.PREFIX), 1))),
	EAT_2_SUSPICIOUS_STEW(new ConsumeChallenge(new FuzzyItemStack(SUSPICIOUS_STEW, 2))),
	EAT_1_MUSHROOM_STEW(new ConsumeChallenge(new FuzzyItemStack(MUSHROOM_STEW, 1))),
	DRINK_A_POTION(new ConsumeChallenge(new FuzzyItemStack(POTION, 1))),

	// Biomes
	// TODO

	// Dimensions
	ENTER_THE_NETHER(new DimensionChallenge(Environment.NETHER)),

	// Structures
	FIND_A_NETHER_FORTRESS(new StructureChallenge(StructureType.NETHER_FORTRESS)),
	FIND_A_BASTION(new StructureChallenge(StructureType.BASTION_REMNANT)),
	FIND_A_VILLAGE(new StructureChallenge(StructureType.VILLAGE)),
	FIND_A_SHIPWRECK(new StructureChallenge(StructureType.SHIPWRECK)),

	// Statistic Increase
	WALK_1_KILOMETER(new StatisticIncreaseChallenge(DIAMOND_BOOTS, Statistic.WALK_ONE_CM, 100000)),
	BOAT_1_KILOMETER(new StatisticIncreaseChallenge(OAK_BOAT, Statistic.BOAT_ONE_CM, 500000)),
	BREAK_A_WOODEN_PICKAXE(new StatisticIncreaseChallenge(WOODEN_PICKAXE, Statistic.BREAK_ITEM, WOODEN_PICKAXE, 1)),

	// Custom
	// TODO Better way to do this?
	SPAWN_AN_IRON_GOLEM(new CustomChallenge(IRON_BLOCK, CustomTask.SPAWN_AN_IRON_GOLEM)),
	SPAWN_A_SNOW_GOLEM(new CustomChallenge(Material.SNOW_BLOCK, CustomTask.SPAWN_A_SNOW_GOLEM)),
	CLIMB_TO_BUILD_HEIGHT(new CustomChallenge(BLUE_CONCRETE, CustomTask.CLIMB_TO_BUILD_HEIGHT)),
	DIG_TO_BEDROCK(new CustomChallenge(BEDROCK, CustomTask.DIG_TO_BEDROCK)),

	;

	private final IChallenge challenge;

	public <T extends IChallenge> T getChallenge() {
		return (T) challenge;
	}

	public static List<Challenge> shuffle() {
		ArrayList<Challenge> values = new ArrayList<>(Arrays.asList(Challenge.values()));
		Collections.shuffle(values);
		return values;
	}

	public ItemBuilder getDisplayItem() {
		return new ItemBuilder(challenge.getDisplayMaterial()).name(EnumUtils.prettyName(name())).lore("");
	}

}
