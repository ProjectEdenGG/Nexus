package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable.Type;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Block;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.types.RotatableBlock;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.features.resourcepack.decoration.types.TestThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.BirdHouse;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime.WindChimeType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Chair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch.CouchPart;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Stump;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.CeilingThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.DyeableFloorThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.FloorThing;
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
		- middle click creative copy
		- add catalogs menu
		- Mob Plushies
 */

@AllArgsConstructor
public enum DecorationType {
	// Catalog: Christmas / Holiday?
	//	Fireplaces
	FIREPLACE_DARK_XMAS(new Fireplace("Dark XMas Fireplace", CustomMaterial.FIREPLACE_DARK_XMAS)),
	FIREPLACE_BROWN_XMAS(new Fireplace("Brown XMas Fireplace", CustomMaterial.FIREPLACE_BROWN_XMAS)),
	FIREPLACE_LIGHT_XMAS(new Fireplace("Light XMas Fireplace", CustomMaterial.FIREPLACE_LIGHT_XMAS)),
	CHRISTMAS_TREE_COLOR(new FloorThing("Christmas Tree Colored", CustomMaterial.CHRISTMAS_TREE_COLORED)),
	CHRISTMAS_TREE_WHITE(new FloorThing("Christmas Tree White", CustomMaterial.CHRISTMAS_TREE_WHITE)),
	//	TOY_TRAIN(new FloorThing("Toy Train", CustomMaterial.TOY_TRAIN)), // Add as part of a Christmas tree structure
	MISTLETOE(new CeilingThing("Mistletoe", CustomMaterial.MISTLETOE)),
	WREATH(new DecorationConfig("Wreath", CustomMaterial.WREATH)),
	STOCKINGS_SINGLE(new DecorationConfig("Single Stocking", CustomMaterial.STOCKINGS_SINGLE)),
	STOCKINGS_DOUBLE(new DecorationConfig("Double Stocking", CustomMaterial.STOCKINGS_DOUBLE)),
	BUNTING_PHRASE_HAPPY_HOLIDAYS(new DecorationConfig("Happy Holidays Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_HOLIDAYS)),
	BUNTING_PHRASE_HAPPY_NEW_YEAR(new DecorationConfig("Happy New Year Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_NEW_YEAR)),
	BUNTING_PHRASE_MERRY_CHRISTMAS(new DecorationConfig("Merry Christmas Bunting", CustomMaterial.BUNTING_PHRASE_MERRY_CHRISTMAS)),
	SNOWMAN_PLAIN(new FloorThing("Snowman Plain", CustomMaterial.SNOWMAN_PLAIN)),
	SNOWMAN_FANCY(new FloorThing("Snowman Fancy", CustomMaterial.SNOWMAN_FANCY)),
	SNOWBALLS_SMALL(new FloorThing("Snowballs Small Pile", CustomMaterial.SNOWBALLS_SMALL)),
	SNOWBALLS_BIG(new FloorThing("Snowballs Big Pile", CustomMaterial.SNOWBALLS_BIG)),
	ICICLE_LIGHT_CENTER(new DecorationConfig("Icicle Lights Center", CustomMaterial.ICICLE_LIGHT_CENTER)),
	ICICLE_LIGHT_LEFT(new DecorationConfig("Icicle Lights Left", CustomMaterial.ICICLE_LIGHT_LEFT)),
	ICICLE_LIGHT_RIGHT(new DecorationConfig("Icicle Lights Right", CustomMaterial.ICICLE_LIGHT_RIGHT)),

	// Catalog: Halloween / Spooky?
	// 	Gravestones
	GRAVESTONE_SMALL(new FloorThing("Small Gravestone", CustomMaterial.GRAVESTONE_SMALL)),
	GRAVESTONE_CROSS(new FloorThing("Gravestone Cross", CustomMaterial.GRAVESTONE_CROSS, Hitbox.single(Material.IRON_BARS))),
	GRAVESTONE_PLAQUE(new FloorThing("Gravestone Plaque", CustomMaterial.GRAVESTONE_PLAQUE)),
	GRAVESTONE_STACK(new FloorThing("Rock Stack Gravestone", CustomMaterial.GRAVESTONE_STACK)),
	GRAVESTONE_FLOWERBED(new FloorThing("Flowerbed Gravestone", CustomMaterial.GRAVESTONE_FLOWERBED)),
	GRAVESTONE_TALL(new FloorThing("Tall Gravestone", CustomMaterial.GRAVESTONE_TALL, List.of(Hitbox.origin(Material.IRON_BARS), Hitbox.offset(Material.IRON_BARS, BlockFace.UP)))),

	// Catalog: Main / General?
	// 	Tables
	TABLE_WOODEN_1x1(CatalogTab.TABLES, new Table("Wooden Table 1x1", CustomMaterial.TABLE_WOODEN_1X1, Type.STAIN, Table.TableSize._1x1)),
	TABLE_WOODEN_1x2(CatalogTab.TABLES, new Table("Wooden Table 1x2", CustomMaterial.TABLE_WOODEN_1X2, Type.STAIN, Table.TableSize._1x2)),
	TABLE_WOODEN_2x2(CatalogTab.TABLES, new Table("Wooden Table 2x2", CustomMaterial.TABLE_WOODEN_2X2, Type.STAIN, Table.TableSize._2x2)),
	TABLE_WOODEN_2x3(CatalogTab.TABLES, new Table("Wooden Table 2x3", CustomMaterial.TABLE_WOODEN_2X3, Type.STAIN, Table.TableSize._2x3)),
	TABLE_WOODEN_3x3(CatalogTab.TABLES, new Table("Wooden Table 3x3", CustomMaterial.TABLE_WOODEN_3X3, Type.STAIN, Table.TableSize._3x3)),
	// 	Chairs
	CHAIR_WOODEN_BASIC(CatalogTab.CHAIRS, new Chair("Wooden Chair", CustomMaterial.CHAIR_WOODEN_BASIC, Type.STAIN)),
	CHAIR_WOODEN_CUSHIONED(CatalogTab.CHAIRS, new Chair("Cushioned Wooden Chair", CustomMaterial.CHAIR_WOODEN_CUSHIONED, Type.DYE)),
	CHAIR_CLOTH(CatalogTab.CHAIRS, new Chair("Cloth Chair", CustomMaterial.CHAIR_CLOTH, Type.DYE)),
	ADIRONDACK(CatalogTab.CHAIRS, new Chair("Adirondack", CustomMaterial.ADIRONDACK, Type.DYE)),
	// 	Stools
	STOOL_WOODEN_BASIC(CatalogTab.CHAIRS, new Chair("Wooden Stool", CustomMaterial.STOOL_WOODEN_BASIC, Type.STAIN)),
	STOOL_WOODEN_CUSHIONED(CatalogTab.CHAIRS, new Chair("Cushioned Wooden Stool", CustomMaterial.STOOL_WOODEN_CUSHIONED, Type.DYE)),
	STOOL_BAR_WOODEN(CatalogTab.CHAIRS, new Chair("Wooden Bar Stool", CustomMaterial.STOOL_BAR_WOODEN, Type.STAIN, 1.2)),
	STOOL_STUMP_OAK(CatalogTab.CHAIRS, new Stump("Oak Stump", CustomMaterial.STOOL_STUMP_OAK)),
	STOOL_STUMP_OAK_ROOTS(CatalogTab.CHAIRS, new Stump("Rooted Oak Stump", CustomMaterial.STOOL_STUMP_OAK_ROOTS)),
	STOOL_STUMP_SPRUCE(CatalogTab.CHAIRS, new Stump("Spruce Stump", CustomMaterial.STOOL_STUMP_SPRUCE)),
	STOOL_STUMP_SPRUCE_ROOTS(CatalogTab.CHAIRS, new Stump("Rooted Spruce Stump", CustomMaterial.STOOL_STUMP_SPRUCE_ROOTS)),
	STOOL_STUMP_BIRCH(CatalogTab.CHAIRS, new Stump("Birch Stump", CustomMaterial.STOOL_STUMP_BIRCH)),
	STOOL_STUMP_BIRCH_ROOTS(CatalogTab.CHAIRS, new Stump("Rooted Birch Stump", CustomMaterial.STOOL_STUMP_BIRCH_ROOTS)),
	STOOL_STUMP_JUNGLE(CatalogTab.CHAIRS, new Stump("Jungle Stump", CustomMaterial.STOOL_STUMP_JUNGLE)),
	STOOL_STUMP_JUNGLE_ROOTS(CatalogTab.CHAIRS, new Stump("Rooted Jungle Stump", CustomMaterial.STOOL_STUMP_JUNGLE_ROOTS)),
	STOOL_STUMP_ACACIA(CatalogTab.CHAIRS, new Stump("Acacia Stump", CustomMaterial.STOOL_STUMP_ACACIA)),
	STOOL_STUMP_ACACIA_ROOTS(CatalogTab.CHAIRS, new Stump("Rooted Acacia Stump", CustomMaterial.STOOL_STUMP_ACACIA_ROOTS)),
	STOOL_STUMP_DARK_OAK(CatalogTab.CHAIRS, new Stump("Dark Oak Stump", CustomMaterial.STOOL_STUMP_DARK_OAK)),
	STOOL_STUMP_DARK_OAK_ROOTS(CatalogTab.CHAIRS, new Stump("Rooted Dark Oak Stump", CustomMaterial.STOOL_STUMP_DARK_OAK_ROOTS)),
	STOOL_STUMP_MANGROVE(CatalogTab.CHAIRS, new Stump("Mangrove Stump", CustomMaterial.STOOL_STUMP_MANGROVE)),
	STOOL_STUMP_MANGROVE_ROOTS(CatalogTab.CHAIRS, new Stump("Rooted Mangrove Stump", CustomMaterial.STOOL_STUMP_MANGROVE_ROOTS)),
	// 	Benches
	BENCH_WOODEN(CatalogTab.CHAIRS, new Bench("Wooden Bench", CustomMaterial.BENCH_WOODEN, Type.STAIN)),
	// 	Couches
	COUCH_WOODEN_CUSHIONED_END_LEFT(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Left End", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_LEFT, Type.DYE, CouchPart.END)),
	COUCH_WOODEN_CUSHIONED_END_RIGHT(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Left Right", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_RIGHT, Type.DYE, CouchPart.END)),
	COUCH_WOODEN_CUSHIONED_MIDDLE(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Middle", CustomMaterial.COUCH_WOODEN_CUSHIONED_MIDDLE, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_WOODEN_CUSHIONED_CORNER(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Corner", CustomMaterial.COUCH_WOODEN_CUSHIONED_CORNER, Type.DYE, CouchPart.CORNER)),
	COUCH_WOODEN_CUSHIONED_OTTOMAN(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Ottoman", CustomMaterial.COUCH_WOODEN_CUSHIONED_OTTOMAN, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_CLOTH_END_LEFT(CatalogTab.CHAIRS, new Couch("Cloth Couch Left End", CustomMaterial.COUCH_CLOTH_END_LEFT, Type.DYE, CouchPart.END)),
	COUCH_CLOTH_END_RIGHT(CatalogTab.CHAIRS, new Couch("Cloth Couch Left Right", CustomMaterial.COUCH_CLOTH_END_RIGHT, Type.DYE, CouchPart.END)),
	COUCH_CLOTH_MIDDLE(CatalogTab.CHAIRS, new Couch("Cloth Couch Middle", CustomMaterial.COUCH_CLOTH_MIDDLE, Type.DYE, CouchPart.STRAIGHT)),
	COUCH_CLOTH_CORNER(CatalogTab.CHAIRS, new Couch("Cloth Couch Corner", CustomMaterial.COUCH_CLOTH_CORNER, Type.DYE, CouchPart.CORNER)),
	COUCH_CLOTH_OTTOMAN(CatalogTab.CHAIRS, new Couch("Cloth Couch Ottoman", CustomMaterial.COUCH_CLOTH_OTTOMAN, Type.DYE, CouchPart.STRAIGHT)),
	// 	Blocks
	DYE_STATION(CatalogTab.NONE, new Block("Dye Station", CustomMaterial.DYE_STATION)),
	TRASH_CAN(new RotatableBlock("Trash Can", CustomMaterial.TRASH_CAN)),
	// 	Fireplaces
	FIREPLACE_DARK(new Fireplace("Dark Fireplace", CustomMaterial.FIREPLACE_DARK)),
	FIREPLACE_BROWN(new Fireplace("Brown Fireplace", CustomMaterial.FIREPLACE_BROWN)),
	FIREPLACE_LIGHT(new Fireplace("Light Fireplace", CustomMaterial.FIREPLACE_LIGHT)),
	//	Windchimes
	WINDCHIME_IRON(new WindChime("Iron Windchimes", WindChimeType.IRON)),
	WINDCHIME_GOLD(new WindChime("Gold Windchimes", WindChimeType.GOLD)),
	WINDCHIME_COPPER(new WindChime("Copper Windchimes", WindChimeType.COPPER)),
	WINDCHIME_AMETHYST(new WindChime("Amethyst Windchimes", WindChimeType.AMETHYST)),
	WINDCHIME_LAPIS(new WindChime("Lapis Windchimes", WindChimeType.LAPIS)),
	WINDCHIME_NETHERITE(new WindChime("Netherite Windchimes", WindChimeType.NETHERITE)),
	WINDCHIME_DIAMOND(new WindChime("Diamond Windchimes", WindChimeType.DIAMOND)),
	WINDCHIME_REDSTONE(new WindChime("Redstone Windchimes", WindChimeType.REDSTONE)),
	WINDCHIME_EMERALD(new WindChime("Emerald Windchimes", WindChimeType.EMERALD)),
	WINDCHIME_QUARTZ(new WindChime("Quartz Windchimes", WindChimeType.QUARTZ)),
	WINDCHIME_COAL(new WindChime("Coal Windchimes", WindChimeType.COAL)),
	WINDCHIME_ICE(new WindChime("Ice Windchimes", WindChimeType.ICE)),
	// 	Birdhouses
	BIRDHOUSE_FOREST_HORIZONTAL(new BirdHouse("Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL)),
	BIRDHOUSE_FOREST_VERTICAL(new BirdHouse("Vertical Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_VERTICAL)),
	BIRDHOUSE_FOREST_HANGING(new BirdHouse("Hanging Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HANGING)),
	BIRDHOUSE_ENCHANTED_HORIZONTAL(new BirdHouse("Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HORIZONTAL)),
	BIRDHOUSE_ENCHANTED_VERTICAL(new BirdHouse("Vertical Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_VERTICAL)),
	BIRDHOUSE_ENCHANTED_HANGING(new BirdHouse("Hanging Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HANGING)),
	BIRDHOUSE_DEPTHS_HORIZONTAL(new BirdHouse("Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HORIZONTAL)),
	BIRDHOUSE_DEPTHS_VERTICAL(new BirdHouse("Vertical Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_VERTICAL)),
	BIRDHOUSE_DEPTHS_HANGING(new BirdHouse("Hanging Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HANGING)),
	//	Food
	PIZZA_BOX_SINGLE(CatalogTab.FOOD, new FloorThing("Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE)),
	PIZZA_BOX_SINGLE_OPENED(CatalogTab.FOOD, new FloorThing("Opened Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE_OPENED)),
	PIZZA_BOX_STACK(CatalogTab.FOOD, new FloorThing("Pizza Box Stack", CustomMaterial.FOOD_PIZZA_BOX_STACK)),
	SOUP_MUSHROOM(CatalogTab.FOOD, new FloorThing("Mushroom Soup", CustomMaterial.FOOD_SOUP_MUSHROOM)),
	SOUP_BEETROOT(CatalogTab.FOOD, new FloorThing("Beetroot Soup", CustomMaterial.FOOD_SOUP_BEETROOT)),
	SOUP_RABBIT(CatalogTab.FOOD, new FloorThing("Rabbit Soup", CustomMaterial.FOOD_SOUP_RABBIT)),
	BREAD_LOAF(CatalogTab.FOOD, new FloorThing("Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF)),
	BREAD_LOAF_CUT(CatalogTab.FOOD, new FloorThing("Cut Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF_CUT)),
	BROWNIES_CHOCOLATE(CatalogTab.FOOD, new FloorThing("Chocolate Brownies", CustomMaterial.FOOD_BROWNIES_CHOCOLATE)),
	BROWNIES_VANILLA(CatalogTab.FOOD, new FloorThing("Vanilla Brownies", CustomMaterial.FOOD_BROWNIES_VANILLA)),
	COOKIES_CHOCOLATE(CatalogTab.FOOD, new FloorThing("Chocolate Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE)),
	COOKIES_CHOCOLATE_CHIP(CatalogTab.FOOD, new FloorThing("Chocolate Chip Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE_CHIP)),
	COOKIES_SUGAR(CatalogTab.FOOD, new FloorThing("Sugar Cookies", CustomMaterial.FOOD_COOKIES_SUGAR)),
	MILK_AND_COOKIES(CatalogTab.FOOD, new FloorThing("Milk and Cookies", CustomMaterial.FOOD_MILK_AND_COOKIES)),
	MUFFINS_CHOCOLATE(CatalogTab.FOOD, new FloorThing("Chocolate Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE)),
	MUFFINS_CHOCOLATE_CHIP(CatalogTab.FOOD, new FloorThing("Chocolate Chip Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE_CHIP)),
	MUFFINS_LEMON(CatalogTab.FOOD, new FloorThing("Lemon Muffins", CustomMaterial.FOOD_MUFFINS_LEMON)),
	DINNER_HAM(CatalogTab.FOOD, new FloorThing("Ham Dinner", CustomMaterial.FOOD_DINNER_HAM)),
	DINNER_ROAST(CatalogTab.FOOD, new FloorThing("Roast Dinner", CustomMaterial.FOOD_DINNER_ROAST)),
	DINNER_TURKEY(CatalogTab.FOOD, new FloorThing("Turkey Dinner", CustomMaterial.FOOD_DINNER_TURKEY)),
	PUNCHBOWL(CatalogTab.FOOD, new DyeableFloorThing("Punchbowl", CustomMaterial.FOOD_PUNCHBOWL, Type.DYE)),
	PUNCHBOWL_EGGNOG(CatalogTab.FOOD, new DyeableFloorThing("Eggnog", CustomMaterial.FOOD_PUNCHBOWL, Type.DYE, "FFF4BB")),
	SIDE_SAUCE(CatalogTab.FOOD, new DyeableFloorThing("Sauce Side", CustomMaterial.FOOD_SIDE_SAUCE, Type.DYE)),
	SIDE_SAUCE_CRANBERRIES(CatalogTab.FOOD, new DyeableFloorThing("Cranberries Side", CustomMaterial.FOOD_SIDE_SAUCE, Type.DYE, "C61B1B")),
	SIDE_GREEN_BEAN_CASSEROLE(CatalogTab.FOOD, new FloorThing("Green Bean Casserole Side", CustomMaterial.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),
	SIDE_MAC_AND_CHEESE(CatalogTab.FOOD, new FloorThing("Mac N' Cheese Side", CustomMaterial.FOOD_SIDE_MAC_AND_CHEESE)),
	SIDE_SWEET_POTATOES(CatalogTab.FOOD, new FloorThing("Sweet Potatoes Side", CustomMaterial.FOOD_SIDE_SWEET_POTATOES)),
	SIDE_MASHED_POTATOES(CatalogTab.FOOD, new FloorThing("Mashed Potatoes Side", CustomMaterial.FOOD_SIDE_MASHED_POTATOES)),
	SIDE_ROLLS(CatalogTab.FOOD, new FloorThing("Rolls", CustomMaterial.FOOD_SIDE_ROLLS)),
	CAKE_BATTER(CatalogTab.FOOD, new DyeableFloorThing("Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE)),
	CAKE_BATTER_RED_VELVET(CatalogTab.FOOD, new DyeableFloorThing("Red Velvet Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE, "720606")),
	CAKE_BATTER_VANILLA(CatalogTab.FOOD, new DyeableFloorThing("Vanilla Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE, "FFF9CC")),
	CAKE_BATTER_CHOCOLATE(CatalogTab.FOOD, new DyeableFloorThing("Chocolate Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, Type.DYE, "492804")),
	CAKE_WHITE_CHOCOLATE(CatalogTab.FOOD, new FloorThing("White Chocolate Cake", CustomMaterial.FOOD_CAKE_WHITE_CHOCOLATE)),
	CAKE_BUNDT(CatalogTab.FOOD, new FloorThing("Bundt Cake", CustomMaterial.FOOD_CAKE_BUNDT)),
	CAKE_CHOCOLATE_DRIP(CatalogTab.FOOD, new FloorThing("Chocolate Drip Cake", CustomMaterial.FOOD_CAKE_CHOCOLATE_DRIP)),
	PIE_ROUGH(CatalogTab.FOOD, new DyeableFloorThing("Rough Pie", CustomMaterial.FOOD_PIE_ROUGH, Type.DYE)),
	PIE_ROUGH_PECAN(CatalogTab.FOOD, new DyeableFloorThing("Pecan Pie", CustomMaterial.FOOD_PIE_ROUGH, Type.DYE, "4E3004")),
	PIE_SMOOTH(CatalogTab.FOOD, new DyeableFloorThing("Smooth Pie", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE)),
	PIE_SMOOTH_CHOCOLATE(CatalogTab.FOOD, new DyeableFloorThing("Chocolate Pie", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE, "734008")),
	PIE_SMOOTH_LEMON(CatalogTab.FOOD, new DyeableFloorThing("Lemon Pie", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE, "FFE050")),
	PIE_SMOOTH_PUMPKIN(CatalogTab.FOOD, new DyeableFloorThing("Pumpkin Pie Decoration", CustomMaterial.FOOD_PIE_SMOOTH, Type.DYE, "BF7D18")),
	PIE_LATTICED(CatalogTab.FOOD, new DyeableFloorThing("Latticed Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE)),
	PIE_LATTICED_APPLE(CatalogTab.FOOD, new DyeableFloorThing("Apple Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE, "FDC330")),
	PIE_LATTICED_BLUEBERRY(CatalogTab.FOOD, new DyeableFloorThing("Blueberry Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE, "4E1892")),
	PIE_LATTICED_CHERRY(CatalogTab.FOOD, new DyeableFloorThing("Cherry Pie", CustomMaterial.FOOD_PIE_LATTICED, Type.DYE, "B60C0C")),
	//	Kitchenware
	WINE_BOTTLE(CatalogTab.KITCHENWARE, new FloorThing("Wine Bottle", CustomMaterial.KITCHENWARE_WINE_BOTTLE)),
	WINE_BOTTLE_GROUP(CatalogTab.KITCHENWARE, new FloorThing("Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP)),
	WINE_BOTTLE_GROUP_RANDOM(CatalogTab.KITCHENWARE, new FloorThing("Random Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM)),
	WINE_BOTTLE_GROUP_SIDE(CatalogTab.KITCHENWARE, new FloorThing("Wine Bottles on Side", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_SIDE)),
	WINE_GLASS(CatalogTab.KITCHENWARE, new FloorThing("Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS)),
	WINE_GLASS_FULL(CatalogTab.KITCHENWARE, new FloorThing("Full Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS_FULL)),
	MUG_GLASS(CatalogTab.KITCHENWARE, new FloorThing("Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS)),
	MUG_GLASS_FULL(CatalogTab.KITCHENWARE, new FloorThing("Full Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS_FULL)),
	MUG_WOODEN(CatalogTab.KITCHENWARE, new FloorThing("Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN)),
	MUG_WOODEN_FULL(CatalogTab.KITCHENWARE, new FloorThing("Full Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN_FULL)),
	GLASSWARE_GROUP_1(CatalogTab.KITCHENWARE, new FloorThing("Random Glassware 1", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_1)),
	GLASSWARE_GROUP_2(CatalogTab.KITCHENWARE, new FloorThing("Random Glassware 2", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_2)),
	GLASSWARE_GROUP_3(CatalogTab.KITCHENWARE, new FloorThing("Random Glassware 3", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_3)),
	JAR(CatalogTab.KITCHENWARE, new FloorThing("Jar", CustomMaterial.KITCHENWARE_JAR)),
	JAR_HONEY(CatalogTab.KITCHENWARE, new FloorThing("Honey Jar", CustomMaterial.KITCHENWARE_JAR_HONEY)),
	JAR_COOKIES(CatalogTab.KITCHENWARE, new FloorThing("Cookie Jar", CustomMaterial.KITCHENWARE_JAR_COOKIES)),
	JAR_WIDE(CatalogTab.KITCHENWARE, new FloorThing("Wide Jar", CustomMaterial.KITCHENWARE_JAR_WIDE)),
	BOWL(CatalogTab.KITCHENWARE, new FloorThing("Wooden Bowl", CustomMaterial.KITCHENWARE_BOWL)),
	MIXING_BOWL(CatalogTab.KITCHENWARE, new FloorThing("Mixing Bowl", CustomMaterial.KITCHENWARE_MIXING_BOWL)),
	PAN_CAKE(CatalogTab.KITCHENWARE, new FloorThing("Cake Pan", CustomMaterial.KITCHENWARE_PAN_CAKE)),
	PAN_CASSEROLE(CatalogTab.KITCHENWARE, new FloorThing("Casserole Pan", CustomMaterial.KITCHENWARE_PAN_CASSEROLE)),
	PAN_COOKIE(CatalogTab.KITCHENWARE, new FloorThing("Cookie Pan", CustomMaterial.KITCHENWARE_PAN_COOKIE)),
	PAN_MUFFIN(CatalogTab.KITCHENWARE, new FloorThing("Muffin Pan", CustomMaterial.KITCHENWARE_PAN_MUFFIN)),
	PAN_PIE(CatalogTab.KITCHENWARE, new FloorThing("Pie Pan", CustomMaterial.KITCHENWARE_PAN_PIE)),
	//	Potions
	POTION_FILLED_TINY_1(CatalogTab.POTIONS, new DyeableFloorThing("Tiny Potions 1", CustomMaterial.POTION_FILLED_TINY_1, Type.DYE)),
	POTION_FILLED_TINY_2(CatalogTab.POTIONS, new DyeableFloorThing("Tiny Potions 2", CustomMaterial.POTION_FILLED_TINY_2, Type.DYE)),
	POTION_FILLED_SMALL_1(CatalogTab.POTIONS, new DyeableFloorThing("Small Potion 1", CustomMaterial.POTION_FILLED_SMALL_1, Type.DYE)),
	POTION_FILLED_SMALL_2(CatalogTab.POTIONS, new DyeableFloorThing("Small Potion 2", CustomMaterial.POTION_FILLED_SMALL_2, Type.DYE)),
	POTION_FILLED_SMALL_3(CatalogTab.POTIONS, new DyeableFloorThing("Small Potion 3", CustomMaterial.POTION_FILLED_SMALL_3, Type.DYE)),
	POTION_FILLED_MEDIUM_1(CatalogTab.POTIONS, new DyeableFloorThing("Medium Potion 1", CustomMaterial.POTION_FILLED_MEDIUM_1, Type.DYE)),
	POTION_FILLED_MEDIUM_2(CatalogTab.POTIONS, new DyeableFloorThing("Medium Potion 2", CustomMaterial.POTION_FILLED_MEDIUM_2, Type.DYE)),
	POTION_FILLED_WIDE(CatalogTab.POTIONS, new DyeableFloorThing("Wide Potion", CustomMaterial.POTION_FILLED_WIDE, Type.DYE)),
	POTION_FILLED_SKINNY(CatalogTab.POTIONS, new DyeableFloorThing("Skinny Potion", CustomMaterial.POTION_FILLED_SKINNY, Type.DYE)),
	POTION_FILLED_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Tall Potion", CustomMaterial.POTION_FILLED_TALL, Type.DYE)),
	POTION_FILLED_BIG_BOTTLE(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Bottle", CustomMaterial.POTION_FILLED_BIG_BOTTLE, Type.DYE)),
	POTION_FILLED_BIG_TEAR(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Tear", CustomMaterial.POTION_FILLED_BIG_TEAR, Type.DYE)),
	POTION_FILLED_BIG_DONUT(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Donut", CustomMaterial.POTION_FILLED_BIG_DONUT, Type.DYE)),
	POTION_FILLED_BIG_SKULL(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Skull", CustomMaterial.POTION_FILLED_BIG_SKULL, Type.DYE)),
	POTION_FILLED_GROUP_SMALL(CatalogTab.POTIONS, new DyeableFloorThing("Small Potions", CustomMaterial.POTION_FILLED_GROUP_SMALL, Type.DYE)),
	POTION_FILLED_GROUP_MEDIUM(CatalogTab.POTIONS, new DyeableFloorThing("Medium Potions", CustomMaterial.POTION_FILLED_GROUP_MEDIUM, Type.DYE)),
	POTION_FILLED_GROUP_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Tall Potions", CustomMaterial.POTION_FILLED_GROUP_TALL, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_1(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 1", CustomMaterial.POTION_FILLED_GROUP_RANDOM_1, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_2(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 2", CustomMaterial.POTION_FILLED_GROUP_RANDOM_2, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_3(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 3", CustomMaterial.POTION_FILLED_GROUP_RANDOM_3, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_4(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 4", CustomMaterial.POTION_FILLED_GROUP_RANDOM_4, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_5(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 5", CustomMaterial.POTION_FILLED_GROUP_RANDOM_5, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_6(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 6", CustomMaterial.POTION_FILLED_GROUP_RANDOM_6, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_7(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 7", CustomMaterial.POTION_FILLED_GROUP_RANDOM_7, Type.DYE)),
	POTION_FILLED_GROUP_RANDOM_8(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 8", CustomMaterial.POTION_FILLED_GROUP_RANDOM_8, Type.DYE)),

	POTION_EMPTY_SMALL_1(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potion 1", CustomMaterial.POTION_EMPTY_SMALL_1, Type.DYE)),
	POTION_EMPTY_SMALL_2(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potion 2", CustomMaterial.POTION_EMPTY_SMALL_2, Type.DYE)),
	POTION_EMPTY_SMALL_3(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potion 3", CustomMaterial.POTION_EMPTY_SMALL_3, Type.DYE)),
	POTION_EMPTY_MEDIUM_1(CatalogTab.POTIONS, new DyeableFloorThing("Empty Medium Potion 1", CustomMaterial.POTION_EMPTY_MEDIUM_1, Type.DYE)),
	POTION_EMPTY_MEDIUM_2(CatalogTab.POTIONS, new DyeableFloorThing("Empty Medium Potion 2", CustomMaterial.POTION_EMPTY_MEDIUM_2, Type.DYE)),
	POTION_EMPTY_WIDE(CatalogTab.POTIONS, new DyeableFloorThing("Empty Wide Potion", CustomMaterial.POTION_EMPTY_WIDE, Type.DYE)),
	POTION_EMPTY_SKINNY(CatalogTab.POTIONS, new DyeableFloorThing("Empty Skinny Potion", CustomMaterial.POTION_EMPTY_SKINNY, Type.DYE)),
	POTION_EMPTY_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Tall Potion", CustomMaterial.POTION_EMPTY_TALL, Type.DYE)),
	POTION_EMPTY_BIG_BOTTLE(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Bottle", CustomMaterial.POTION_EMPTY_BIG_BOTTLE, Type.DYE)),
	POTION_EMPTY_BIG_TEAR(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Tear", CustomMaterial.POTION_EMPTY_BIG_TEAR, Type.DYE)),
	POTION_EMPTY_BIG_DONUT(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Donut", CustomMaterial.POTION_EMPTY_BIG_DONUT, Type.DYE)),
	POTION_EMPTY_BIG_SKULL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Skull", CustomMaterial.POTION_EMPTY_BIG_SKULL, Type.DYE)),
	POTION_EMPTY_GROUP_SMALL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potions", CustomMaterial.POTION_EMPTY_GROUP_SMALL, Type.DYE)),
	POTION_EMPTY_GROUP_MEDIUM(CatalogTab.POTIONS, new DyeableFloorThing("Empty Medium Potions", CustomMaterial.POTION_EMPTY_GROUP_MEDIUM, Type.DYE)),
	POTION_EMPTY_GROUP_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Tall Potions", CustomMaterial.POTION_EMPTY_GROUP_TALL, Type.DYE)),

	// 	Balloons
	BALLOON_SHORT(new FloorThing("Balloon Short", CustomMaterial.BALLOON_SHORT)),
	BALLOON_MEDIUM(new FloorThing("Balloon Medium", CustomMaterial.BALLOON_MEDIUM)),
	BALLOON_TALL(new FloorThing("Balloon Tall ", CustomMaterial.BALLOON_TALL)),
	//	Misc
	INKWELL(new FloorThing("Inkwell", CustomMaterial.INKWELL)),
	WHEEL_SMALL(new DecorationConfig("Small Wheel", CustomMaterial.WHEEL_SMALL)),
	TELESCOPE(new FloorThing("Telescope", CustomMaterial.TELESCOPE)),
	MICROSCOPE(new FloorThing("Microscope", CustomMaterial.MICROSCOPE)),
	MICROSCOPE_WITH_GEM(new FloorThing("Microscope With Gem", CustomMaterial.MICROSCOPE_WITH_GEM)),
	HELM(new DecorationConfig("Helm", CustomMaterial.HELM)),
	TRAFFIC_BLOCKADE(new FloorThing("Traffic Blockade", CustomMaterial.TRAFFIC_BLOCKADE)),
	TRAFFIC_BLOCKADE_LIGHTS(new FloorThing("Traffic Blockade with Lights", CustomMaterial.TRAFFIC_BLOCKADE_LIGHTS)),
	TRAFFIC_CONE(new FloorThing("Traffic Cone", CustomMaterial.TRAFFIC_CONE)),
	POSTBOX(new FloorThing("Postbox", CustomMaterial.POSTBOX)),
	SANDWICH_SIGN(new FloorThing("Sandwich Sign", CustomMaterial.SANDWICH_SIGN)),
	SANDWICH_SIGN_TALL(new FloorThing("Sandwhich Sign Tall", CustomMaterial.SANDWICH_SIGN_TALL)),
	FIRE_HYDRANT(new FloorThing("Fire Hydrant", CustomMaterial.FIRE_HYDRANT)),
	WAYSTONE(new FloorThing("Waystone", CustomMaterial.WAYSTONE)),
	WAYSTONE_ACTIVATED(new FloorThing("Waystone Activated", CustomMaterial.WAYSTONE_ACTIVATED)),

	// Testing
	TEST(new TestThing("Test Thing", CustomMaterial.WAYSTONE_ACTIVATED));

	@Getter
	private final CatalogTab tab;
	@Getter
	private final DecorationConfig config;

	DecorationType(DecorationConfig config) {
		this.config = config;
		this.tab = CatalogTab.MISC;
	}

	public static void init() {}

}
