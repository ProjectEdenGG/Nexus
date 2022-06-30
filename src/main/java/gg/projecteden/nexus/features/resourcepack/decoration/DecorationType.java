package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable.Type;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Block;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Chair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Couch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Couch.CouchPart;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.PlayerPlushie;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.features.resourcepack.playerplushies.Pose;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
	TODO:
		- add new dyeables, make converter automatic using entity spawn event or something
		- figure out player plushies
		- finish adding rest of decorations
 */

@AllArgsConstructor
public enum DecorationType {
	// Player Plushies: TODO
	PLAYER_PLUSHIE_STANDING(new PlayerPlushie("Player Plushie", 1, Pose.STANDING)),
	// Mob Plushies: TODO
	// Trophies: TODO
	// Tables
	TABLE_WOODEN_1x1(new Table("Wooden Table 1x1", 300, Type.STAIN, Table.TableSize._1x1)),
	TABLE_WOODEN_1x2(new Table("Wooden Table 1x2", 301, Type.STAIN, Table.TableSize._1x2)),
	TABLE_WOODEN_2x2(new Table("Wooden Table 2x2", 302, Type.STAIN, Table.TableSize._2x2)),
	TABLE_WOODEN_2x3(new Table("Wooden Table 2x3", 303, Type.STAIN, Table.TableSize._2x3)),
	TABLE_WOODEN_3x3(new Table("Wooden Table 3x3", 304, Type.STAIN, Table.TableSize._3x3)),
	// Chairs
	CHAIR_WOODEN_BASIC(new Chair("Wooden Chair", 400, Type.STAIN)),
	CHAIR_WOODEN_CUSHION(new Chair("Cushioned Wooden Chair", 401, Type.DYE)),
	// Stools
	STOOL_WOODEN_BASIC(new Chair("Wooden Stool", 500, Type.STAIN)),
	STOOL_WOODEN_CUSHION(new Chair("Cushioned Wooden Stool", 501, Type.DYE)),
	STOOL_STUMP(new Chair("Oak Stump", 301, Type.NONE)),
	STOOL_STUMP_ROOTS(new Chair("Rooted Oak Stump", 302, Type.NONE)),
	// Benches
	BENCH_WOODEN(new Bench("Wooden Bench", 450, Type.STAIN)),
	// Couches
	COUCH_WOODEN_END_LEFT(new Couch("Wooden Couch Left End", 525, Type.DYE, CouchPart.END)),
	COUCH_WOODEN_END_RIGHT(new Couch("Wooden Couch Left Right", 526, Type.DYE, CouchPart.END)),
	COUCH_WOODEN_MIDDLE(new Couch("Wooden Couch Middle", 527, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_WOODEN_CORNER(new Couch("Wooden Couch Corner", 528, Type.DYE, CouchPart.CORNER)),
	COUCH_WOODEN_OTTOMAN(new Couch("Wooden Couch Ottoman", 529, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_MODERN_END_LEFT(new Couch("Modern Couch Left End", 530, Type.DYE, CouchPart.END)),
	COUCH_MODERN_END_RIGHT(new Couch("Modern Couch Left Right", 531, Type.DYE, CouchPart.END)),
	COUCH_MODERN_MIDDLE(new Couch("Modern Couch Middle", 532, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_MODERN_CORNER(new Couch("Modern Couch Corner", 533, Type.DYE, CouchPart.CORNER)),
	COUCH_MODERN_OTTOMAN(new Couch("Modern Couch Ottoman", 534, Type.DYE, CouchPart.STRAIGHT)),
	// Blocks
	DYE_STATION(new Block("Dye Station", 1, Material.CRAFTING_TABLE)),
	// Fireplaces: TODO
	// Gravestones
	GRAVESTONE_SMALL(new DecorationConfig("Small Gravestone", 2, Material.STONE)),
	GRAVESTONE_CROSS(new DecorationConfig("Gravestone Cross", 1, Material.STONE, Hitbox.single(Material.IRON_BARS))),
	GRAVESTONE_PLAQUE(new DecorationConfig("Gravestone Plaque", 4, Material.STONE)),
	GRAVESTONE_STACK(new DecorationConfig("Rock Stack Gravestone", 3, Material.STONE)),
	GRAVESTONE_FLOWERBED(new DecorationConfig("Flowerbed Gravestone", 6, Material.STONE)),
	GRAVESTONE_TALL(new DecorationConfig("Tall Gravestone", 5, Material.STONE, List.of(Hitbox.origin(Material.IRON_BARS), Hitbox.offset(Material.IRON_BARS, BlockFace.UP)))),
	// Food
	PIZZA_BOX_SINGLE(new DecorationConfig("Pizza Box", 1, Material.BREAD)),
	PIZZA_BOX_SINGLE_OPENED(new DecorationConfig("Opened Pizza Box", 3, Material.BREAD)),
	PIZZA_BOX_STACK(new DecorationConfig("Pizza Box Stack", 2, Material.BREAD)),
	SOUP_MUSHROOM(new DecorationConfig("Mushroom Soup", 4, Material.BOWL)),
	SOUP_BEETROOT(new DecorationConfig("Beetroot Soup", 5, Material.BOWL)),
	SOUP_RABBIT(new DecorationConfig("Rabbit Soup", 6, Material.BOWL)),
	BREAD_LOAF(new DecorationConfig("Loaf of Bread", 4, Material.BREAD)),
	BREAD_LOAF_CUT(new DecorationConfig("Cut Loaf of Bread", 5, Material.BREAD)),
	BROWNIES_CHOCOLATE(new DecorationConfig("Chocolate Brownies", 101)),
	BROWNIES_VANILLA(new DecorationConfig("Vanilla Brownies", 102)),
	COOKIES_CHOCOLATE(new DecorationConfig("Chocolate Cookies", 103)),
	COOKIES_CHOCOLATE_CHIP(new DecorationConfig("Chocolate Chip Cookies", 104)),
	COOKIES_SUGAR(new DecorationConfig("Sugar Cookies", 105)),
	MILK_AND_COOKIES(new DecorationConfig("Milk and Cookies", 109)),
	MUFFINS_CHOCOLATE(new DecorationConfig("Chocolate Muffins", 110)),
	MUFFINS_CHOCOLATE_CHIP(new DecorationConfig("Chocolate Chip Muffins", 111)),
	MUFFINS_LEMON(new DecorationConfig("Lemon Muffins", 112)),
	DINNER_HAM(new DecorationConfig("Ham Dinner", 106)),
	DINNER_ROAST(new DecorationConfig("Roast Dinner", 107)),
	DINNER_TURKEY(new DecorationConfig("Turkey Dinner", 108)),
	//	PUNCHBOWL_EGGNOG(new Dyeable("name", 113)), // TODO: Make dyeable
//	SIDE_CRANBERRIES(new Dyeable("name", 114)), // TODO: Make dyeable
	SIDE_GREEN_BEAN_CASSEROLE(new DecorationConfig("Green Bean Casserole Side", 115)),
	SIDE_MAC_AND_CHEESE(new DecorationConfig("Mac N' Cheese Side", 116)),
	SIDE_SWEET_POTATOES(new DecorationConfig("Sweet Potatoes Side", 119)),
	SIDE_MASHED_POTATOES(new DecorationConfig("Mashed Potatoes Side", 117)),
	SIDE_ROLLS(new DecorationConfig("Rolls", 118)),
	//	CAKE_BATTER(new Dyeable("name", -1)), // TODO: Make dyeable
	CAKE_WHITE_CHOCOLATE(new DecorationConfig("White Chocolate Cake", 122)),
	CAKE_BUNDT(new DecorationConfig("Bundt Cake", 123)),
	CAKE_CHOCOLATE_DRIP(new DecorationConfig("Chocolate Drip Cake", 125)),
//	PIE(new Dyeable("name", -1)), // TODO: Make dyeable
//	PIE_LATTICED(new Dyeable("name", -1)), // TODO: Make dyeable
	// Kitchenware
	WINE_BOTTLE(new DecorationConfig("Wine Bottle", 400, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_BOTTLE_GROUP(new DecorationConfig("Wine Bottles", 454, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_BOTTLE_GROUP_RANDOM(new DecorationConfig("Random Wine Bottles", 453, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_BOTTLE_GROUP_SIDE(new DecorationConfig("Wine Bottles on Side", 455, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_GLASS(new DecorationConfig("Wine Glass", 401, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_GLASS_FULL(new DecorationConfig("Full Wine Glass", 402, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_GLASS(new DecorationConfig("Glass Mug", 403, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_GLASS_FULL(new DecorationConfig("Full Glass Mug", 404, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_WOODEN(new DecorationConfig("Wooden Mug", 406, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_WOODEN_FULL(new DecorationConfig("Full Wooden Mug", 405, Material.BLUE_STAINED_GLASS_PANE)),
	GLASSWARE_GROUP_1(new DecorationConfig("Random Glassware 1", 450, Material.BLUE_STAINED_GLASS_PANE)),
	GLASSWARE_GROUP_2(new DecorationConfig("Random Glassware 2", 451, Material.BLUE_STAINED_GLASS_PANE)),
	GLASSWARE_GROUP_3(new DecorationConfig("Random Glassware 3", 452, Material.BLUE_STAINED_GLASS_PANE)),
	JAR(new DecorationConfig("Jar", 501, Material.BLUE_STAINED_GLASS_PANE)),
	JAR_HONEY(new DecorationConfig("Honey Jar", 502, Material.BLUE_STAINED_GLASS_PANE)),
	JAR_COOKIES(new DecorationConfig("Cookie Jar", 503, Material.BLUE_STAINED_GLASS_PANE)),
	JAR_WIDE(new DecorationConfig("Wide Jar", 500, Material.BLUE_STAINED_GLASS_PANE)),
	BOWL(new DecorationConfig("Wooden Bowl", 3, Material.BOWL)),
	MIXING_BOWL(new DecorationConfig("Mixing Bowl", 133)),
	PAN_CAKE(new DecorationConfig("Cake Pan", 134)),
	PAN_CASSEROLE(new DecorationConfig("Casserole Pan", 135)),
	PAN_COOKIE(new DecorationConfig("Cookie Pan", 136)),
	PAN_MUFFIN(new DecorationConfig("Muffin Pan", 137)),
	PAN_PIE(new DecorationConfig("Pie Pan", 138)),
	// Potions
	POTION_TINY_1(new Dyeable("Tiny Potions 1", 27, Type.DYE)),
	POTION_TINY_2(new Dyeable("Tiny Potions 2", 28, Type.DYE)),
	POTION_SMALL_1(new Dyeable("Small Potion 1", 6, Type.DYE)),
	POTION_SMALL_2(new Dyeable("Small Potion 2", 7, Type.DYE)),
	POTION_SMALL_3(new Dyeable("Small Potion 3", 8, Type.DYE)),
	POTION_MEDIUM_1(new Dyeable("Medium Potion 1", 9, Type.DYE)),
	POTION_MEDIUM_2(new Dyeable("Medium Potion 2", 10, Type.DYE)),
	POTION_WIDE(new Dyeable("Wide Potion", 11, Type.DYE)),
	POTION_SKINNY(new Dyeable("Skinny Potion", 12, Type.DYE)),
	POTION_TALL(new Dyeable("Tall Potion", 13, Type.DYE)),
	POTION_BIG_BOTTLE(new Dyeable("Big Potion Bottle", 14, Type.DYE)),
	POTION_BIG_TEAR(new Dyeable("Big Potion Tear", 15, Type.DYE)),
	POTION_BIG_DONUT(new Dyeable("Big Potion Donut", 16, Type.DYE)),
	POTION_BIG_SKULL(new Dyeable("Big Potion Skull", 17, Type.DYE)),
	POTION_GROUP_SMALL(new Dyeable("Small Potions", 18, Type.DYE)),
	POTION_GROUP_MEDIUM(new Dyeable("Medium Potions", 21, Type.DYE)),
	POTION_GROUP_TALL(new Dyeable("Tall Potions", 26, Type.DYE)),
	POTION_GROUP_RANDOM_1(new Dyeable("Random Potions 1", 29, Type.DYE)),
	POTION_GROUP_RANDOM_2(new Dyeable("Random Potions 2", 30, Type.DYE)),
	POTION_GROUP_RANDOM_3(new Dyeable("Random Potions 3", 31, Type.DYE)),
	POTION_GROUP_RANDOM_4(new Dyeable("Random Potions 4", 32, Type.DYE)),
	POTION_GROUP_RANDOM_5(new Dyeable("Random Potions 5", 33, Type.DYE)),
	POTION_GROUP_RANDOM_6(new Dyeable("Random Potions 6", 34, Type.DYE)),
	POTION_GROUP_RANDOM_7(new Dyeable("Random Potions 7", 35, Type.DYE)),
	POTION_GROUP_RANDOM_8(new Dyeable("Random Potions 8", 36, Type.DYE)),
	// Misc
	INKWELL(new DecorationConfig("Inkwell", 1, Material.FEATHER)),
	WHEEL_SMALL(new DecorationConfig("Small Wheel", 2, Material.MUSIC_DISC_11)),
	;

	@Getter
	final DecorationConfig config;

	public ItemStack getItem() {
		return config.getItem().clone();
	}

	public static DecorationType of(ItemStack tool) {
		if (Nullables.isNullOrAir(tool))
			return null;

		for (DecorationType decoration : values()) {
			if (decoration.isFuzzyMatch(tool))
				return decoration;
		}

		return null;
	}

	public boolean isFuzzyMatch(ItemStack item2) {
		ItemStack item1 = this.getItem();

		if (item2 == null)
			return false;

		if (!item1.getType().equals(item2.getType()))
			return false;

		int decorModelData = CustomModelData.of(item1);
		int itemModelData = CustomModelData.of(item2);
		if (decorModelData != itemModelData)
			return false;

		return true;
	}

	private static final Set<Material> hitboxTypes = new HashSet<>();

	public static Set<Material> getHitboxTypes() {
		if (!hitboxTypes.isEmpty())
			return hitboxTypes;

		Arrays.stream(values()).forEach(decorationType ->
			hitboxTypes.addAll(decorationType.getConfig().getHitboxes()
				.stream()
				.map(Hitbox::getMaterial)
				.filter(material -> !MaterialTag.ALL_AIR.isTagged(material))
				.toList()));

		return hitboxTypes;
	}

}
