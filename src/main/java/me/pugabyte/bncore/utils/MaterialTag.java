package me.pugabyte.bncore.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.Material.*;

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

	public static final MaterialTag DIRTS = new MaterialTag(DIRT, GRASS_BLOCK, FARMLAND, PODZOL, COARSE_DIRT);

	public static final MaterialTag VILLAGER_WORKBLOCKS = new MaterialTag(BLAST_FURNACE, SMOKER,
			CARTOGRAPHY_TABLE, BREWING_STAND, COMPOSTER, BARREL, FLETCHING_TABLE,
			CAULDRON, LECTERN, STONECUTTER, LOOM, SMITHING_TABLE, GRINDSTONE);

	public static final MaterialTag TREE_LOGS = new MaterialTag(OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG);
	public static final MaterialTag STRIPPED_LOGS = new MaterialTag(STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG);
	public static final MaterialTag LOGS = new MaterialTag(TREE_LOGS, STRIPPED_LOGS);

	public static final MaterialTag TREE_WOOD = new MaterialTag(OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK_WOOD);
	public static final MaterialTag STRIPPED_WOOD = new MaterialTag(STRIPPED_OAK_WOOD, STRIPPED_SPRUCE_WOOD, STRIPPED_BIRCH_WOOD, STRIPPED_JUNGLE_WOOD, STRIPPED_ACACIA_WOOD, STRIPPED_DARK_OAK_WOOD);
	public static final MaterialTag WOOD = new MaterialTag(TREE_WOOD, STRIPPED_WOOD);

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
