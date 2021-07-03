package me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common;

import eden.utils.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BiomeChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.BreakChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.ConsumeChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.CraftChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.DimensionChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.KillChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.ObtainChallenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.StructureChallenge;
import me.pugabyte.nexus.utils.BiomeTag;
import me.pugabyte.nexus.utils.FuzzyItemStack;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.StructureType;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
	BREAK_64_NETHER_BRICKS(new BreakChallenge(new FuzzyItemStack(NETHER_BRICKS, 64))),
	BREAK_64_BASALT(new BreakChallenge(new FuzzyItemStack(BASALT, 64))),
	BREAK_64_BLACKSTONE(new BreakChallenge(new FuzzyItemStack(BLACKSTONE, 64))),
	BREAK_32_PODZOL(new BreakChallenge(new FuzzyItemStack(PODZOL, 32))),
	BREAK_32_GRAVEL(new BreakChallenge(new FuzzyItemStack(GRAVEL, 32))),
	BREAK_3_OF_EACH_TULIP(new BreakChallenge(FuzzyItemStack.ofEach(MaterialTag.TULIPS, 1))),
	BREAK_1_MONSTER_SPAWNER(new BreakChallenge(new FuzzyItemStack(SPAWNER, 1))),
	BREAK_32_BAMBOO(new BreakChallenge(new FuzzyItemStack(BAMBOO, 32))),
	BREAK_1_OF_EVERY_ORE(new BreakChallenge(FuzzyItemStack.ofEach(MaterialTag.MINERAL_ORES, 1))),
	BREAK_64_OF_COMMON_BLOCKS(new BreakChallenge(FuzzyItemStack.ofEach(new MaterialTag(SAND, GRAVEL, STONE, ANDESITE, DIORITE, GRANITE, NETHERRACK), 64))),
	BREAK_1_SPONGE(new BreakChallenge(new FuzzyItemStack(Set.of(SPONGE, WET_SPONGE), 1))),

	// Crafting
	CRAFT_16_FENCE_GATES(new CraftChallenge(new FuzzyItemStack(MaterialTag.FENCE_GATES, 16))),
	CRAFT_16_FENCES(new CraftChallenge(new FuzzyItemStack(MaterialTag.FENCES, 16))),
	CRAFT_IRON_ARMOR(new CraftChallenge(FuzzyItemStack.ofEach(MaterialTag.ARMOR_IRON, 1))),
	CRAFT_32_WALLS(new CraftChallenge(new FuzzyItemStack(MaterialTag.WALLS, 32))),
	CRAFT_32_POLISHED_BLACKSTONE_BRICKS(new CraftChallenge(new FuzzyItemStack(POLISHED_BLACKSTONE_BRICKS, 32))),

	// Obtaining
	OBTAIN_4_OBSIDIAN(new ObtainChallenge(new FuzzyItemStack(OBSIDIAN, 4))),
	OBTAIN_CROPS(new ObtainChallenge(FuzzyItemStack.ofEach(new MaterialTag(BEETROOT, CARROT, WHEAT, POTATO, APPLE), 1))),
	CATCH_8_FISH(new ObtainChallenge(new FuzzyItemStack(MaterialTag.RAW_FISH, 8))),
	// TODO prevent placing them back?
	CATCH_1_FISH_WITH_A_BUCKET(new ObtainChallenge(new FuzzyItemStack(MaterialTag.FISH_BUCKETS, 1))),
	CATCH_16_FISH_WITH_A_BUCKET(new ObtainChallenge(new FuzzyItemStack(MaterialTag.FISH_BUCKETS, 16))),
	OBTAIN_1_OF_EVERY_DYE(new ObtainChallenge(FuzzyItemStack.ofEach(MaterialTag.DYES, 1))),
	OBTAIN_1_NETHERITE_INGOT(new ObtainChallenge(new FuzzyItemStack(NETHERITE_INGOT, 1))),
	OBTAIN_1_TOTEM_OF_UNDYING(new ObtainChallenge(new FuzzyItemStack(TOTEM_OF_UNDYING, 1))),

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
	FIND_AN_ICE_SPIKES_BIOME(new BiomeChallenge(BiomeTag.ICE_SPIKES)),
	// TODO More

	// Dimensions
	ENTER_THE_NETHER(new DimensionChallenge(Environment.NETHER)),

	// Structures
	FIND_A_NETHER_FORTRESS(new StructureChallenge(StructureType.NETHER_FORTRESS)),
	FIND_A_BASTION(new StructureChallenge(StructureType.BASTION_REMNANT)),

	;

	private final IChallenge challenge;

	public static List<Challenge> shuffle() {
		ArrayList<Challenge> values = new ArrayList<>(Arrays.asList(Challenge.values()));
		Collections.shuffle(values);
		return values;
	}

	public ItemBuilder getDisplayItem() {
		return new ItemBuilder(challenge.getDisplayMaterial()).name(EnumUtils.prettyName(name())).lore("");
	}

}
