package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class MaterialTag implements Tag<Material> {
	public static final MaterialTag ALL_AIR = new MaterialTag("AIR", MatchMode.SUFFIX);
	public static final MaterialTag WOOL = new MaterialTag("_WOOL", MatchMode.SUFFIX);
	public static final MaterialTag WOOL_CARPET = new MaterialTag("_CARP", MatchMode.SUFFIX);
	public static final MaterialTag DYES = new MaterialTag("_DYE", MatchMode.SUFFIX);
	public static final MaterialTag BEDS = new MaterialTag("_BED", MatchMode.SUFFIX);
	public static final MaterialTag ALL_BANNERS = new MaterialTag(Tag.BANNERS);
	public static final MaterialTag STANDING_BANNERS = new MaterialTag(ALL_BANNERS).exclude("_WALL", MatchMode.CONTAINS);
	public static final MaterialTag WALL_BANNERS = new MaterialTag("_WALL_BANNER", MatchMode.SUFFIX);
	public static final MaterialTag STAINED_GLASS = new MaterialTag("_STAINED_GLASS", MatchMode.SUFFIX);
	public static final MaterialTag STAINED_GLASS_PANES = new MaterialTag("_STAINED_GLASS_PANE", MatchMode.SUFFIX);
	public static final MaterialTag ALL_STAINED_GLASS = new MaterialTag("STAINED_GLASS", MatchMode.CONTAINS);
	public static final MaterialTag GLAZED_TERRACOTTAS = new MaterialTag("_GLAZED_TERRACOTTA", MatchMode.SUFFIX);
	public static final MaterialTag COLORED_TERRACOTTAS = new MaterialTag("_TERRACOTTA", MatchMode.SUFFIX).exclude(GLAZED_TERRACOTTAS);
	public static final MaterialTag ALL_TERRACOTTAS = new MaterialTag("TERRACOTTA", MatchMode.CONTAINS);
	public static final MaterialTag CONCRETES = new MaterialTag("_CONCRETE", MatchMode.SUFFIX);
	public static final MaterialTag CONCRETE_POWDERS = new MaterialTag("_CONCRETE_POWDER", MatchMode.SUFFIX);
	public static final MaterialTag ALL_CONCRETES = new MaterialTag("CONCRETE", MatchMode.CONTAINS);
	public static final MaterialTag SANDSTONES = new MaterialTag("SANDSTONE", MatchMode.CONTAINS);
	public static final MaterialTag PRISMARINE = new MaterialTag("PRISMARINE", MatchMode.CONTAINS).exclude(Material.PRISMARINE_CRYSTALS, Material.PRISMARINE_SHARD);
	public static final MaterialTag PURPURS = new MaterialTag("PURPUR", MatchMode.CONTAINS);
	public static final MaterialTag END_STONES = new MaterialTag("END_STONE", MatchMode.CONTAINS);
	public static final MaterialTag SHULKER_BOXES = new MaterialTag("_SHULKER_BOX", MatchMode.SUFFIX).append(Material.SHULKER_BOX);
	public static final MaterialTag BOOKS = new MaterialTag("BOOK", MatchMode.CONTAINS);
	public static final MaterialTag ALL_QUARTZ = new MaterialTag("QUARTZ", MatchMode.CONTAINS).exclude(Material.QUARTZ, Material.NETHER_QUARTZ_ORE);
	public static final MaterialTag ALL_GLASS = new MaterialTag("GLASS", MatchMode.CONTAINS);

	public static final MaterialTag FROGLIGHT = new MaterialTag("_FROGLIGHT", MatchMode.SUFFIX);
	public static final MaterialTag TORCHES = new MaterialTag("TORCH", MatchMode.CONTAINS).exclude("FLOWER", MatchMode.CONTAINS);
	public static final MaterialTag LANTERNS = new MaterialTag("LANTERN", MatchMode.CONTAINS); // torch, soul torch, jack-o, & sea
	public static final MaterialTag CAMPFIRES = new MaterialTag("CAMPFIRE", MatchMode.CONTAINS);

	public static final MaterialTag DECORATIVE_LIGHT_SOURCES = new MaterialTag(Material.GLOWSTONE, Material.SHROOMLIGHT, Material.END_ROD, Material.SEA_PICKLE, Material.REDSTONE_LAMP, Material.LIGHT,
		Material.MAGMA_BLOCK, Material.CRYING_OBSIDIAN, Material.BEACON, Material.LAVA_BUCKET, Material.GLOW_LICHEN)
		.append(TORCHES, LANTERNS, CANDLES, FROGLIGHT, CAMPFIRES);

	public static final MaterialTag COLORABLE = new MaterialTag(WOOL, DYES, WOOL_CARPETS, BEDS, ALL_BANNERS,
		ALL_STAINED_GLASS, ALL_TERRACOTTAS, ALL_CONCRETES, SHULKER_BOXES);

	public static final MaterialTag FOODS = new MaterialTag(Material::isEdible);

	public static final MaterialTag SWORDS = new MaterialTag("_SWORD", MatchMode.SUFFIX);
	public static final MaterialTag PICKAXES = new MaterialTag("_PICKAXE", MatchMode.SUFFIX);
	public static final MaterialTag AXES = new MaterialTag("_AXE", MatchMode.SUFFIX);
	public static final MaterialTag SHOVELS = new MaterialTag("_SHOVEL", MatchMode.SUFFIX);
	public static final MaterialTag HOES = new MaterialTag("_HOE", MatchMode.SUFFIX);

	public static final MaterialTag TOOLS = new MaterialTag(PICKAXES, AXES, SHOVELS, HOES)
		.append(Material.FISHING_ROD, Material.LEAD, Material.SHEARS, Material.FLINT_AND_STEEL);

	public static final MaterialTag ARROWS = new MaterialTag("ARROW", MatchMode.SUFFIX);
	public static final MaterialTag WEAPONS = new MaterialTag("_SWORD", MatchMode.SUFFIX)
		.append(Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.MACE).append(ARROWS);

	public static final MaterialTag ALL_HELMETS = new MaterialTag("_HELMET", MatchMode.SUFFIX);
	public static final MaterialTag ALL_CHESTPLATES = new MaterialTag("_CHESTPLATE", MatchMode.SUFFIX);
	public static final MaterialTag ALL_LEGGINGS = new MaterialTag("_LEGGINGS", MatchMode.SUFFIX);
	public static final MaterialTag ALL_BOOTS = new MaterialTag("_BOOTS", MatchMode.SUFFIX);
	public static final MaterialTag ARMOR = new MaterialTag(ALL_HELMETS, ALL_CHESTPLATES, ALL_LEGGINGS, ALL_BOOTS);

	public static final MaterialTag TOOLS_WEAPONS_ARMOR = new MaterialTag(TOOLS, WEAPONS, ARMOR);

	public static final MaterialTag ARMOR_NETHERITE = new MaterialTag("NETHERITE_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_DIAMOND = new MaterialTag("DIAMOND_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_IRON = new MaterialTag("IRON_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_GOLD = new MaterialTag("GOLDEN_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_CHAINMAIL = new MaterialTag("CHAINMAIL_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_LEATHER = new MaterialTag("LEATHER_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_TRIM = new MaterialTag("ARMOR_TRIM", MatchMode.CONTAINS);

	public static final MaterialTag TOOLS_NETHERITE = new MaterialTag("NETHERITE_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_DIAMOND = new MaterialTag("DIAMOND_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_IRON = new MaterialTag("IRON_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_GOLD = new MaterialTag("GOLDEN_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_CHAINMAIL = new MaterialTag("CHAINMAIL_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_STONE = new MaterialTag("STONE_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_WOODEN = new MaterialTag("WOODEN_", MatchMode.PREFIX, MaterialTag.TOOLS);

	public static final MaterialTag CHESTS = new MaterialTag(Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL);
	public static final MaterialTag FURNACES = new MaterialTag(Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER);
	public static final MaterialTag INVENTORY_BLOCKS = new MaterialTag(Material.HOPPER, Material.DISPENSER, Material.DROPPER)
		.append(CHESTS)
		.append(FURNACES)
		.append(SHULKER_BOXES)
		.append(Material.LECTERN);
	public static final MaterialTag MENU_BLOCKS = new MaterialTag(Material.CRAFTING_TABLE, Material.GRINDSTONE,
		Material.ENCHANTING_TABLE, Material.STONECUTTER, Material.CARTOGRAPHY_TABLE, Material.LOOM, Material.BELL,
		Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.BEACON);

	public static final MaterialTag COMMAND_BLOCKS = new MaterialTag("COMMAND_BLOCK", MatchMode.CONTAINS);
	public static final MaterialTag MINECARTS = new MaterialTag("_MINECART", MatchMode.SUFFIX).append(Material.MINECART);

	public static final MaterialTag UNOBTAINABLE = new MaterialTag(Material.WATER, Material.LAVA, Material.AIR,
		Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.JIGSAW, Material.BARRIER, Material.BEDROCK,
		Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
		Material.END_PORTAL, Material.END_PORTAL_FRAME, Material.NETHER_PORTAL, Material.KNOWLEDGE_BOOK,
		Material.DEBUG_STICK, Material.SPAWNER, Material.CHORUS_PLANT);

	public static final MaterialTag UNSTACKABLE = new MaterialTag(material -> material.getMaxStackSize() == 1);

	public static final MaterialTag DYEABLE = new MaterialTag(ARMOR_LEATHER).append(Material.LEATHER_HORSE_ARMOR);

	public static final MaterialTag POTIONS = new MaterialTag(Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION);

	public static final MaterialTag POTION_MATERIALS = new MaterialTag(POTIONS)
		.append(Material.GLASS_BOTTLE, Material.FERMENTED_SPIDER_EYE, Material.BLAZE_POWDER, Material.MAGMA_CREAM, Material.GLISTERING_MELON_SLICE, Material.GOLDEN_CARROT,
			Material.RABBIT_FOOT, Material.DRAGON_BREATH, Material.PHANTOM_MEMBRANE, Material.GHAST_TEAR, Material.BREWING_STAND, Material.CAULDRON, Material.CARROT, Material.SLIME_BALL,
			Material.QUARTZ, Material.RED_MUSHROOM, Material.APPLE, Material.ROTTEN_FLESH, Material.BROWN_MUSHROOM, Material.INK_SAC, Material.FERN, Material.POISONOUS_POTATO, Material.GOLDEN_APPLE);

	public static final MaterialTag REQUIRES_META = new MaterialTag(POTIONS).append(Material.TIPPED_ARROW, Material.WRITTEN_BOOK, Material.ENCHANTED_BOOK);

	public static final MaterialTag CORAL_BLOCKS = new MaterialTag("CORAL_BLOCK", MatchMode.CONTAINS);
	public static final MaterialTag CORAL_WALL_FANS = new MaterialTag("_WALL_FAN", MatchMode.SUFFIX);
	public static final MaterialTag ALL_CORALS = new MaterialTag("CORAL", MatchMode.CONTAINS);

	public static final MaterialTag SEEDS = new MaterialTag(Material.COCOA_BEANS, Material.WHEAT_SEEDS, Material.POTATO, Material.CARROT, Material.BEETROOT_SEEDS, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS);
	public static final MaterialTag PLANTS = new MaterialTag(Material.SHORT_GRASS, Material.FERN, Material.TALL_GRASS, Material.LARGE_FERN, Material.DEAD_BUSH, Material.SWEET_BERRY_BUSH,
		Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.LILY_PAD, Material.BAMBOO_SAPLING, Material.BAMBOO, Material.SEAGRASS, Material.TALL_SEAGRASS, Material.KELP, Material.KELP_PLANT, Material.SUGAR_CANE,
		Material.CACTUS, Material.SEA_PICKLE, Material.CHORUS_PLANT, Material.CHORUS_FLOWER, Material.WEEPING_VINES, Material.TWISTING_VINES, Material.NETHER_SPROUTS, Material.WARPED_ROOTS, Material.CRIMSON_ROOTS,
		Material.WARPED_FUNGUS, Material.CRIMSON_FUNGUS, Material.VINE, Material.LEAF_LITTER)
		.append(ALL_CORALS, FLOWERS);

	public static final MaterialTag MUSHROOMS = new MaterialTag(Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS);

	public static final MaterialTag TULIPS = new MaterialTag("_TULIP", MatchMode.SUFFIX).exclude("POTTED", MatchMode.CONTAINS);

	public static final MaterialTag ALL_FLOWERS = new MaterialTag(SMALL_FLOWERS, FLOWERS);

	public static final MaterialTag ALL_BEEHIVES = new MaterialTag(Material.BEEHIVE, Material.BEE_NEST);

	public static final MaterialTag ALL_PUMPKINS = new MaterialTag(Material.PUMPKIN, Material.CARVED_PUMPKIN, Material.JACK_O_LANTERN);

	public static final MaterialTag ALL_DEEPSLATE = new MaterialTag("DEEPSLATE", MatchMode.CONTAINS);

	// TODO: Include Blackstone?
	public static final MaterialTag ALL_STONE = new MaterialTag(Material.STONE, Material.STONE_STAIRS, Material.STONE_SLAB, Material.STONE_BRICKS, Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB)
		.append(ALL_DEEPSLATE).exclude(new MaterialTag("WALL", MatchMode.CONTAINS, ALL_DEEPSLATE))
		.append(new MaterialTag("STONE_BRICK", MatchMode.PREFIX))
		.append(new MaterialTag("_STONE_BRICKS", MatchMode.SUFFIX)).exclude(Material.END_STONE_BRICKS)
		.append(new MaterialTag("COBBLESTONE", MatchMode.CONTAINS))
		.append(new MaterialTag("GRANITE", MatchMode.CONTAINS))
		.append(new MaterialTag("DIORITE", MatchMode.CONTAINS))
		.append(new MaterialTag("ANDESITE", MatchMode.CONTAINS));

	public static final MaterialTag INFESTED_STONE = new MaterialTag("INFESTED_", MatchMode.PREFIX);

	public static final MaterialTag ALL_COPPER = new MaterialTag("COPPER", MatchMode.CONTAINS);

	public static final MaterialTag MINERAL_ORES = new MaterialTag("_ORE", MatchMode.CONTAINS);
	public static final MaterialTag MINERAL_RAW = new MaterialTag(Material.RAW_COPPER, Material.RAW_GOLD, Material.RAW_IRON);
	public static final MaterialTag MINERAL_NUGGETS = new MaterialTag(Material.GOLD_NUGGET, Material.IRON_NUGGET, Material.NETHERITE_SCRAP, Material.ANCIENT_DEBRIS);
	public static final MaterialTag MINERAL_INGOTS = new MaterialTag(Material.COAL, Material.CHARCOAL, Material.LAPIS_LAZULI, Material.REDSTONE, Material.QUARTZ,
		Material.COPPER_INGOT, Material.GOLD_INGOT, Material.IRON_INGOT, Material.DIAMOND, Material.EMERALD, Material.NETHERITE_INGOT);
	public static final MaterialTag MINERAL_RAW_BLOCKS = new MaterialTag(Material.RAW_COPPER_BLOCK, Material.RAW_GOLD_BLOCK, Material.RAW_IRON_BLOCK);
	public static final MaterialTag MINERAL_BLOCKS = new MaterialTag(Material.COAL_BLOCK, Material.LAPIS_BLOCK, Material.REDSTONE_BLOCK, Material.COPPER_BLOCK, Material.GOLD_BLOCK,
		Material.IRON_BLOCK, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.NETHERITE_BLOCK)
		.append(ALL_QUARTZ);
	public static final MaterialTag ALL_MINERALS = new MaterialTag(MINERAL_ORES, MINERAL_RAW, MINERAL_NUGGETS, MINERAL_INGOTS, MINERAL_RAW_BLOCKS, MINERAL_BLOCKS);

	public static final MaterialTag MUDABLE_DIRT = new MaterialTag(Material.DIRT, Material.COARSE_DIRT, Material.ROOTED_DIRT);
	public static final MaterialTag ALL_DIRT = new MaterialTag(Tag.DIRT).append(Material.DIRT_PATH, Material.FARMLAND);
	public static final MaterialTag ALL_SAND = new MaterialTag(Material.SAND, Material.SUSPICIOUS_SAND, Material.RED_SAND);
	public static final MaterialTag NATURAL_GRAVITY_SEDIMENT = new MaterialTag(ALL_SAND).append(Material.GRAVEL, Material.SUSPICIOUS_GRAVEL);

	public static final MaterialTag VILLAGER_WORKBLOCKS = new MaterialTag(Material.BLAST_FURNACE, Material.SMOKER,
		Material.CARTOGRAPHY_TABLE, Material.BREWING_STAND, Material.COMPOSTER, Material.BARREL, Material.FLETCHING_TABLE,
		Material.CAULDRON, Material.LECTERN, Material.STONECUTTER, Material.LOOM, Material.SMITHING_TABLE, Material.GRINDSTONE);

	public static final MaterialTag TREE_LOGS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getLog).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag STRIPPED_LOGS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getStrippedLog).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag TREE_WOOD = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getWood).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag STRIPPED_WOOD = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getStrippedWood).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_STAIRS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getStair).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_SLABS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getSlab).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_BUTTONS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getButton).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_DOORS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getDoor).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_TRAPDOORS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getTrapDoor).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_FENCES = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getFence).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_FENCE_GATES = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getFenceGate).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_PRESSURE_PLATES = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getPressurePlate).filter(Nullables::isNotNullOrAir).forEach(this::append); }};
	public static final MaterialTag WOOD_SIGNS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getSign).filter(Nullables::isNotNullOrAir).forEach(this::append); }};

	public static final MaterialTag LOGS = new MaterialTag(TREE_LOGS, STRIPPED_LOGS);
	public static final MaterialTag WOOD = new MaterialTag(TREE_WOOD, STRIPPED_WOOD);

	public static final MaterialTag ALL_WOOD = new MaterialTag(LOGS, WOOD, PLANKS, WOOD_STAIRS, WOOD_SLABS, WOOD_BUTTONS,
		WOOD_DOORS, WOOD_TRAPDOORS, WOOD_FENCES, WOOD_FENCE_GATES, WOOD_PRESSURE_PLATES, WOOD_SIGNS);

	public static final MaterialTag ALL_SIGNS = new MaterialTag(Tag.ALL_SIGNS);

	public static final MaterialTag ALL_NETHER = new MaterialTag(Material.GLOWSTONE, Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.GRAVEL, Material.MAGMA_BLOCK, Material.MAGMA_CREAM,
		Material.WITHER_SKELETON_SKULL, Material.BLAZE_ROD, Material.BLAZE_POWDER, Material.WEEPING_VINES, Material.TWISTING_VINES, Material.MUSIC_DISC_PIGSTEP)
		.append("NETHER", MatchMode.CONTAINS)
		.append("NYLIUM", MatchMode.CONTAINS)
		.append("WARPED", MatchMode.CONTAINS)
		.append("CRIMSON", MatchMode.CONTAINS)
		.append("BLACKSTONE", MatchMode.CONTAINS)
		.append("SOUL", MatchMode.CONTAINS)
		.append(Material.GOLDEN_SWORD, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS)
		.append(Material.NETHER_QUARTZ_ORE, Material.QUARTZ, Material.NETHER_GOLD_ORE, Material.GOLD_NUGGET, Material.GOLD_INGOT, Material.GOLD_BLOCK).append(ALL_QUARTZ);

	public static final MaterialTag ALL_END = new MaterialTag("END", MatchMode.CONTAINS)
		.append("PURPUR", MatchMode.CONTAINS)
		.append("CHORUS", MatchMode.CONTAINS)
		.append(SHULKER_BOXES)
		.append(Material.SHULKER_SHELL, Material.ELYTRA, Material.DRAGON_EGG, Material.DRAGON_HEAD, Material.OBSIDIAN, Material.MAGENTA_STAINED_GLASS);

	public static final MaterialTag RAW_FISH = new MaterialTag(Material.SALMON, Material.COD, Material.TROPICAL_FISH, Material.PUFFERFISH);
	public static final MaterialTag COOKED_FISH = new MaterialTag(Material.COOKED_SALMON, Material.COOKED_COD);

	public static final MaterialTag ALL_FISH = new MaterialTag(RAW_FISH, COOKED_FISH);

	public static final MaterialTag FISH_BUCKETS = new MaterialTag(Material.TROPICAL_FISH_BUCKET, Material.PUFFERFISH_BUCKET, Material.COD_BUCKET, Material.SALMON_BUCKET);

	public static final MaterialTag ALL_OCEAN = new MaterialTag(Material.SOUL_SAND, Material.MAGMA_BLOCK, Material.TURTLE_EGG, Material.CONDUIT, Material.TURTLE_SCUTE,
		Material.PUFFERFISH_BUCKET, Material.SALMON_BUCKET, Material.COD_BUCKET, Material.TROPICAL_FISH_BUCKET, Material.NAUTILUS_SHELL, Material.COD, Material.SALMON, Material.PUFFERFISH,
		Material.TROPICAL_FISH, Material.COOKED_COD, Material.COOKED_SALMON, Material.LILY_PAD, Material.TURTLE_HELMET, Material.FISHING_ROD, Material.INK_SAC, Material.GRAVEL, Material.SAND)
		.append("PRISMARINE", MatchMode.CONTAINS)
		.append("KELP", MatchMode.CONTAINS)
		.append("SEA", MatchMode.CONTAINS)
		.append("BOAT", MatchMode.CONTAINS)
		.append(ALL_CORALS, ALL_FISH);

	public static final MaterialTag MUSIC = new MaterialTag("DISC", MatchMode.CONTAINS).append(Material.NOTE_BLOCK, Material.JUKEBOX, Material.BELL);
	public static final MaterialTag ITEMS_MUSIC_DISCS = new MaterialTag("DISC", MatchMode.CONTAINS);

	public static final MaterialTag BLOCKS = new MaterialTag(Material::isSolid)
		.exclude(Material.CACTUS, Material.BAMBOO, Material.DRAGON_EGG, Material.TURTLE_EGG, Material.CONDUIT, Material.CAKE)
		.exclude(SIGNS, ALL_BANNERS, ALL_CORALS)
		.append(CORAL_BLOCKS);

	public static final MaterialTag ITEMS = new MaterialTag(Material::isItem);

	public static final MaterialTag SKULLS = new MaterialTag("_SKULL", MatchMode.SUFFIX).append("_HEAD", MatchMode.SUFFIX).exclude(Material.PISTON_HEAD);
	public static final MaterialTag PLAYER_SKULLS = new MaterialTag(Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD);
	public static final MaterialTag MOB_SKULLS = new MaterialTag(SKULLS).exclude(PLAYER_SKULLS);
	public static final MaterialTag FLOOR_SKULLS = new MaterialTag(SKULLS).exclude("WALL", MatchMode.CONTAINS);

	public static final MaterialTag BOATS = new MaterialTag(Tag.ITEMS_BOATS);
	public static final MaterialTag ALL_SAPLINGS = new MaterialTag(Tag.SAPLINGS).append(Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS);
	public static final MaterialTag SPAWN_EGGS = new MaterialTag("_SPAWN_EGG", MatchMode.SUFFIX);
	public static final MaterialTag PORTALS = new MaterialTag(Material.END_PORTAL, Material.NETHER_PORTAL);
	public static final MaterialTag LIQUIDS = new MaterialTag(Material.WATER, Material.LAVA);
	public static final MaterialTag CONTAINERS = new MaterialTag(Material.ENDER_CHEST, Material.ANVIL, Material.BREWING_STAND).append(INVENTORY_BLOCKS);
	public static final MaterialTag PRESSURE_PLATES = new MaterialTag("_PRESSURE_PLATE", MatchMode.SUFFIX);

	public static final MaterialTag FLORA = new MaterialTag(ALL_SAPLINGS, FLOWERS, PLANTS, SEEDS, LEAVES);
	public static final MaterialTag REDSTONE_ACTIVATORS = new MaterialTag(BUTTONS, PRESSURE_PLATES).append(Material.LEVER);
	public static final MaterialTag SUSPICIOUS_BLOCKS = new MaterialTag(Material.SUSPICIOUS_SAND, Material.SUSPICIOUS_GRAVEL);
	public static final MaterialTag POTTERY_SHERDS = new MaterialTag("_POTTERY_SHERD", MatchMode.SUFFIX);

	public static final MaterialTag REDSTONE_BLOCKS = new MaterialTag(REDSTONE_ACTIVATORS, DOORS, COMMAND_BLOCKS)
		.append(Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.STICKY_PISTON, Material.PISTON, Material.REDSTONE_TORCH, Material.REDSTONE_WALL_TORCH, Material.REDSTONE_WIRE,
			Material.TNT, Material.DISPENSER, Material.NOTE_BLOCK, Material.REPEATER, Material.TRIPWIRE_HOOK, Material.TRAPPED_CHEST, Material.COMPARATOR, Material.REDSTONE_BLOCK, Material.HOPPER,
			Material.ACTIVATOR_RAIL, Material.DROPPER, Material.DAYLIGHT_DETECTOR);


	public static final MaterialTag NEEDS_SUPPORT = new MaterialTag(Material.GRAVEL, Material.VINE, Material.LILY_PAD, Material.TURTLE_EGG,
		Material.REPEATER, Material.COMPARATOR, Material.ITEM_FRAME, Material.BELL, Material.SNOW, Material.SCAFFOLDING, Material.TRIPWIRE_HOOK, Material.LADDER, Material.LEVER, Material.LANTERN, Material.SOUL_LANTERN)
		.append(ALL_SAPLINGS, DOORS, SIGNS, RAILS, ALL_BANNERS, CONCRETE_POWDERS, SAND, CORALS, WOOL_CARPETS,
			PRESSURE_PLATES, BUTTONS, FLOWER_POTS, ANVIL, PLANTS, TORCHES, CANDLES);

	public static final MaterialTag SPAWNS_ENTITY = new MaterialTag(SPAWN_EGGS, BOATS, MINECARTS).append(Material.EGG, Material.SNOWBALL, Material.BOW, Material.CROSSBOW,
		Material.TRIDENT, Material.FIREWORK_ROCKET, Material.ENDER_PEARL, Material.ENDER_EYE, Material.SPLASH_POTION, Material.LINGERING_POTION, Material.EXPERIENCE_BOTTLE, Material.ARMOR_STAND, Material.ITEM_FRAME,
		Material.GLOW_ITEM_FRAME, Material.PAINTING);

	public static final MaterialTag VEHICLES = new MaterialTag(BOATS, ITEMS_CHEST_BOATS, MINECARTS);

	public static final MaterialTag WEARABLE = new MaterialTag(ARMOR, SKULLS).append(Material.CARVED_PUMPKIN).exclude("_WALL_", MatchMode.CONTAINS);

	public static final MaterialTag INTERACTABLES = new MaterialTag(BEDS, SHULKER_BOXES, CONTAINERS, WOOD_FENCE_GATES,
			DOORS, TRAPDOORS, BUTTONS, MENU_BLOCKS, CAULDRONS).append(
		Material.REPEATER, Material.COMPARATOR, Material.NOTE_BLOCK, Material.JUKEBOX, Material.DAYLIGHT_DETECTOR, Material.LEVER, Material.REDSTONE_WIRE, Material.CHISELED_BOOKSHELF,
		Material.COMPOSTER, Material.DECORATED_POT);

	public static final MaterialTag BUNDLES = new MaterialTag("BUNDLE", MatchMode.CONTAINS);

	public static final MaterialTag BACKPACK_DENY = new MaterialTag(SHULKER_BOXES, BUNDLES);

	public static final MaterialTag REPLACEABLE_FIXED = new MaterialTag(REPLACEABLE)
		.append(Material.SCULK_VEIN, Material.PALE_HANGING_MOSS);

	@SneakyThrows
	public static Map<String, Tag<Material>> getApplicable(Material material) {
		return Utils.collect(tags.entrySet().stream().filter(entry -> entry.getValue().isTagged(material)));
	}

	@Getter
	private static final Map<String, Tag<Material>> tags = new HashMap<>() {{
		List<Field> fields = new ArrayList<>() {{
			addAll(Arrays.asList(MaterialTag.class.getFields()));
			addAll(Arrays.asList(Tag.class.getFields()));
		}};

		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (field.getType() == Tag.class || field.getType() == MaterialTag.class) {
					Tag<Material> materialTag = (Tag<Material>) field.get(null);

					if (materialTag == null)
						continue;

					try {
						Method isTaggedMethod = materialTag.getClass().getMethod("isTagged", Material.class);
						put(field.getName(), materialTag);
					} catch (NoSuchMethodException ignore) {
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}};

	static {
		for (Field field : MaterialTag.class.getFields()) {
			try {
				field.setAccessible(true);
				if (field.getType() == MaterialTag.class) {
					MaterialTag materialTag = (MaterialTag) field.get(null);

					try {
						Method isTaggedMethod = materialTag.getClass().getMethod("isTagged", Material.class);
						materialTag.key = new NamespacedKey(Nexus.getInstance(), field.getName());
					} catch (NoSuchMethodException ignore) {
					}

					materialTag.lock();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private final EnumSet<Material> materials;
	private NamespacedKey key = null;

	public MaterialTag() {
		this.materials = EnumSet.noneOf(Material.class);
	}

	public MaterialTag(NamespacedKey key) {
		this.materials = EnumSet.noneOf(Material.class);
		this.key = key;
	}

	public MaterialTag(EnumSet<Material> materials) {
		this.materials = materials.clone();
	}

	@SafeVarargs
	public MaterialTag(Tag<Material>... materialTags) {
		this.materials = EnumSet.noneOf(Material.class);
		append(materialTags);
	}

	public MaterialTag(Material... materials) {
		this.materials = EnumSet.noneOf(Material.class);
		append(materials);
	}

	public MaterialTag(String segment, MatchMode mode) {
		this.materials = EnumSet.noneOf(Material.class);
		append(segment, mode);
	}

	public MaterialTag(String segment, MatchMode mode, MaterialTag materials) {
		this.materials = EnumSet.noneOf(Material.class);
		append(segment, mode, materials.getValues().toArray(Material[]::new));
	}

	public MaterialTag(Predicate<Material> predicate) {
		this.materials = EnumSet.noneOf(Material.class);
		append(predicate);
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	public MaterialTag append(Material... materials) {
		return edit(values -> values.addAll(Arrays.asList(materials)));
	}

	@SafeVarargs
	public final MaterialTag append(Tag<Material>... materialTags) {
		return edit(values -> {
			for (Tag<Material> materialTag : materialTags)
				values.addAll(materialTag.getValues());
		});
	}

	public MaterialTag append(Predicate<Material> predicate) {
		return edit(values -> {
			for (Material material : Material.values())
				if (predicate.test(material))
					values.add(material);
		});
	}

	public MaterialTag append(String segment, MatchMode mode) {
		append(segment, mode, Material.values());
		return this;
	}

	public MaterialTag append(String segment, MatchMode mode, Material[] materials) {
		edit(values -> {
			switch (mode) {
				case PREFIX:
					for (Material material : materials)
						if (material.name().startsWith(segment.toUpperCase()))
							values.add(material);
					break;

				case SUFFIX:
					for (Material material : materials)
						if (material.name().endsWith(segment.toUpperCase()))
							values.add(material);
					break;

				case CONTAINS:
					for (Material material : materials)
						if (material.name().contains(segment.toUpperCase()))
							values.add(material);
					break;
			};
		});

		return this;
	}

	public MaterialTag exclude(Material... materials) {
		return edit(values -> {
			for (Material material : materials)
				values.remove(material);
		});
	}

	@SafeVarargs
	public final MaterialTag exclude(Tag<Material>... materialTags) {
		return edit(values -> {
			for (Tag<Material> materialTag : materialTags)
				values.removeAll(materialTag.getValues());
		});
	}

	public MaterialTag exclude(Predicate<Material> predicate) {
		return edit(values -> values.removeIf(predicate));
	}

	private MaterialTag edit(Consumer<EnumSet<Material>> consumer) {
		EnumSet<Material> materials = getValues();
		consumer.accept(materials);
		return locked ? new MaterialTag(materials) : this;
	}

	public MaterialTag exclude(String segment, MatchMode mode) {
		exclude(segment, mode, Material.values());
		return this;
	}

	public MaterialTag exclude(String segment, MatchMode mode, Material[] materials) {
		return edit(values -> {
			switch (mode) {
				case PREFIX:
					for (Material material : materials)
						if (material.name().startsWith(segment.toUpperCase()))
							values.remove(material);
					break;

				case SUFFIX:
					for (Material material : materials)
						if (material.name().endsWith(segment.toUpperCase()))
							values.remove(material);
					break;

				case CONTAINS:
					for (Material material : materials)
						if (material.name().contains(segment.toUpperCase()))
							values.remove(material);
					break;
			}
		});
	}

	@Override
	public EnumSet<Material> getValues() {
		return locked ? EnumSet.copyOf(materials) : materials;
	}

	public Material first() {
		return materials.iterator().next();
	}

	public Material[] toArray() {
		return new ArrayList<>(materials).toArray(Material[]::new);
	}

	@Override
	public boolean isTagged(@NotNull Material material) {
		return materials.contains(material);
	}

	@Contract("null -> false")
	public boolean isTagged(@Nullable ItemStack item) {
		return item != null && isTagged(item.getType());
	}

	public boolean isTagged(@NotNull Block block) {
		return isTagged(block.getType());
	}

	public boolean isTagged(@NotNull BlockData block) {
		return isTagged(block.getMaterial());
	}

	public boolean isNotTagged(@NotNull Material material) {
		return !isTagged(material);
	}

	public boolean isNotTagged(@Nullable ItemStack item) {
		return !isTagged(item);
	}

	public boolean isNotTagged(@NotNull Block block) {
		return !isTagged(block);
	}

	public boolean isNotTagged(@NotNull BlockData block) {
		return !isTagged(block);
	}

	@Override
	public String toString() {
		return materials.toString();
	}

	public Material random() {
		return RandomUtils.randomMaterial(this);
	}

	public enum MatchMode {
		PREFIX,
		SUFFIX,
		CONTAINS
	}

	private boolean locked;

	private void lock() {
		this.locked = true;
	}

}
