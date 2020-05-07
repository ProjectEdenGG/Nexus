package me.pugabyte.bncore.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class MaterialTag implements Tag<Material> {
	public static final MaterialTag WOOL = new MaterialTag("_WOOL", MatchMode.SUFFIX);
	public static final MaterialTag DYES = new MaterialTag("_DYE", MatchMode.SUFFIX);
	public static final MaterialTag CARPET = new MaterialTag("_CARPET", MatchMode.SUFFIX);
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
	public static final MaterialTag SHULKER_BOXES = new MaterialTag("_SHULKER_BOX", MatchMode.SUFFIX).append(Material.SHULKER_BOX);

	public static final MaterialTag COLORABLE = new MaterialTag(WOOL, DYES, CARPET, BEDS, BANNERS, WALL_BANNERS, STAINED_GLASS,
			STAINED_GLASS_PANES, TERRACOTTAS, GLAZED_TERRACOTTAS, CONCRETES, CONCRETE_POWDERS, SHULKER_BOXES);

	public static final MaterialTag TOOLS = new MaterialTag("_PICKAXE", MatchMode.SUFFIX)
			.append("_AXE", MatchMode.SUFFIX).append("_SHOVEL", MatchMode.SUFFIX).append("_HOE", MatchMode.SUFFIX)
			.append(Material.FISHING_ROD).append(Material.LEAD).append(Material.SHEARS).append(Material.FLINT_AND_STEEL);

	public static final MaterialTag WEAPONS = new MaterialTag("_SWORD", MatchMode.SUFFIX)
			.append(Material.BOW).append(Material.CROSSBOW).append(Material.TRIDENT).append("ARROW", MatchMode.SUFFIX);

	public static final MaterialTag ARMOR = new MaterialTag("_HELMET", MatchMode.SUFFIX)
			.append("_CHESTPLATE", MatchMode.SUFFIX).append("_LEGGINGS", MatchMode.SUFFIX).append("_BOOTS", MatchMode.SUFFIX);

	public static final MaterialTag TOOLS_WEAPONS_ARMOR = new MaterialTag(TOOLS, WEAPONS, ARMOR);

	public static final MaterialTag UNOBTAINABLE = new MaterialTag(Material.WATER, Material.LAVA, Material.AIR,
			Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.JIGSAW, Material.BARRIER, Material.BEDROCK,
			Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.COMMAND_BLOCK_MINECART,
			Material.END_PORTAL, Material.END_PORTAL_FRAME, Material.NETHER_PORTAL, Material.KNOWLEDGE_BOOK,
			Material.DEBUG_STICK, Material.SPAWNER, Material.CHORUS_PLANT);

	public static final MaterialTag REQUIRES_META = new MaterialTag(Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION,
			Material.TIPPED_ARROW, Material.WRITTEN_BOOK, Material.ENCHANTED_BOOK);

	public static final MaterialTag CORAL_WALL_FANS = new MaterialTag("_WALL_FAN", MatchMode.SUFFIX);

	public static final MaterialTag PLANTS = new MaterialTag(Material.GRASS, Material.FERN, Material.DEAD_BUSH,
			Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
			Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
			Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.LILY_PAD, Material.KELP, Material.KELP_PLANT)
			.append(CORAL_WALL_FANS)
			.append(Tag.CORALS);

	public static final MaterialTag TREE_LOGS = new MaterialTag(Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG);
	public static final MaterialTag STRIPPED_LOGS = new MaterialTag(Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_DARK_OAK_LOG);
	public static final MaterialTag LOGS = new MaterialTag(TREE_LOGS, STRIPPED_LOGS);

	public static final MaterialTag TREE_WOOD = new MaterialTag(Material.OAK_WOOD, Material.SPRUCE_WOOD, Material.BIRCH_WOOD, Material.JUNGLE_WOOD, Material.ACACIA_WOOD, Material.DARK_OAK_WOOD);
	public static final MaterialTag STRIPPED_WOOD = new MaterialTag(Material.STRIPPED_OAK_WOOD, Material.STRIPPED_SPRUCE_WOOD, Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_DARK_OAK_WOOD);
	public static final MaterialTag WOOD = new MaterialTag(TREE_WOOD, STRIPPED_WOOD);

	public static final MaterialTag SKULLS = new MaterialTag("_SKULL", MatchMode.SUFFIX).append("_HEAD", MatchMode.SUFFIX);
	public static final MaterialTag BOATS = new MaterialTag(Tag.ITEMS_BOATS);
	public static final MaterialTag SPAWN_EGGS = new MaterialTag("_SPAWN_EGG", MatchMode.SUFFIX);
	public static final MaterialTag PORTALS = new MaterialTag(Material.END_PORTAL, Material.NETHER_PORTAL);
	public static final MaterialTag LIQUIDS = new MaterialTag(Material.WATER, Material.LAVA);
	public static final MaterialTag CONTAINERS = new MaterialTag(Material.FURNACE, Material.DISPENSER, Material.CHEST, Material.BARREL,
			Material.ENDER_CHEST, Material.ANVIL, Material.BREWING_STAND, Material.TRAPPED_CHEST, Material.HOPPER, Material.DROPPER)
			.append("_SHULKER_BOX", MatchMode.SUFFIX);
	public static final MaterialTag PRESSURE_PLATES = new MaterialTag("_PRESSURE_PLATE", MatchMode.SUFFIX);

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
		segment = segment.toUpperCase();

		switch (mode) {
			case PREFIX:
				for (Material m : Material.values())
					if (m.name().startsWith(segment))
						materials.add(m);
				break;

			case SUFFIX:
				for (Material m : Material.values())
					if (m.name().endsWith(segment))
						materials.add(m);
				break;

			case CONTAINS:
				for (Material m : Material.values())
					if (m.name().contains(segment))
						materials.add(m);
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

		segment = segment.toUpperCase();

		switch (mode) {
			case PREFIX:
				for (Material m : Material.values())
					if (m.name().startsWith(segment))
						materials.remove(m);
				break;

			case SUFFIX:
				for (Material m : Material.values())
					if (m.name().endsWith(segment))
						materials.remove(m);
				break;

			case CONTAINS:
				for (Material m : Material.values())
					if (m.name().contains(segment))
						materials.remove(m);
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
