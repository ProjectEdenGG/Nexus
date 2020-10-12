package me.pugabyte.bncore.utils;

import static org.bukkit.Material.ACACIA_LOG;
import static org.bukkit.Material.ACACIA_SLAB;
import static org.bukkit.Material.ACACIA_STAIRS;
import static org.bukkit.Material.ACACIA_WOOD;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.BAMBOO;
import static org.bukkit.Material.BAMBOO_SAPLING;
import static org.bukkit.Material.BARREL;
import static org.bukkit.Material.BARRIER;
import static org.bukkit.Material.BEDROCK;
import static org.bukkit.Material.BELL;
import static org.bukkit.Material.BIRCH_LOG;
import static org.bukkit.Material.BIRCH_SLAB;
import static org.bukkit.Material.BIRCH_STAIRS;
import static org.bukkit.Material.BIRCH_WOOD;
import static org.bukkit.Material.BLAST_FURNACE;
import static org.bukkit.Material.BOW;
import static org.bukkit.Material.BREWING_STAND;
import static org.bukkit.Material.BROWN_MUSHROOM;
import static org.bukkit.Material.CACTUS;
import static org.bukkit.Material.CARTOGRAPHY_TABLE;
import static org.bukkit.Material.CAULDRON;
import static org.bukkit.Material.CHAIN_COMMAND_BLOCK;
import static org.bukkit.Material.CHEST;
import static org.bukkit.Material.CHORUS_FLOWER;
import static org.bukkit.Material.CHORUS_PLANT;
import static org.bukkit.Material.COARSE_DIRT;
import static org.bukkit.Material.COMMAND_BLOCK;
import static org.bukkit.Material.COMMAND_BLOCK_MINECART;
import static org.bukkit.Material.COMPARATOR;
import static org.bukkit.Material.COMPOSTER;
import static org.bukkit.Material.CROSSBOW;
import static org.bukkit.Material.DARK_OAK_LOG;
import static org.bukkit.Material.DARK_OAK_SLAB;
import static org.bukkit.Material.DARK_OAK_STAIRS;
import static org.bukkit.Material.DARK_OAK_WOOD;
import static org.bukkit.Material.DEAD_BUSH;
import static org.bukkit.Material.DEBUG_STICK;
import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.DISPENSER;
import static org.bukkit.Material.DROPPER;
import static org.bukkit.Material.ENCHANTED_BOOK;
import static org.bukkit.Material.ENDER_CHEST;
import static org.bukkit.Material.END_PORTAL;
import static org.bukkit.Material.END_PORTAL_FRAME;
import static org.bukkit.Material.END_STONE_BRICKS;
import static org.bukkit.Material.FARMLAND;
import static org.bukkit.Material.FERN;
import static org.bukkit.Material.FISHING_ROD;
import static org.bukkit.Material.FLETCHING_TABLE;
import static org.bukkit.Material.FLINT_AND_STEEL;
import static org.bukkit.Material.FURNACE;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.GRASS_BLOCK;
import static org.bukkit.Material.GRAVEL;
import static org.bukkit.Material.GRINDSTONE;
import static org.bukkit.Material.HOPPER;
import static org.bukkit.Material.ITEM_FRAME;
import static org.bukkit.Material.JIGSAW;
import static org.bukkit.Material.JUNGLE_LOG;
import static org.bukkit.Material.JUNGLE_SLAB;
import static org.bukkit.Material.JUNGLE_STAIRS;
import static org.bukkit.Material.JUNGLE_WOOD;
import static org.bukkit.Material.KELP;
import static org.bukkit.Material.KELP_PLANT;
import static org.bukkit.Material.KNOWLEDGE_BOOK;
import static org.bukkit.Material.LADDER;
import static org.bukkit.Material.LARGE_FERN;
import static org.bukkit.Material.LAVA;
import static org.bukkit.Material.LEAD;
import static org.bukkit.Material.LECTERN;
import static org.bukkit.Material.LEVER;
import static org.bukkit.Material.LILY_PAD;
import static org.bukkit.Material.LINGERING_POTION;
import static org.bukkit.Material.LOOM;
import static org.bukkit.Material.NETHER_PORTAL;
import static org.bukkit.Material.OAK_LOG;
import static org.bukkit.Material.OAK_SLAB;
import static org.bukkit.Material.OAK_STAIRS;
import static org.bukkit.Material.OAK_WOOD;
import static org.bukkit.Material.PETRIFIED_OAK_SLAB;
import static org.bukkit.Material.PISTON_HEAD;
import static org.bukkit.Material.PLAYER_HEAD;
import static org.bukkit.Material.PLAYER_WALL_HEAD;
import static org.bukkit.Material.PODZOL;
import static org.bukkit.Material.POTION;
import static org.bukkit.Material.REDSTONE_TORCH;
import static org.bukkit.Material.REDSTONE_WALL_TORCH;
import static org.bukkit.Material.RED_MUSHROOM;
import static org.bukkit.Material.RED_SAND;
import static org.bukkit.Material.REPEATER;
import static org.bukkit.Material.REPEATING_COMMAND_BLOCK;
import static org.bukkit.Material.SCAFFOLDING;
import static org.bukkit.Material.SEAGRASS;
import static org.bukkit.Material.SEA_PICKLE;
import static org.bukkit.Material.SHEARS;
import static org.bukkit.Material.SHULKER_BOX;
import static org.bukkit.Material.SMITHING_TABLE;
import static org.bukkit.Material.SMOKER;
import static org.bukkit.Material.SMOOTH_STONE;
import static org.bukkit.Material.SMOOTH_STONE_SLAB;
import static org.bukkit.Material.SNOW;
import static org.bukkit.Material.SPAWNER;
import static org.bukkit.Material.SPLASH_POTION;
import static org.bukkit.Material.SPRUCE_LOG;
import static org.bukkit.Material.SPRUCE_SLAB;
import static org.bukkit.Material.SPRUCE_STAIRS;
import static org.bukkit.Material.SPRUCE_WOOD;
import static org.bukkit.Material.STONE;
import static org.bukkit.Material.STONECUTTER;
import static org.bukkit.Material.STONE_SLAB;
import static org.bukkit.Material.STONE_STAIRS;
import static org.bukkit.Material.STRIPPED_ACACIA_LOG;
import static org.bukkit.Material.STRIPPED_ACACIA_WOOD;
import static org.bukkit.Material.STRIPPED_BIRCH_LOG;
import static org.bukkit.Material.STRIPPED_BIRCH_WOOD;
import static org.bukkit.Material.STRIPPED_DARK_OAK_LOG;
import static org.bukkit.Material.STRIPPED_DARK_OAK_WOOD;
import static org.bukkit.Material.STRIPPED_JUNGLE_LOG;
import static org.bukkit.Material.STRIPPED_JUNGLE_WOOD;
import static org.bukkit.Material.STRIPPED_OAK_LOG;
import static org.bukkit.Material.STRIPPED_OAK_WOOD;
import static org.bukkit.Material.STRIPPED_SPRUCE_LOG;
import static org.bukkit.Material.STRIPPED_SPRUCE_WOOD;
import static org.bukkit.Material.STRUCTURE_BLOCK;
import static org.bukkit.Material.STRUCTURE_VOID;
import static org.bukkit.Material.SUGAR_CANE;
import static org.bukkit.Material.SWEET_BERRY_BUSH;
import static org.bukkit.Material.TALL_GRASS;
import static org.bukkit.Material.TALL_SEAGRASS;
import static org.bukkit.Material.TIPPED_ARROW;
import static org.bukkit.Material.TORCH;
import static org.bukkit.Material.TRAPPED_CHEST;
import static org.bukkit.Material.TRIDENT;
import static org.bukkit.Material.TRIPWIRE_HOOK;
import static org.bukkit.Material.TURTLE_EGG;
import static org.bukkit.Material.VINE;
import static org.bukkit.Material.WALL_TORCH;
import static org.bukkit.Material.WATER;
import static org.bukkit.Material.WRITTEN_BOOK;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

@SuppressWarnings("unused")
public class MaterialTag implements Tag<Material> {
	public static final MaterialTag WOOL = new MaterialTag("_WOOL", MatchMode.SUFFIX);
	public static final MaterialTag DYES = new MaterialTag("_DYE", MatchMode.SUFFIX);
	public static final MaterialTag BEDS = new MaterialTag("_BED", MatchMode.SUFFIX);
	public static final MaterialTag ALL_BANNERS = new MaterialTag(Tag.BANNERS);
	public static final MaterialTag BANNERS = new MaterialTag("_BANNER", MatchMode.SUFFIX);
	public static final MaterialTag WALL_BANNERS = new MaterialTag("_WALL_BANNER", MatchMode.SUFFIX);
	public static final MaterialTag STAINED_GLASS = new MaterialTag("_STAINED_GLASS", MatchMode.SUFFIX);
	public static final MaterialTag STAINED_GLASS_PANES = new MaterialTag("_STAINED_GLASS_PANE", MatchMode.SUFFIX);
	public static final MaterialTag TERRACOTTAS = new MaterialTag("_TERRACOTTA", MatchMode.SUFFIX);
	public static final MaterialTag GLAZED_TERRACOTTAS = new MaterialTag("_GLAZED_TERRACOTTA", MatchMode.SUFFIX);
	public static final MaterialTag ALL_CONCRETES = new MaterialTag("CONCRETE", MatchMode.CONTAINS);
	public static final MaterialTag CONCRETES = new MaterialTag("_CONCRETE", MatchMode.SUFFIX);
	public static final MaterialTag CONCRETE_POWDERS = new MaterialTag("_CONCRETE_POWDER", MatchMode.SUFFIX);
	public static final MaterialTag SHULKER_BOXES = new MaterialTag("_SHULKER_BOX", MatchMode.SUFFIX).append(SHULKER_BOX);

	public static final MaterialTag COLORABLE = new MaterialTag(WOOL, DYES, CARPETS, BEDS, BANNERS, WALL_BANNERS, STAINED_GLASS,
			STAINED_GLASS_PANES, TERRACOTTAS, GLAZED_TERRACOTTAS, CONCRETES, CONCRETE_POWDERS, SHULKER_BOXES);

	public static final MaterialTag TOOLS = new MaterialTag("_PICKAXE", MatchMode.SUFFIX)
			.append("_AXE", MatchMode.SUFFIX).append("_SHOVEL", MatchMode.SUFFIX).append("_HOE", MatchMode.SUFFIX)
			.append(FISHING_ROD).append(LEAD).append(SHEARS).append(FLINT_AND_STEEL);

	public static final MaterialTag WEAPONS = new MaterialTag("_SWORD", MatchMode.SUFFIX)
			.append(BOW).append(CROSSBOW).append(TRIDENT).append("ARROW", MatchMode.SUFFIX);

	public static final MaterialTag ARMOR = new MaterialTag("_HELMET", MatchMode.SUFFIX)
			.append("_CHESTPLATE", MatchMode.SUFFIX).append("_LEGGINGS", MatchMode.SUFFIX).append("_BOOTS", MatchMode.SUFFIX);

	public static final MaterialTag TOOLS_WEAPONS_ARMOR = new MaterialTag(TOOLS, WEAPONS, ARMOR);

	public static final MaterialTag ARMOR_DIAMOND = new MaterialTag("DIAMOND_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_IRON = new MaterialTag("IRON_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_GOLD = new MaterialTag("GOLDEN_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_CHAINMAIL = new MaterialTag("CHAINMAIL_", MatchMode.PREFIX, MaterialTag.ARMOR);
	public static final MaterialTag ARMOR_LEATHER = new MaterialTag("LEATHER_", MatchMode.PREFIX, MaterialTag.ARMOR);

	public static final MaterialTag TOOLS_DIAMOND = new MaterialTag("DIAMOND_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_IRON = new MaterialTag("IRON_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_GOLD = new MaterialTag("GOLDEN_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_CHAINMAIL = new MaterialTag("CHAINMAIL_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_LEATHER = new MaterialTag("LEATHER_", MatchMode.PREFIX, MaterialTag.TOOLS);

	public static final MaterialTag UNOBTAINABLE = new MaterialTag(WATER, LAVA, AIR,
			STRUCTURE_BLOCK, STRUCTURE_VOID, JIGSAW, BARRIER, BEDROCK,
			COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK, COMMAND_BLOCK_MINECART,
			END_PORTAL, END_PORTAL_FRAME, NETHER_PORTAL, KNOWLEDGE_BOOK,
			DEBUG_STICK, SPAWNER, CHORUS_PLANT);

	public static final MaterialTag REQUIRES_META = new MaterialTag(POTION, SPLASH_POTION, LINGERING_POTION,
			TIPPED_ARROW, WRITTEN_BOOK, ENCHANTED_BOOK);

	public static final MaterialTag ALL_CORALS = new MaterialTag(Tag.CORAL_BLOCKS).append(Tag.CORAL_PLANTS, Tag.WALL_CORALS, Tag.CORALS);
	public static final MaterialTag CORAL_WALL_FANS = new MaterialTag("_WALL_FAN", MatchMode.SUFFIX);

	public static final MaterialTag PLANTS = new MaterialTag(GRASS, FERN, TALL_GRASS, LARGE_FERN, DEAD_BUSH, SWEET_BERRY_BUSH,
			BROWN_MUSHROOM, RED_MUSHROOM, LILY_PAD, BAMBOO_SAPLING, BAMBOO, SEAGRASS, TALL_SEAGRASS, KELP, KELP_PLANT, SUGAR_CANE,
			CACTUS, SEA_PICKLE, CHORUS_PLANT, CHORUS_FLOWER)
			.append(CORAL_WALL_FANS)
			.append(Tag.CORALS, Tag.FLOWERS);

	public static final MaterialTag ALL_FLOWERS = new MaterialTag(SMALL_FLOWERS, TALL_FLOWERS);

	public static final MaterialTag ALL_STONE = new MaterialTag(STONE, STONE_STAIRS, STONE_SLAB, Material.STONE_BRICKS, SMOOTH_STONE, SMOOTH_STONE_SLAB)
		.append(new MaterialTag("STONE_BRICK", MatchMode.PREFIX))
		.append(new MaterialTag("_STONE_BRICKS", MatchMode.SUFFIX)).exclude(END_STONE_BRICKS)
		.append(new MaterialTag("COBBLESTONE", MatchMode.CONTAINS))
		.append(new MaterialTag("GRANITE", MatchMode.CONTAINS))
		.append(new MaterialTag("DIORITE", MatchMode.CONTAINS))
		.append(new MaterialTag("ANDESITE", MatchMode.CONTAINS));

	public static final MaterialTag ALL_DIRT = new MaterialTag(DIRT, GRASS_BLOCK, FARMLAND, PODZOL, COARSE_DIRT);

	public static final MaterialTag VILLAGER_WORKBLOCKS = new MaterialTag(BLAST_FURNACE, SMOKER,
			CARTOGRAPHY_TABLE, BREWING_STAND, COMPOSTER, BARREL, FLETCHING_TABLE,
			CAULDRON, LECTERN, STONECUTTER, LOOM, SMITHING_TABLE, GRINDSTONE);

	public static final MaterialTag TREE_LOGS = new MaterialTag(OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG);
	public static final MaterialTag STRIPPED_LOGS = new MaterialTag(STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG);
	public static final MaterialTag LOGS = new MaterialTag(TREE_LOGS, STRIPPED_LOGS);

	public static final MaterialTag TREE_WOOD = new MaterialTag(OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK_WOOD);
	public static final MaterialTag STRIPPED_WOOD = new MaterialTag(STRIPPED_OAK_WOOD, STRIPPED_SPRUCE_WOOD, STRIPPED_BIRCH_WOOD, STRIPPED_JUNGLE_WOOD, STRIPPED_ACACIA_WOOD, STRIPPED_DARK_OAK_WOOD);
	public static final MaterialTag WOOD = new MaterialTag(TREE_WOOD, STRIPPED_WOOD);

	public static final MaterialTag WOOD_STAIRS = new MaterialTag(OAK_STAIRS, SPRUCE_STAIRS, BIRCH_STAIRS, JUNGLE_STAIRS, ACACIA_STAIRS, DARK_OAK_STAIRS);
	public static final MaterialTag WOOD_SLABS = new MaterialTag(OAK_SLAB, SPRUCE_SLAB, BIRCH_SLAB, JUNGLE_SLAB, ACACIA_SLAB, DARK_OAK_SLAB, PETRIFIED_OAK_SLAB);

	public static final MaterialTag ALL_WOOD = new MaterialTag(LOGS, WOOD, WOOD_STAIRS, WOOD_SLABS);

	public static final MaterialTag SKULLS = new MaterialTag("_SKULL", MatchMode.SUFFIX).append("_HEAD", MatchMode.SUFFIX).exclude(PISTON_HEAD);
	public static final MaterialTag PLAYER_SKULLS = new MaterialTag(PLAYER_HEAD, PLAYER_WALL_HEAD);
	public static final MaterialTag BOATS = new MaterialTag(Tag.ITEMS_BOATS);
	public static final MaterialTag SAPLINGS = new MaterialTag(Tag.SAPLINGS);
	public static final MaterialTag SPAWN_EGGS = new MaterialTag("_SPAWN_EGG", MatchMode.SUFFIX);
	public static final MaterialTag PORTALS = new MaterialTag(END_PORTAL, NETHER_PORTAL);
	public static final MaterialTag LIQUIDS = new MaterialTag(WATER, LAVA);
	public static final MaterialTag CONTAINERS = new MaterialTag(FURNACE, DISPENSER, CHEST, BARREL,
			ENDER_CHEST, Material.ANVIL, BREWING_STAND, TRAPPED_CHEST, HOPPER, DROPPER)
			.append("_SHULKER_BOX", MatchMode.SUFFIX);
	public static final MaterialTag PRESSURE_PLATES = new MaterialTag("_PRESSURE_PLATE", MatchMode.SUFFIX);

	public static final MaterialTag NEEDS_SUPPORT = new MaterialTag(Material.SAND, RED_SAND, GRAVEL,
			VINE, LILY_PAD, TURTLE_EGG, REPEATER, COMPARATOR, ITEM_FRAME, BELL, SNOW, SCAFFOLDING, TRIPWIRE_HOOK,
			LADDER, LEVER, TORCH, WALL_TORCH, REDSTONE_TORCH, REDSTONE_WALL_TORCH)
			.append(SAPLINGS, DOORS, SIGNS, RAILS, BANNERS, CONCRETE_POWDERS, SAND, CORALS, CARPETS,
					PRESSURE_PLATES, BUTTONS, FLOWER_POTS, ANVIL, PLANTS);

	private final EnumSet<Material> materials;
	private final NamespacedKey key = null;

	static {
		for (DyeColor value : DyeColor.values())
			COLORABLE.append(value + "_", MatchMode.PREFIX);
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
		append(segment, mode, materials.getValues().toArray(new Material[0]));
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	public MaterialTag append(Material... materials) {
		this.materials.addAll(Arrays.asList(materials));
		return this;
	}

	@SafeVarargs
	public final MaterialTag append(Tag<Material>... materialTags) {
		for (Tag<Material> materialTag : materialTags)
			this.materials.addAll(materialTag.getValues());

		return this;
	}

	public MaterialTag append(String segment, MatchMode mode) {
		append(segment, mode, Material.values());
		return this;
	}

	public MaterialTag append(String segment, MatchMode mode, Material[] materials) {
		segment = segment.toUpperCase();

		switch (mode) {
			case PREFIX:
				for (Material m : materials)
					if (m.name().startsWith(segment))
						this.materials.add(m);
				break;

			case SUFFIX:
				for (Material m : materials)
					if (m.name().endsWith(segment))
						this.materials.add(m);
				break;

			case CONTAINS:
				for (Material m : materials)
					if (m.name().contains(segment))
						this.materials.add(m);
				break;
		}

		return this;
	}

	public MaterialTag exclude(Material... materials) {
		for (Material m : materials)
			this.materials.remove(m);

		return this;
	}

	@SafeVarargs
	public final MaterialTag exclude(Tag<Material>... materialTags) {
		for (Tag<Material> materialTag : materialTags)
			this.materials.removeAll(materialTag.getValues());

		return this;
	}

	public MaterialTag exclude(String segment, MatchMode mode) {
		exclude(segment, mode, Material.values());
		return this;
	}

	public MaterialTag exclude(String segment, MatchMode mode, Material[] materials) {
		segment = segment.toUpperCase();

		switch (mode) {
			case PREFIX:
				for (Material m : materials)
					if (m.name().startsWith(segment))
						this.materials.remove(m);
				break;

			case SUFFIX:
				for (Material m : materials)
					if (m.name().endsWith(segment))
						this.materials.remove(m);
				break;

			case CONTAINS:
				for (Material m : materials)
					if (m.name().contains(segment))
						this.materials.remove(m);
				break;
		}

		return this;
	}

	@Override
	public Set<Material> getValues() {
		return materials;
	}

	@Override
	public boolean isTagged(Material material) {
		return materials.contains(material);
	}

	@Override
	public String toString() {
		return materials.toString();
	}

	public enum MatchMode {
		PREFIX,
		SUFFIX,
		CONTAINS
	}

}
