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

import static gg.projecteden.nexus.utils.Utils.collect;
import static org.bukkit.Material.*;

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
	public static final MaterialTag PRISMARINE = new MaterialTag("PRISMARINE", MatchMode.CONTAINS).exclude(PRISMARINE_CRYSTALS, PRISMARINE_SHARD);
	public static final MaterialTag PURPURS = new MaterialTag("PURPUR", MatchMode.CONTAINS);
	public static final MaterialTag END_STONES = new MaterialTag("END_STONE", MatchMode.CONTAINS);
	public static final MaterialTag SHULKER_BOXES = new MaterialTag("_SHULKER_BOX", MatchMode.SUFFIX).append(SHULKER_BOX);
	public static final MaterialTag BOOKS = new MaterialTag("BOOK", MatchMode.CONTAINS);
	public static final MaterialTag ALL_QUARTZ = new MaterialTag("QUARTZ", MatchMode.CONTAINS).exclude(QUARTZ, NETHER_QUARTZ_ORE);
	public static final MaterialTag ALL_GLASS = new MaterialTag("GLASS", MatchMode.CONTAINS);

	public static final MaterialTag FROGLIGHT = new MaterialTag("_FROGLIGHT", MatchMode.SUFFIX);
	public static final MaterialTag LIGHT_SOURCES = new MaterialTag(GLOWSTONE, MAGMA_BLOCK, CRYING_OBSIDIAN,
		SEA_PICKLE, END_ROD, ENDER_CHEST, SHROOMLIGHT, REDSTONE_LAMP, BEACON, CONDUIT, LAVA_BUCKET, LIGHT)
		.append("LANTERN", MatchMode.CONTAINS)
		.append("TORCH", MatchMode.CONTAINS)
		.append(CANDLES, FROGLIGHT);

	public static final MaterialTag COLORABLE = new MaterialTag(WOOL, DYES, CARPETS, BEDS, ALL_BANNERS,
		ALL_STAINED_GLASS, ALL_TERRACOTTAS, ALL_CONCRETES, SHULKER_BOXES);

	public static final MaterialTag FOODS = new MaterialTag(Material::isEdible);

	public static final MaterialTag SWORDS = new MaterialTag("_SWORD", MatchMode.SUFFIX);
	public static final MaterialTag PICKAXES = new MaterialTag("_PICKAXE", MatchMode.SUFFIX);
	public static final MaterialTag AXES = new MaterialTag("_AXE", MatchMode.SUFFIX);
	public static final MaterialTag SHOVELS = new MaterialTag("_SHOVEL", MatchMode.SUFFIX);
	public static final MaterialTag HOES = new MaterialTag("_HOE", MatchMode.SUFFIX);

	public static final MaterialTag TOOLS = new MaterialTag(PICKAXES, AXES, SHOVELS, HOES)
		.append(FISHING_ROD, LEAD, SHEARS, FLINT_AND_STEEL);

	public static final MaterialTag ARROWS = new MaterialTag("ARROW", MatchMode.SUFFIX);
	public static final MaterialTag WEAPONS = new MaterialTag("_SWORD", MatchMode.SUFFIX)
		.append(BOW, CROSSBOW, TRIDENT).append(ARROWS);

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

	public static final MaterialTag TOOLS_NETHERITE = new MaterialTag("NETHERITE_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_DIAMOND = new MaterialTag("DIAMOND_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_IRON = new MaterialTag("IRON_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_GOLD = new MaterialTag("GOLDEN_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_CHAINMAIL = new MaterialTag("CHAINMAIL_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_STONE = new MaterialTag("STONE_", MatchMode.PREFIX, MaterialTag.TOOLS);
	public static final MaterialTag TOOLS_WOODEN = new MaterialTag("WOODEN_", MatchMode.PREFIX, MaterialTag.TOOLS);

	public static final MaterialTag CHESTS = new MaterialTag(CHEST, TRAPPED_CHEST, BARREL);
	public static final MaterialTag FURNACES = new MaterialTag(FURNACE, BLAST_FURNACE, SMOKER);
	public static final MaterialTag INVENTORY_BLOCKS = new MaterialTag(HOPPER, DISPENSER, DROPPER)
		.append(CHESTS)
		.append(FURNACES)
		.append(SHULKER_BOXES)
		.append(LECTERN);
	public static final MaterialTag MENU_BLOCKS = new MaterialTag(Material.CRAFTING_TABLE, Material.GRINDSTONE,
		Material.ENCHANTING_TABLE, Material.STONECUTTER, Material.CARTOGRAPHY_TABLE, Material.LOOM, Material.BELL,
		Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL, Material.BEACON);

	public static final MaterialTag COMMAND_BLOCKS = new MaterialTag("COMMAND_BLOCK", MatchMode.CONTAINS);
	public static final MaterialTag MINECARTS = new MaterialTag("_MINECART", MatchMode.SUFFIX);

	public static final MaterialTag UNOBTAINABLE = new MaterialTag(WATER, LAVA, AIR,
		STRUCTURE_BLOCK, STRUCTURE_VOID, JIGSAW, BARRIER, BEDROCK,
		COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK, COMMAND_BLOCK_MINECART,
		END_PORTAL, END_PORTAL_FRAME, NETHER_PORTAL, KNOWLEDGE_BOOK,
		DEBUG_STICK, SPAWNER, CHORUS_PLANT);

	public static final MaterialTag UNSTACKABLE = new MaterialTag(material -> material.getMaxStackSize() == 1);

	public static final MaterialTag DYEABLE = new MaterialTag(ARMOR_LEATHER).append(LEATHER_HORSE_ARMOR);

	public static final MaterialTag POTIONS = new MaterialTag(POTION, SPLASH_POTION, LINGERING_POTION);

	public static final MaterialTag POTION_MATERIALS = new MaterialTag(POTIONS)
		.append(GLASS_BOTTLE, FERMENTED_SPIDER_EYE, BLAZE_POWDER, MAGMA_CREAM, GLISTERING_MELON_SLICE, GOLDEN_CARROT,
			RABBIT_FOOT, DRAGON_BREATH, PHANTOM_MEMBRANE, GHAST_TEAR, BREWING_STAND, CAULDRON, CARROT, SLIME_BALL,
			QUARTZ, RED_MUSHROOM, APPLE, ROTTEN_FLESH, BROWN_MUSHROOM, INK_SAC, FERN, POISONOUS_POTATO, GOLDEN_APPLE);

	public static final MaterialTag REQUIRES_META = new MaterialTag(POTIONS).append(TIPPED_ARROW, WRITTEN_BOOK, ENCHANTED_BOOK);

	public static final MaterialTag CORAL_BLOCKS = new MaterialTag("CORAL_BLOCK", MatchMode.CONTAINS);
	public static final MaterialTag CORAL_WALL_FANS = new MaterialTag("_WALL_FAN", MatchMode.SUFFIX);
	public static final MaterialTag ALL_CORALS = new MaterialTag("CORAL", MatchMode.CONTAINS);

	public static final MaterialTag SEEDS = new MaterialTag(COCOA_BEANS, WHEAT_SEEDS, POTATO, CARROT, BEETROOT_SEEDS, PUMPKIN_SEEDS, MELON_SEEDS);
	public static final MaterialTag PLANTS = new MaterialTag(SHORT_GRASS, FERN, TALL_GRASS, LARGE_FERN, DEAD_BUSH, SWEET_BERRY_BUSH,
		BROWN_MUSHROOM, RED_MUSHROOM, LILY_PAD, BAMBOO_SAPLING, BAMBOO, SEAGRASS, TALL_SEAGRASS, KELP, KELP_PLANT, SUGAR_CANE,
		CACTUS, SEA_PICKLE, CHORUS_PLANT, CHORUS_FLOWER, WEEPING_VINES, TWISTING_VINES, NETHER_SPROUTS, WARPED_ROOTS, CRIMSON_ROOTS,
		WARPED_FUNGUS, CRIMSON_FUNGUS, VINE)
		.append(ALL_CORALS, FLOWERS);

	public static final MaterialTag TULIPS = new MaterialTag("_TULIP", MatchMode.SUFFIX).exclude("POTTED", MatchMode.CONTAINS);

	public static final MaterialTag ALL_FLOWERS = new MaterialTag(SMALL_FLOWERS, TALL_FLOWERS);

	public static final MaterialTag ALL_BEEHIVES = new MaterialTag(BEEHIVE, BEE_NEST);

	public static final MaterialTag ALL_PUMPKINS = new MaterialTag(PUMPKIN, CARVED_PUMPKIN, JACK_O_LANTERN);

	public static final MaterialTag ALL_DEEPSLATE = new MaterialTag("DEEPSLATE", MatchMode.CONTAINS);

	// TODO: Include Blackstone?
	public static final MaterialTag ALL_STONE = new MaterialTag(STONE, STONE_STAIRS, STONE_SLAB, Material.STONE_BRICKS, SMOOTH_STONE, SMOOTH_STONE_SLAB)
		.append(ALL_DEEPSLATE).exclude(new MaterialTag("WALL", MatchMode.CONTAINS, ALL_DEEPSLATE))
		.append(new MaterialTag("STONE_BRICK", MatchMode.PREFIX))
		.append(new MaterialTag("_STONE_BRICKS", MatchMode.SUFFIX)).exclude(END_STONE_BRICKS)
		.append(new MaterialTag("COBBLESTONE", MatchMode.CONTAINS))
		.append(new MaterialTag("GRANITE", MatchMode.CONTAINS))
		.append(new MaterialTag("DIORITE", MatchMode.CONTAINS))
		.append(new MaterialTag("ANDESITE", MatchMode.CONTAINS));

	public static final MaterialTag INFESTED_STONE = new MaterialTag("INFESTED_", MatchMode.PREFIX);

	public static final MaterialTag ALL_COPPER = new MaterialTag("COPPER", MatchMode.CONTAINS);

	public static final MaterialTag MINERAL_ORES = new MaterialTag("_ORE", MatchMode.CONTAINS);
	public static final MaterialTag MINERAL_RAW = new MaterialTag(RAW_COPPER, RAW_GOLD, RAW_IRON);
	public static final MaterialTag MINERAL_NUGGETS = new MaterialTag(GOLD_NUGGET, IRON_NUGGET, NETHERITE_SCRAP, ANCIENT_DEBRIS);
	public static final MaterialTag MINERAL_INGOTS = new MaterialTag(COAL, CHARCOAL, LAPIS_LAZULI, REDSTONE, QUARTZ,
		COPPER_INGOT, GOLD_INGOT, IRON_INGOT, DIAMOND, EMERALD, NETHERITE_INGOT);
	public static final MaterialTag MINERAL_RAW_BLOCKS = new MaterialTag(RAW_COPPER_BLOCK, RAW_GOLD_BLOCK, RAW_IRON_BLOCK);
	public static final MaterialTag MINERAL_BLOCKS = new MaterialTag(COAL_BLOCK, LAPIS_BLOCK, REDSTONE_BLOCK, COPPER_BLOCK, GOLD_BLOCK,
		IRON_BLOCK, DIAMOND_BLOCK, EMERALD_BLOCK, NETHERITE_BLOCK)
		.append(ALL_QUARTZ);
	public static final MaterialTag ALL_MINERALS = new MaterialTag(MINERAL_ORES, MINERAL_RAW, MINERAL_NUGGETS, MINERAL_INGOTS, MINERAL_RAW_BLOCKS, MINERAL_BLOCKS);

	public static final MaterialTag MUDABLE_DIRT = new MaterialTag(Material.DIRT, Material.COARSE_DIRT, Material.ROOTED_DIRT);
	public static final MaterialTag ALL_DIRT = new MaterialTag(Tag.DIRT).append(DIRT_PATH, FARMLAND);
	public static final MaterialTag ALL_SAND = new MaterialTag(Material.SAND, SUSPICIOUS_SAND, RED_SAND);
	public static final MaterialTag NATURAL_GRAVITY_SEDIMENT = new MaterialTag(ALL_SAND).append(GRAVEL, SUSPICIOUS_GRAVEL);

	public static final MaterialTag VILLAGER_WORKBLOCKS = new MaterialTag(BLAST_FURNACE, SMOKER,
		CARTOGRAPHY_TABLE, BREWING_STAND, COMPOSTER, BARREL, FLETCHING_TABLE,
		CAULDRON, LECTERN, STONECUTTER, LOOM, SMITHING_TABLE, GRINDSTONE);

	public static final MaterialTag TREE_LOGS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getLog).forEach(this::append); }};
	public static final MaterialTag STRIPPED_LOGS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getStrippedLog).forEach(this::append); }};
	public static final MaterialTag TREE_WOOD = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getWood).forEach(this::append); }};
	public static final MaterialTag STRIPPED_WOOD = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getStrippedWood).forEach(this::append); }};
	public static final MaterialTag WOOD_STAIRS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getStair).forEach(this::append); }};
	public static final MaterialTag WOOD_SLABS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getSlab).forEach(this::append); }};
	public static final MaterialTag WOOD_BUTTONS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getButton).forEach(this::append); }};
	public static final MaterialTag WOOD_DOORS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getDoor).forEach(this::append); }};
	public static final MaterialTag WOOD_TRAPDOORS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getTrapDoor).forEach(this::append); }};
	public static final MaterialTag WOOD_FENCES = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getFence).forEach(this::append); }};
	public static final MaterialTag WOOD_FENCE_GATES = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getFenceGate).forEach(this::append); }};
	public static final MaterialTag WOOD_PRESSURE_PLATES = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getPressurePlate).forEach(this::append); }};
	public static final MaterialTag WOOD_SIGNS = new MaterialTag() {{ Arrays.stream(WoodType.values()).map(WoodType::getSign).forEach(this::append); }};

	public static final MaterialTag LOGS = new MaterialTag(TREE_LOGS, STRIPPED_LOGS);
	public static final MaterialTag WOOD = new MaterialTag(TREE_WOOD, STRIPPED_WOOD);

	public static final MaterialTag ALL_WOOD = new MaterialTag(LOGS, WOOD, PLANKS, WOOD_STAIRS, WOOD_SLABS, WOOD_BUTTONS,
		WOOD_DOORS, WOOD_TRAPDOORS, WOOD_FENCES, WOOD_FENCE_GATES, WOOD_PRESSURE_PLATES, WOOD_SIGNS);

	public static final MaterialTag ALL_SIGNS = new MaterialTag(Tag.ALL_SIGNS);

	public static final MaterialTag ALL_NETHER = new MaterialTag(GLOWSTONE, OBSIDIAN, CRYING_OBSIDIAN, GRAVEL, MAGMA_BLOCK, MAGMA_CREAM,
		WITHER_SKELETON_SKULL, BLAZE_ROD, BLAZE_POWDER, WEEPING_VINES, TWISTING_VINES, MUSIC_DISC_PIGSTEP)
		.append("NETHER", MatchMode.CONTAINS)
		.append("NYLIUM", MatchMode.CONTAINS)
		.append("WARPED", MatchMode.CONTAINS)
		.append("CRIMSON", MatchMode.CONTAINS)
		.append("BLACKSTONE", MatchMode.CONTAINS)
		.append("SOUL", MatchMode.CONTAINS)
		.append(GOLDEN_SWORD, GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS)
		.append(NETHER_QUARTZ_ORE, QUARTZ, NETHER_GOLD_ORE, GOLD_NUGGET, GOLD_INGOT, GOLD_BLOCK).append(ALL_QUARTZ);

	public static final MaterialTag ALL_END = new MaterialTag("END", MatchMode.CONTAINS)
		.append("PURPUR", MatchMode.CONTAINS)
		.append("CHORUS", MatchMode.CONTAINS)
		.append(SHULKER_BOXES)
		.append(SHULKER_SHELL, ELYTRA, DRAGON_EGG, DRAGON_HEAD, OBSIDIAN, MAGENTA_STAINED_GLASS);

	public static final MaterialTag RAW_FISH = new MaterialTag(SALMON, COD, TROPICAL_FISH, PUFFERFISH);
	public static final MaterialTag COOKED_FISH = new MaterialTag(COOKED_SALMON, COOKED_COD);

	public static final MaterialTag ALL_FISH = new MaterialTag(RAW_FISH, COOKED_FISH);

	public static final MaterialTag FISH_BUCKETS = new MaterialTag(TROPICAL_FISH_BUCKET, PUFFERFISH_BUCKET, COD_BUCKET, SALMON_BUCKET);

	public static final MaterialTag ALL_OCEAN = new MaterialTag(SOUL_SAND, MAGMA_BLOCK, TURTLE_EGG, CONDUIT, SCUTE,
		PUFFERFISH_BUCKET, SALMON_BUCKET, COD_BUCKET, TROPICAL_FISH_BUCKET, NAUTILUS_SHELL, COD, SALMON, PUFFERFISH,
		TROPICAL_FISH, COOKED_COD, COOKED_SALMON, LILY_PAD, TURTLE_HELMET, FISHING_ROD, INK_SAC, GRAVEL, Material.SAND)
		.append("PRISMARINE", MatchMode.CONTAINS)
		.append("KELP", MatchMode.CONTAINS)
		.append("SEA", MatchMode.CONTAINS)
		.append("BOAT", MatchMode.CONTAINS)
		.append(ALL_CORALS, ALL_FISH);

	public static final MaterialTag MUSIC = new MaterialTag("DISC", MatchMode.CONTAINS).append(NOTE_BLOCK, JUKEBOX, BELL);

	public static final MaterialTag BLOCKS = new MaterialTag(Material::isSolid)
		.exclude(CACTUS, BAMBOO, DRAGON_EGG, TURTLE_EGG, CONDUIT, CAKE)
		.exclude(SIGNS, ALL_BANNERS, ALL_CORALS)
		.append(CORAL_BLOCKS);

	public static final MaterialTag ITEMS = new MaterialTag(Material::isItem);

	public static final MaterialTag SKULLS = new MaterialTag("_SKULL", MatchMode.SUFFIX).append("_HEAD", MatchMode.SUFFIX).exclude(PISTON_HEAD);
	public static final MaterialTag PLAYER_SKULLS = new MaterialTag(PLAYER_HEAD, PLAYER_WALL_HEAD);
	public static final MaterialTag MOB_SKULLS = new MaterialTag(SKULLS).exclude(PLAYER_SKULLS);
	public static final MaterialTag FLOOR_SKULLS = new MaterialTag(SKULLS).exclude("WALL", MatchMode.CONTAINS);

	public static final MaterialTag BOATS = new MaterialTag(Tag.ITEMS_BOATS);
	public static final MaterialTag SAPLINGS = new MaterialTag(Tag.SAPLINGS);
	public static final MaterialTag SPAWN_EGGS = new MaterialTag("_SPAWN_EGG", MatchMode.SUFFIX);
	public static final MaterialTag PORTALS = new MaterialTag(END_PORTAL, NETHER_PORTAL);
	public static final MaterialTag LIQUIDS = new MaterialTag(WATER, LAVA);
	public static final MaterialTag CONTAINERS = new MaterialTag(ENDER_CHEST, Material.ANVIL, BREWING_STAND).append(INVENTORY_BLOCKS);
	public static final MaterialTag PRESSURE_PLATES = new MaterialTag("_PRESSURE_PLATE", MatchMode.SUFFIX);
	public static final MaterialTag TORCHES = new MaterialTag("TORCH", MatchMode.CONTAINS);
	public static final MaterialTag CAMPFIRES = new MaterialTag("CAMPFIRE", MatchMode.CONTAINS);
	public static final MaterialTag FLORA = new MaterialTag(SAPLINGS, FLOWERS, PLANTS, SEEDS, LEAVES);
	public static final MaterialTag REDSTONE_ACTIVATORS = new MaterialTag(BUTTONS, PRESSURE_PLATES).append(LEVER);
	public static final MaterialTag SUSPICIOUS_BLOCKS = new MaterialTag(SUSPICIOUS_SAND, SUSPICIOUS_GRAVEL);
	public static final MaterialTag POTTERY_SHERDS = new MaterialTag("_POTTERY_SHERD", MatchMode.SUFFIX);

	public static final MaterialTag NEEDS_SUPPORT = new MaterialTag(Material.GRAVEL, VINE, LILY_PAD, TURTLE_EGG,
		REPEATER, COMPARATOR, ITEM_FRAME, BELL, Material.SNOW, SCAFFOLDING, TRIPWIRE_HOOK, LADDER, LEVER, SOUL_LANTERN)
		.append(SAPLINGS, DOORS, SIGNS, RAILS, ALL_BANNERS, CONCRETE_POWDERS, SAND, CORALS, CARPETS,
			PRESSURE_PLATES, BUTTONS, FLOWER_POTS, ANVIL, PLANTS, TORCHES);

	public static final MaterialTag SPAWNS_ENTITY = new MaterialTag(SPAWN_EGGS, BOATS, MINECARTS).append(EGG, SNOWBALL, BOW, CROSSBOW,
		TRIDENT, FIREWORK_ROCKET, ENDER_PEARL, ENDER_EYE, SPLASH_POTION, LINGERING_POTION, EXPERIENCE_BOTTLE, ARMOR_STAND, ITEM_FRAME,
		GLOW_ITEM_FRAME, PAINTING);

	public static final MaterialTag WEARABLE = new MaterialTag(ARMOR, SKULLS).append(CARVED_PUMPKIN).exclude("_WALL_", MatchMode.CONTAINS);

	public static final MaterialTag INTERACTABLES = new MaterialTag(BEDS, SHULKER_BOXES, CONTAINERS, WOOD_FENCE_GATES,
			DOORS, TRAPDOORS, BUTTONS, MENU_BLOCKS, CAULDRONS).append(
			REPEATER, COMPARATOR, NOTE_BLOCK, JUKEBOX, DAYLIGHT_DETECTOR, LEVER, REDSTONE_WIRE, CHISELED_BOOKSHELF,
			COMPOSTER, DECORATED_POT);

	@SneakyThrows
	public static Map<String, Tag<Material>> getApplicable(Material material) {
		return collect(tags.entrySet().stream().filter(entry -> entry.getValue().isTagged(material)));
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
