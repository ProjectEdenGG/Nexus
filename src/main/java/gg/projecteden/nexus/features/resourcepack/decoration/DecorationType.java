package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable.Type;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Block;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.types.RotatableBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Chair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch.CouchPart;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Stump;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;

/*
	TODO:
		- finish adding rest of decorations
		- cant interact with decorations with item in offhand
		- add "Structure" type
 */

@AllArgsConstructor
public enum DecorationType {
	// Mob Plushies: TODO
	// Tables
	TABLE_WOODEN_1x1(new Table("Wooden Table 1x1", CustomMaterial.TABLE_WOODEN_1X1, Type.STAIN, Table.TableSize._1x1)),
	TABLE_WOODEN_1x2(new Table("Wooden Table 1x2", CustomMaterial.TABLE_WOODEN_1X2, Type.STAIN, Table.TableSize._1x2)),
	TABLE_WOODEN_2x2(new Table("Wooden Table 2x2", CustomMaterial.TABLE_WOODEN_2X2, Type.STAIN, Table.TableSize._2x2)),
	TABLE_WOODEN_2x3(new Table("Wooden Table 2x3", CustomMaterial.TABLE_WOODEN_2X3, Type.STAIN, Table.TableSize._2x3)),
	TABLE_WOODEN_3x3(new Table("Wooden Table 3x3", CustomMaterial.TABLE_WOODEN_3X3, Type.STAIN, Table.TableSize._3x3)),
	// Chairs
	CHAIR_WOODEN_BASIC(new Chair("Wooden Chair", CustomMaterial.CHAIR_WOODEN_BASIC, Type.STAIN)),
	CHAIR_WOODEN_CUSHION(new Chair("Cushioned Wooden Chair", CustomMaterial.CHAIR_WOODEN_CUSHION, Type.DYE)),
	ADIRONDACK(new Chair("Adirondack", CustomMaterial.ADIRONDACK, Type.DYE)),
	// Stools
	STOOL_WOODEN_BASIC(new Chair("Wooden Stool", CustomMaterial.STOOL_WOODEN_BASIC, Type.STAIN)),
	STOOL_WOODEN_CUSHION(new Chair("Cushioned Wooden Stool", CustomMaterial.STOOL_WOODEN_CUSHION, Type.DYE)),
	STOOL_STUMP_OAK(new Stump("Oak Stump", CustomMaterial.STOOL_STUMP_OAK)),
	STOOL_STUMP_OAK_ROOTS(new Stump("Rooted Oak Stump", CustomMaterial.STOOL_STUMP_OAK_ROOTS)),
	STOOL_STUMP_SPRUCE(new Stump("Spruce Stump", CustomMaterial.STOOL_STUMP_SPRUCE)),
	STOOL_STUMP_SPRUCE_ROOTS(new Stump("Rooted Spruce Stump", CustomMaterial.STOOL_STUMP_SPRUCE_ROOTS)),
	STOOL_STUMP_BIRCH(new Stump("Birch Stump", CustomMaterial.STOOL_STUMP_BIRCH)),
	STOOL_STUMP_BIRCH_ROOTS(new Stump("Rooted Birch Stump", CustomMaterial.STOOL_STUMP_BIRCH_ROOTS)),
	STOOL_STUMP_JUNGLE(new Stump("Jungle Stump", CustomMaterial.STOOL_STUMP_JUNGLE)),
	STOOL_STUMP_JUNGLE_ROOTS(new Stump("Rooted Jungle Stump", CustomMaterial.STOOL_STUMP_JUNGLE_ROOTS)),
	STOOL_STUMP_ACACIA(new Stump("Acacia Stump", CustomMaterial.STOOL_STUMP_ACACIA)),
	STOOL_STUMP_ACACIA_ROOTS(new Stump("Rooted Acacia Stump", CustomMaterial.STOOL_STUMP_ACACIA_ROOTS)),
	STOOL_STUMP_DARK_OAK(new Stump("Dark Oak Stump", CustomMaterial.STOOL_STUMP_DARK_OAK)),
	STOOL_STUMP_DARK_OAK_ROOTS(new Stump("Rooted Dark Oak Stump", CustomMaterial.STOOL_STUMP_DARK_OAK_ROOTS)),
	STOOL_STUMP_MANGROVE(new Stump("Mangrove Stump", CustomMaterial.STOOL_STUMP_MANGROVE)),
	STOOL_STUMP_MANGROVE_ROOTS(new Stump("Rooted Mangrove Stump", CustomMaterial.STOOL_STUMP_MANGROVE_ROOTS)),
	// Benches
	BENCH_WOODEN(new Bench("Wooden Bench", CustomMaterial.BENCH_WOODEN, Type.STAIN)),
	// Couches
	COUCH_WOODEN_END_LEFT(new Couch("Wooden Couch Left End", CustomMaterial.COUCH_WOODEN_END_LEFT, Type.DYE, CouchPart.END)),
	COUCH_WOODEN_END_RIGHT(new Couch("Wooden Couch Left Right", CustomMaterial.COUCH_WOODEN_END_RIGHT, Type.DYE, CouchPart.END)),
	COUCH_WOODEN_MIDDLE(new Couch("Wooden Couch Middle", CustomMaterial.COUCH_WOODEN_MIDDLE, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_WOODEN_CORNER(new Couch("Wooden Couch Corner", CustomMaterial.COUCH_WOODEN_CORNER, Type.DYE, CouchPart.CORNER)),
	COUCH_WOODEN_OTTOMAN(new Couch("Wooden Couch Ottoman", CustomMaterial.COUCH_WOODEN_OTTOMAN, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_MODERN_END_LEFT(new Couch("Modern Couch Left End", CustomMaterial.COUCH_MODERN_END_LEFT, Type.DYE, CouchPart.END)),
	COUCH_MODERN_END_RIGHT(new Couch("Modern Couch Left Right", CustomMaterial.COUCH_MODERN_END_RIGHT, Type.DYE, CouchPart.END)),
	COUCH_MODERN_MIDDLE(new Couch("Modern Couch Middle", CustomMaterial.COUCH_MODERN_MIDDLE, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_MODERN_CORNER(new Couch("Modern Couch Corner", CustomMaterial.COUCH_MODERN_CORNER, Type.DYE, CouchPart.CORNER)),
	COUCH_MODERN_OTTOMAN(new Couch("Modern Couch Ottoman", CustomMaterial.COUCH_MODERN_OTTOMAN, Type.DYE, CouchPart.STRAIGHT)),
	// Blocks
	DYE_STATION(new Block("Dye Station", CustomMaterial.DYE_STATION)),
	TRASH_CAN(new RotatableBlock("Trash Can", CustomMaterial.TRASH_CAN)),
	// Fireplaces
	FIREPLACE_DARK(new Fireplace("Dark Fireplace", CustomMaterial.FIREPLACE_DARK)),
	FIREPLACE_DARK_XMAS(new Fireplace("Dark XMas Fireplace", CustomMaterial.FIREPLACE_DARK_XMAS)),
	FIREPLACE_BROWN(new Fireplace("Brown Fireplace", CustomMaterial.FIREPLACE_BROWN)),
	FIREPLACE_BROWN_XMAS(new Fireplace("Brown XMas Fireplace", CustomMaterial.FIREPLACE_BROWN_XMAS)),
	FIREPLACE_LIGHT(new Fireplace("Light Fireplace", CustomMaterial.FIREPLACE_LIGHT)),
	FIREPLACE_LIGHT_XMAS(new Fireplace("Light XMas Fireplace", CustomMaterial.FIREPLACE_LIGHT_XMAS)),
	// Gravestones
	GRAVESTONE_SMALL(new DecorationConfig("Small Gravestone", CustomMaterial.GRAVESTONE_SMALL)),
	GRAVESTONE_CROSS(new DecorationConfig("Gravestone Cross", CustomMaterial.GRAVESTONE_CROSS, Hitbox.single(Material.IRON_BARS))),
	GRAVESTONE_PLAQUE(new DecorationConfig("Gravestone Plaque", CustomMaterial.GRAVESTONE_PLAQUE)),
	GRAVESTONE_STACK(new DecorationConfig("Rock Stack Gravestone", CustomMaterial.GRAVESTONE_STACK)),
	GRAVESTONE_FLOWERBED(new DecorationConfig("Flowerbed Gravestone", CustomMaterial.GRAVESTONE_FLOWERBED)),
	GRAVESTONE_TALL(new DecorationConfig("Tall Gravestone", CustomMaterial.GRAVESTONE_TALL, List.of(Hitbox.origin(Material.IRON_BARS), Hitbox.offset(Material.IRON_BARS, BlockFace.UP)))),
	// Food
	PIZZA_BOX_SINGLE(new DecorationConfig("Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE)),
	PIZZA_BOX_SINGLE_OPENED(new DecorationConfig("Opened Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE_OPENED)),
	PIZZA_BOX_STACK(new DecorationConfig("Pizza Box Stack", CustomMaterial.FOOD_PIZZA_BOX_STACK)),
	SOUP_MUSHROOM(new DecorationConfig("Mushroom Soup", CustomMaterial.FOOD_SOUP_MUSHROOM)),
	SOUP_BEETROOT(new DecorationConfig("Beetroot Soup", CustomMaterial.FOOD_SOUP_BEETROOT)),
	SOUP_RABBIT(new DecorationConfig("Rabbit Soup", CustomMaterial.FOOD_SOUP_RABBIT)),
	BREAD_LOAF(new DecorationConfig("Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF)),
	BREAD_LOAF_CUT(new DecorationConfig("Cut Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF_CUT)),
	BROWNIES_CHOCOLATE(new DecorationConfig("Chocolate Brownies", CustomMaterial.FOOD_BROWNIES_CHOCOLATE)),
	BROWNIES_VANILLA(new DecorationConfig("Vanilla Brownies", CustomMaterial.FOOD_BROWNIES_VANILLA)),
	COOKIES_CHOCOLATE(new DecorationConfig("Chocolate Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE)),
	COOKIES_CHOCOLATE_CHIP(new DecorationConfig("Chocolate Chip Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE_CHIP)),
	COOKIES_SUGAR(new DecorationConfig("Sugar Cookies", CustomMaterial.FOOD_COOKIES_SUGAR)),
	MILK_AND_COOKIES(new DecorationConfig("Milk and Cookies", CustomMaterial.FOOD_MILK_AND_COOKIES)),
	MUFFINS_CHOCOLATE(new DecorationConfig("Chocolate Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE)),
	MUFFINS_CHOCOLATE_CHIP(new DecorationConfig("Chocolate Chip Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE_CHIP)),
	MUFFINS_LEMON(new DecorationConfig("Lemon Muffins", CustomMaterial.FOOD_MUFFINS_LEMON)),
	DINNER_HAM(new DecorationConfig("Ham Dinner", CustomMaterial.FOOD_DINNER_HAM)),
	DINNER_ROAST(new DecorationConfig("Roast Dinner", CustomMaterial.FOOD_DINNER_ROAST)),
	DINNER_TURKEY(new DecorationConfig("Turkey Dinner", CustomMaterial.FOOD_DINNER_TURKEY)),
	PUNCHBOWL(new Dyeable("Punchbowl", CustomMaterial.FOOD_PUNCHBOWL, Type.DYE)),
	PUNCHBOWL_EGGNOG(new Dyeable("Eggnog", CustomMaterial.FOOD_PUNCHBOWL, Type.DYE, "FFF4BB")),
	SIDE_SAUCE(new Dyeable("Sauce Side", CustomMaterial.FOOD_SIDE_SAUCE, Type.DYE)),
	SIDE_SAUCE_CRANBERRIES(new Dyeable("Cranberries Side", CustomMaterial.FOOD_SIDE_SAUCE, Type.DYE, "C61B1B")),
	SIDE_GREEN_BEAN_CASSEROLE(new DecorationConfig("Green Bean Casserole Side", CustomMaterial.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),
	SIDE_MAC_AND_CHEESE(new DecorationConfig("Mac N' Cheese Side", CustomMaterial.FOOD_SIDE_MAC_AND_CHEESE)),
	SIDE_SWEET_POTATOES(new DecorationConfig("Sweet Potatoes Side", CustomMaterial.FOOD_SIDE_SWEET_POTATOES)),
	SIDE_MASHED_POTATOES(new DecorationConfig("Mashed Potatoes Side", CustomMaterial.FOOD_SIDE_MASHED_POTATOES)),
	SIDE_ROLLS(new DecorationConfig("Rolls", CustomMaterial.FOOD_SIDE_ROLLS)),
	CAKE_BATTER(new Dyeable("Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE)),
	CAKE_BATTER_RED_VELVET(new Dyeable("Red Velvet Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE, "720606")),
	CAKE_BATTER_VANILLA(new Dyeable("Vanilla Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE, "FFF9CC")),
	CAKE_BATTER_CHOCOLATE(new Dyeable("Chocolate Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE, "492804")),
	CAKE_WHITE_CHOCOLATE(new DecorationConfig("White Chocolate Cake", CustomMaterial.FOOD_CAKE_WHITE_CHOCOLATE)),
	CAKE_BUNDT(new DecorationConfig("Bundt Cake", CustomMaterial.FOOD_CAKE_BUNDT)),
	CAKE_CHOCOLATE_DRIP(new DecorationConfig("Chocolate Drip Cake", CustomMaterial.FOOD_CAKE_CHOCOLATE_DRIP)),
	PIE_ROUGH(new Dyeable("Rough Pie", CustomMaterial.FOOD_PIE_ROUGH, Type.DYE)),
	PIE_ROUGH_PECAN(new Dyeable("Pecan Pie", CustomMaterial.FOOD_PIE_ROUGH, Type.DYE, "4E3004")),
	PIE_SMOOTH(new Dyeable("Smooth Pie", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE)),
	PIE_SMOOTH_CHOCOLATE(new Dyeable("Chocolate Pie", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE, "734008")),
	PIE_SMOOTH_LEMON(new Dyeable("Lemon Pie", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE, "FFE050")),
	PIE_SMOOTH_PUMPKIN(new Dyeable("Pumpkin Pie Decoration", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE, "BF7D18")),
	PIE_LATTICED(new Dyeable("Latticed Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE)),
	PIE_LATTICED_APPLE(new Dyeable("Apple Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE, "FDC330")),
	PIE_LATTICED_BLUEBERRY(new Dyeable("Blueberry Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE, "4E1892")),
	PIE_LATTICED_CHERRY(new Dyeable("Cherry Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE, "B60C0C")),

	// Kitchenware
	WINE_BOTTLE(new DecorationConfig("Wine Bottle", CustomMaterial.KITCHENWARE_WINE_BOTTLE)),
	WINE_BOTTLE_GROUP(new DecorationConfig("Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP)),
	WINE_BOTTLE_GROUP_RANDOM(new DecorationConfig("Random Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM)),
	WINE_BOTTLE_GROUP_SIDE(new DecorationConfig("Wine Bottles on Side", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_SIDE)),
	WINE_GLASS(new DecorationConfig("Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS)),
	WINE_GLASS_FULL(new DecorationConfig("Full Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS_FULL)),
	MUG_GLASS(new DecorationConfig("Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS)),
	MUG_GLASS_FULL(new DecorationConfig("Full Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS_FULL)),
	MUG_WOODEN(new DecorationConfig("Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN)),
	MUG_WOODEN_FULL(new DecorationConfig("Full Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN_FULL)),
	GLASSWARE_GROUP_1(new DecorationConfig("Random Glassware 1", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_1)),
	GLASSWARE_GROUP_2(new DecorationConfig("Random Glassware 2", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_2)),
	GLASSWARE_GROUP_3(new DecorationConfig("Random Glassware 3", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_3)),
	JAR(new DecorationConfig("Jar", CustomMaterial.KITCHENWARE_JAR)),
	JAR_HONEY(new DecorationConfig("Honey Jar", CustomMaterial.KITCHENWARE_JAR_HONEY)),
	JAR_COOKIES(new DecorationConfig("Cookie Jar", CustomMaterial.KITCHENWARE_JAR_COOKIES)),
	JAR_WIDE(new DecorationConfig("Wide Jar", CustomMaterial.KITCHENWARE_JAR_WIDE)),
	BOWL(new DecorationConfig("Wooden Bowl", CustomMaterial.KITCHENWARE_BOWL)),
	MIXING_BOWL(new DecorationConfig("Mixing Bowl", CustomMaterial.KITCHENWARE_MIXING_BOWL)),
	PAN_CAKE(new DecorationConfig("Cake Pan", CustomMaterial.KITCHENWARE_PAN_CAKE)),
	PAN_CASSEROLE(new DecorationConfig("Casserole Pan", CustomMaterial.KITCHENWARE_PAN_CASSEROLE)),
	PAN_COOKIE(new DecorationConfig("Cookie Pan", CustomMaterial.KITCHENWARE_PAN_COOKIE)),
	PAN_MUFFIN(new DecorationConfig("Muffin Pan", CustomMaterial.KITCHENWARE_PAN_MUFFIN)),
	PAN_PIE(new DecorationConfig("Pie Pan", CustomMaterial.KITCHENWARE_PAN_PIE)),
	// Potions
	POTION_TINY_1(new Dyeable("Tiny Potions 1", CustomMaterial.POTION_TINY_1, Type.DYE)),
	POTION_TINY_2(new Dyeable("Tiny Potions 2", CustomMaterial.POTION_TINY_2, Type.DYE)),
	POTION_SMALL_1(new Dyeable("Small Potion 1", CustomMaterial.POTION_SMALL_1, Type.DYE)),
	POTION_SMALL_2(new Dyeable("Small Potion 2", CustomMaterial.POTION_SMALL_2, Type.DYE)),
	POTION_SMALL_3(new Dyeable("Small Potion 3", CustomMaterial.POTION_SMALL_3, Type.DYE)),
	POTION_MEDIUM_1(new Dyeable("Medium Potion 1", CustomMaterial.POTION_MEDIUM_1, Type.DYE)),
	POTION_MEDIUM_2(new Dyeable("Medium Potion 2", CustomMaterial.POTION_MEDIUM_2, Type.DYE)),
	POTION_WIDE(new Dyeable("Wide Potion", CustomMaterial.POTION_WIDE, Type.DYE)),
	POTION_SKINNY(new Dyeable("Skinny Potion", CustomMaterial.POTION_SKINNY, Type.DYE)),
	POTION_TALL(new Dyeable("Tall Potion", CustomMaterial.POTION_TALL, Type.DYE)),
	POTION_BIG_BOTTLE(new Dyeable("Big Potion Bottle", CustomMaterial.POTION_BIG_BOTTLE, Type.DYE)),
	POTION_BIG_TEAR(new Dyeable("Big Potion Tear", CustomMaterial.POTION_BIG_TEAR, Type.DYE)),
	POTION_BIG_DONUT(new Dyeable("Big Potion Donut", CustomMaterial.POTION_BIG_DONUT, Type.DYE)),
	POTION_BIG_SKULL(new Dyeable("Big Potion Skull", CustomMaterial.POTION_BIG_SKULL, Type.DYE)),
	POTION_GROUP_SMALL(new Dyeable("Small Potions", CustomMaterial.POTION_GROUP_SMALL, Type.DYE)),
	POTION_GROUP_MEDIUM(new Dyeable("Medium Potions", CustomMaterial.POTION_GROUP_MEDIUM, Type.DYE)),
	POTION_GROUP_TALL(new Dyeable("Tall Potions", CustomMaterial.POTION_GROUP_TALL, Type.DYE)),
	POTION_GROUP_RANDOM_1(new Dyeable("Random Potions 1", CustomMaterial.POTION_GROUP_RANDOM_1, Type.DYE)),
	POTION_GROUP_RANDOM_2(new Dyeable("Random Potions 2", CustomMaterial.POTION_GROUP_RANDOM_2, Type.DYE)),
	POTION_GROUP_RANDOM_3(new Dyeable("Random Potions 3", CustomMaterial.POTION_GROUP_RANDOM_3, Type.DYE)),
	POTION_GROUP_RANDOM_4(new Dyeable("Random Potions 4", CustomMaterial.POTION_GROUP_RANDOM_4, Type.DYE)),
	POTION_GROUP_RANDOM_5(new Dyeable("Random Potions 5", CustomMaterial.POTION_GROUP_RANDOM_5, Type.DYE)),
	POTION_GROUP_RANDOM_6(new Dyeable("Random Potions 6", CustomMaterial.POTION_GROUP_RANDOM_6, Type.DYE)),
	POTION_GROUP_RANDOM_7(new Dyeable("Random Potions 7", CustomMaterial.POTION_GROUP_RANDOM_7, Type.DYE)),
	POTION_GROUP_RANDOM_8(new Dyeable("Random Potions 8", CustomMaterial.POTION_GROUP_RANDOM_8, Type.DYE)),
	// Misc
	INKWELL(new DecorationConfig("Inkwell", CustomMaterial.INKWELL)),
	WHEEL_SMALL(new DecorationConfig("Small Wheel", CustomMaterial.WHEEL_SMALL)),
	;

	@Getter
	private final DecorationConfig config;

	public static void init() {}

}
