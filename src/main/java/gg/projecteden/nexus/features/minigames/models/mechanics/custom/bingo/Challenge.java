package gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo;

import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BiomeChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BreakChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BreedChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.ConsumeChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CraftChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CustomChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CustomChallenge.CustomTask;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DeathChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DimensionChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.KillChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.ObtainChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.PlaceChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StatisticIncreaseChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StructureChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.TameChallenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.IChallenge;
import gg.projecteden.nexus.utils.BiomeTag;
import gg.projecteden.nexus.utils.FuzzyItemStack;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.MaterialTag.MatchMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.StructureType;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.entity.EntityType.COW;
import static org.bukkit.entity.EntityType.PIG;

@Getter
@AllArgsConstructor
public enum Challenge {
	// Breaking
	BREAK_32_COAL_ORE(new BreakChallenge(new FuzzyItemStack(MaterialTag.COAL_ORES, 32))),
	BREAK_16_IRON_ORE(new BreakChallenge(new FuzzyItemStack(MaterialTag.IRON_ORES, 16))),
//	BREAK_5_DIAMOND_ORE(new BreakChallenge(new FuzzyItemStack(DIAMOND_ORE, 5))),
	BREAK_192_STONE(new BreakChallenge(new FuzzyItemStack(STONE, 192))),
	BREAK_16_SUGAR_CANE(new BreakChallenge(new FuzzyItemStack(SUGAR_CANE, 16))),
	BREAK_192_NETHERRACK(new BreakChallenge(new FuzzyItemStack(NETHERRACK, 192))),
//	BREAK_128_MAGMA_BLOCKS(new BreakChallenge(new FuzzyItemStack(MAGMA_BLOCK, 128))),
	BREAK_64_GLOWSTONE(new BreakChallenge(new FuzzyItemStack(GLOWSTONE, 64))),
	BREAK_64_NETHER_BRICKS(new BreakChallenge(new FuzzyItemStack(NETHER_BRICKS, 64))),
	BREAK_64_BASALT(new BreakChallenge(new FuzzyItemStack(BASALT, 64))),
	BREAK_64_BLACKSTONE(new BreakChallenge(new FuzzyItemStack(BLACKSTONE, 64))),
	BREAK_32_SHROOMLIGHT(new BreakChallenge(new FuzzyItemStack(SHROOMLIGHT, 32))),
//	BREAK_1_ANCIENT_DEBRIS(new BreakChallenge(new FuzzyItemStack(ANCIENT_DEBRIS, 1))),
	BREAK_32_NETHER_GOLD_ORE(new BreakChallenge(new FuzzyItemStack(NETHER_GOLD_ORE, 32))),
	BREAK_16_SOUL_SAND(new BreakChallenge(new FuzzyItemStack(SOUL_SAND, 16))),
	BREAK_32_PODZOL(new BreakChallenge(new FuzzyItemStack(PODZOL, 32))),
	BREAK_32_GRAVEL(new BreakChallenge(new FuzzyItemStack(GRAVEL, 32))),
	BREAK_3_OF_EACH_TULIP(new BreakChallenge(FuzzyItemStack.ofEach(MaterialTag.TULIPS, 3))),
	//	BREAK_1_MONSTER_SPAWNER(new BreakChallenge(new FuzzyItemStack(SPAWNER, 1))),
	BREAK_32_BAMBOO(new BreakChallenge(new FuzzyItemStack(BAMBOO, 32))),
	BREAK_1_OF_EVERY_ORE(new BreakChallenge(FuzzyItemStack.ofEach(new MaterialTag(MaterialTag.MINERAL_ORES).exclude(EMERALD_ORE).exclude(new MaterialTag("ORE", MatchMode.CONTAINS, MaterialTag.ALL_DEEPSLATE)), 1))),
	BREAK_64_OF_COMMON_BLOCKS(new BreakChallenge(FuzzyItemStack.ofEach(new MaterialTag(SAND, GRAVEL, STONE, SHORT_GRASS, DIRT, NETHERRACK), 64))),
//	BREAK_4_OBSIDIAN(new BreakChallenge(new FuzzyItemStack(OBSIDIAN, 4))),
	BREAK_64_LEAVES(new BreakChallenge(new FuzzyItemStack(MaterialTag.LEAVES, 64))),
	BREAK_2_OF_DIFFERENT_TALL_FLOWERS(new BreakChallenge(new FuzzyItemStack(Set.of(PEONY, ROSE_BUSH, LILAC), 2))),

	// Placing
//	PLACE_6_OF_EACH_RAIL(new PlaceChallenge(FuzzyItemStack.ofEach(new MaterialTag(RAIL, POWERED_RAIL, DETECTOR_RAIL, ACTIVATOR_RAIL), 6))),
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
	// 1.17 + mob spawn fix? CRAFT_4_GLOW_ITEM_FRAMES(new CraftChallenge(new FuzzyItemStack(GLOW_ITEM_FRAME))),
	CRAFT_64_CHISELED_STONE_BRICKS(new CraftChallenge(new FuzzyItemStack(CHISELED_STONE_BRICKS, 64))), // TODO Do I need to worry about stonecutters?
	CRAFT_3_BLAST_FURNACES(new CraftChallenge(new FuzzyItemStack(BLAST_FURNACE, 3))),
	CRAFT_16_SOUL_CAMPFIRES(new CraftChallenge(new FuzzyItemStack(SOUL_CAMPFIRE, 16))),
	CRAFT_32_POLISHED_BASALT(new CraftChallenge(new FuzzyItemStack(POLISHED_BASALT, 32))),
	CRAFT_12_BOOKS(new CraftChallenge(new FuzzyItemStack(BOOK, 12))),
	CRAFT_2_BOOKSHELVES(new CraftChallenge(new FuzzyItemStack(BOOKSHELF, 2))),
	CRAFT_2_LECTERNS(new CraftChallenge(new FuzzyItemStack(LECTERN, 2))),
	CRAFT_2_DAYLIGHT_DETECTORS(new CraftChallenge(new FuzzyItemStack(DAYLIGHT_DETECTOR, 2))),
	CRAFT_2_OBSERVER(new CraftChallenge(new FuzzyItemStack(OBSERVER, 2))),
	CRAFT_2_COMPARATORS(new CraftChallenge(new FuzzyItemStack(COMPARATOR, 2))),
	CRAFT_A_JUKEBOX(new CraftChallenge(new FuzzyItemStack(JUKEBOX, 1))),
	CRAFT_8_TARGET_BLOCKS(new CraftChallenge(new FuzzyItemStack(TARGET, 8))),
//	CRAFT_DIFFERENT_TYPES_OF_BOOTS(new CraftChallenge(FuzzyItemStack.ofEach(new MaterialTag(MaterialTag.ALL_BOOTS).exclude(CHAINMAIL_BOOTS, NETHERITE_BOOTS), 1))),
	CRAFT_A_GOLDEN_APPLE(new CraftChallenge(new FuzzyItemStack(GOLDEN_APPLE, 1))),
	CRAFT_8_ARMOR_STANDS(new CraftChallenge(new FuzzyItemStack(ARMOR_STAND, 8))),
	CRAFT_A_COMPASS(new CraftChallenge(new FuzzyItemStack(COMPASS, 1))),
	CRAFT_A_CLOCK(new CraftChallenge(new FuzzyItemStack(CLOCK, 1))),
	CRAFT_32_CONCRETE_POWDER(new CraftChallenge(new FuzzyItemStack(MaterialTag.CONCRETE_POWDERS, 32))),
	CRAFT_1_TNT(new CraftChallenge(new FuzzyItemStack(TNT, 1))),
	CRAFT_2_LOOMS(new CraftChallenge(new FuzzyItemStack(LOOM, 2))),
	CRAFT_A_FLOWER_BANNER_PATTERN(new CraftChallenge(new FuzzyItemStack(FLOWER_BANNER_PATTERN, 1))),
	CRAFT_3_BANNERS(new CraftChallenge(new FuzzyItemStack(MaterialTag.ALL_BANNERS, 3))),
	CRAFT_AN_ANVIL(new CraftChallenge(new FuzzyItemStack(ANVIL, 1))),
	CRAFT_2_CAULDRONS(new CraftChallenge(new FuzzyItemStack(CAULDRON, 2))),
	CRAFT_3_HOPPERS(new CraftChallenge(new FuzzyItemStack(HOPPER, 3))),
	CRAFT_2_CHEST_MINECARTS(new CraftChallenge(new FuzzyItemStack(CHEST_MINECART, 2))),
	CRAFT_2_FURNACE_MINECARTS(new CraftChallenge(new FuzzyItemStack(FURNACE_MINECART, 2))),
	CRAFT_2_HOPPER_MINECARTS(new CraftChallenge(new FuzzyItemStack(HOPPER_MINECART, 2))),
	CRAFT_12_CHAINS(new CraftChallenge(new FuzzyItemStack(CHAIN, 12))),
	CRAFT_32_BRICKS(new CraftChallenge(new FuzzyItemStack(BRICKS, 32))),
	CRAFT_6_IRON_DOOR(new CraftChallenge(new FuzzyItemStack(IRON_DOOR, 6))),
	CRAFT_3_IRON_TRAPDOORS(new CraftChallenge(new FuzzyItemStack(IRON_TRAPDOOR, 3))),
	CRAFT_8_PISTONS(new CraftChallenge(new FuzzyItemStack(PISTON, 8))),
	CRAFT_A_DISPENSER(new CraftChallenge(new FuzzyItemStack(DISPENSER, 1))),
	CRAFT_16_SPECTRAL_ARROWS(new CraftChallenge(new FuzzyItemStack(SPECTRAL_ARROW, 16))),
	CRAFT_A_CROSSBOW(new CraftChallenge(new FuzzyItemStack(CROSSBOW, 1))),
	CRAFT_1_JACK_O_LANTERN(new CraftChallenge(new FuzzyItemStack(JACK_O_LANTERN, 1))),
	CRAFT_64_STAINED_GLASS_PANES(new CraftChallenge(new FuzzyItemStack(MaterialTag.STAINED_GLASS_PANES, 64))),
	CRAFT_2_GOLDEN_CARROTS(new CraftChallenge(new FuzzyItemStack(GOLDEN_CARROT, 2))),
	CRAFT_2_GLISTERING_MELON_SLICES(new CraftChallenge(new FuzzyItemStack(GLISTERING_MELON_SLICE, 2))),
	CRAFT_2_FLETCHING_TABLES(new CraftChallenge(new FuzzyItemStack(FLETCHING_TABLE, 2))),
	CRAFT_A_LEATHER_HORSE_ARMOR(new CraftChallenge(new FuzzyItemStack(LEATHER_HORSE_ARMOR, 1))),
	CRAFT_2_PUMPKIN_PIE(new CraftChallenge(new FuzzyItemStack(PUMPKIN_PIE, 2))),
	CRAFT_63_WOOD(new CraftChallenge(new FuzzyItemStack(MaterialTag.ALL_WOOD, 63))),
	CRAFT_3_BONE_BLOCK(new CraftChallenge(new FuzzyItemStack(BONE_BLOCK, 3))),
	CRAFT_12_GLASS_BOTTLES(new CraftChallenge(new FuzzyItemStack(GLASS_BOTTLE, 12))),
	CRAFT_4_SNOW_BLOCKS(new CraftChallenge(new FuzzyItemStack(SNOW_BLOCK, 4))),

	// Obtaining
	CATCH_6_FISH_WITH_A_BUCKET(new ObtainChallenge(new FuzzyItemStack(MaterialTag.FISH_BUCKETS, 6))),
	OBTAIN_8_FISH(new ObtainChallenge(new FuzzyItemStack(MaterialTag.RAW_FISH, 8))),
	OBTAIN_CROPS(new ObtainChallenge(FuzzyItemStack.ofEach(new MaterialTag(BEETROOT, CARROT, WHEAT, POTATO, APPLE), 1))),
//	OBTAIN_1_OF_EVERY_DYE(new ObtainChallenge(FuzzyItemStack.ofEach(MaterialTag.DYES, 1))),
	OBTAIN_5_EMERALDS(new ObtainChallenge(new FuzzyItemStack(EMERALD, 5))),
	OBTAIN_16_SWEET_BERRIES(new ObtainChallenge(new FuzzyItemStack(SWEET_BERRIES, 16))),
	OBTAIN_8_ENDER_PEARLS(new ObtainChallenge(new FuzzyItemStack(ENDER_PEARL, 8))),

	// Killing
	KILL_16_COWS(new KillChallenge(COW, 16)),
	KILL_16_SHEEP(new KillChallenge(EntityType.SHEEP, 16)),
	KILL_16_PIG(new KillChallenge(PIG, 16)),
	KILL_16_CHICKEN(new KillChallenge(EntityType.CHICKEN, 16)),
	KILL_2_BATS(new KillChallenge(EntityType.BAT, 2)),
	KILL_8_SQUID(new KillChallenge(EntityType.SQUID, 8)),
	KILL_8_SALMON(new KillChallenge(EntityType.SALMON, 8)),
	KILL_8_COD(new KillChallenge(EntityType.COD, 8)),
	KILL_2_TURTLES(new KillChallenge(EntityType.TURTLE, 2)),

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
	KILL_3_STRIDER(new KillChallenge(EntityType.STRIDER, 3)),
	KILL_1_GHAST(new KillChallenge(EntityType.GHAST, 1)),


	// Dying
	DIE_BY_SUFFOCATION(new DeathChallenge(GRAVEL, DamageCause.SUFFOCATION)),
	//DIE_BY_STARVATION(new DeathChallenge(BOWL, DamageCause.STARVATION)), // Requires Hard Difficulty

	// Breeding
	BREED_2_COWS(new BreedChallenge(EntityType.COW, 2)),
	BREED_2_CHICKENS(new BreedChallenge(EntityType.CHICKEN, 2)),
	BREED_2_PIGS(new BreedChallenge(EntityType.PIG, 2)),

	// Taming
	TAME_A_WOLF(new TameChallenge(EntityType.WOLF, 1)),
	TAME_A_PARROT(new TameChallenge(EntityType.PARROT, 1)),

	// Consuming
	EAT_16_DRIED_KELP(new ConsumeChallenge(new FuzzyItemStack(DRIED_KELP, 16))),
	// TODO I dont think i can currently support this one
//	EAT_4_DIFFERENT_COOKED_FOODS(new ConsumeChallenge(FuzzyItemStack.ofEach(new MaterialTag("COOKED_", MatchMode.PREFIX), 1))),
	EAT_2_SUSPICIOUS_STEW(new ConsumeChallenge(new FuzzyItemStack(SUSPICIOUS_STEW, 2))),
	EAT_1_MUSHROOM_STEW(new ConsumeChallenge(new FuzzyItemStack(MUSHROOM_STEW, 1))),
//	DRINK_A_POTION(new ConsumeChallenge(new FuzzyItemStack(POTION, 1))),
	EAT_1_PUFFERFISH(new ConsumeChallenge(new FuzzyItemStack(PUFFERFISH, 1))),

	// Biomes
	FIND_A_SWAMP(new BiomeChallenge(BiomeTag.SWAMP)),
	FIND_AN_OCEAN(new BiomeChallenge(BiomeTag.OCEAN)),
	// TODO

	// Dimensions
	ENTER_THE_NETHER(new DimensionChallenge(Environment.NETHER)),

	// Structures
	FIND_A_NETHER_FORTRESS(new StructureChallenge(StructureType.NETHER_FORTRESS)),
	FIND_A_VILLAGE(new StructureChallenge(StructureType.VILLAGE)),
	FIND_A_SHIPWRECK(new StructureChallenge(StructureType.SHIPWRECK)),

	// Statistic Increase
	WALK_1_KILOMETER(new StatisticIncreaseChallenge(DIAMOND_BOOTS, Statistic.WALK_ONE_CM, 100000)),
	BOAT_2_KILOMETERS(new StatisticIncreaseChallenge(OAK_BOAT, Statistic.BOAT_ONE_CM, 200000)),
	BREAK_A_WOODEN_PICKAXE(new StatisticIncreaseChallenge(WOODEN_PICKAXE, Statistic.BREAK_ITEM, WOODEN_PICKAXE, 1)),

	// Custom
	// TODO Better way to do this?
	SPAWN_AN_IRON_GOLEM(new CustomChallenge(IRON_BLOCK, CustomTask.SPAWN_AN_IRON_GOLEM)),
	SPAWN_A_SNOW_GOLEM(new CustomChallenge(Material.SNOW_BLOCK, CustomTask.SPAWN_A_SNOW_GOLEM)),
	CLIMB_TO_BUILD_HEIGHT(new CustomChallenge(BLUE_CONCRETE, CustomTask.CLIMB_TO_BUILD_HEIGHT)),
	DIG_TO_BEDROCK(new CustomChallenge(BEDROCK, CustomTask.DIG_TO_BEDROCK)),
	TRADE_WITH_A_VILLAGER(new CustomChallenge(VILLAGER_SPAWN_EGG, CustomTask.TRADE_WITH_A_VILLAGER)),
	TRADE_WITH_A_PIGLIN(new CustomChallenge(GOLD_INGOT, CustomTask.TRADE_WITH_A_PIGLIN)),
	OBTAIN_DOLPHINS_GRACE(new CustomChallenge(DOLPHIN_SPAWN_EGG, CustomTask.OBTAIN_DOLPHINS_GRACE)),
//	DIE_BY_PUFFERFISH_POISON(new CustomChallenge(PUFFERFISH, CustomTask.CONSUME_A_PUFFERFISH, CustomTask.DIE_BY_PUFFERFISH_POISON)), a pufferfish can kill you, but not by the poison itself
	RIDE_A_HORSE(new CustomChallenge(SADDLE, CustomTask.RIDE_A_HORSE)),
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
