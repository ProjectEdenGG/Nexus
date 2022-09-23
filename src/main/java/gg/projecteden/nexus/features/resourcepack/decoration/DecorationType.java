package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable.ColorableType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art.ArtSize;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet.CabinetType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fridge;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fridge.FridgeSize;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Table;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.BirdHouse;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime;
import gg.projecteden.nexus.features.resourcepack.decoration.types.craftable.WindChime.WindChimeType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Bench;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Chair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Couch.CouchPart;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.LongChair;
import gg.projecteden.nexus.features.resourcepack.decoration.types.seats.Stump;
import gg.projecteden.nexus.features.resourcepack.decoration.types.special.TestThing;
import gg.projecteden.nexus.features.resourcepack.decoration.types.surfaces.Block;
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
	TABLE_WOODEN_1x1(CatalogTab.TABLES, new Table("Wooden Table 1x1", CustomMaterial.TABLE_WOODEN_1X1, Table.TableSize._1x1)),
	TABLE_WOODEN_1x2(CatalogTab.TABLES, new Table("Wooden Table 1x2", CustomMaterial.TABLE_WOODEN_1X2, Table.TableSize._1x2)),
	TABLE_WOODEN_2x2(CatalogTab.TABLES, new Table("Wooden Table 2x2", CustomMaterial.TABLE_WOODEN_2X2, Table.TableSize._2x2)),
	TABLE_WOODEN_2x3(CatalogTab.TABLES, new Table("Wooden Table 2x3", CustomMaterial.TABLE_WOODEN_2X3, Table.TableSize._2x3)),
	TABLE_WOODEN_3x3(CatalogTab.TABLES, new Table("Wooden Table 3x3", CustomMaterial.TABLE_WOODEN_3X3, Table.TableSize._3x3)),
	// 	Chairs
	CHAIR_WOODEN_BASIC(CatalogTab.CHAIRS, new Chair("Wooden Chair", CustomMaterial.CHAIR_WOODEN_BASIC, ColorableType.STAIN)),
	CHAIR_WOODEN_CUSHIONED(CatalogTab.CHAIRS, new Chair("Cushioned Wooden Chair", CustomMaterial.CHAIR_WOODEN_CUSHIONED, ColorableType.DYE)),
	CHAIR_CLOTH(CatalogTab.CHAIRS, new Chair("Cloth Chair", CustomMaterial.CHAIR_CLOTH, ColorableType.DYE)),
	ADIRONDACK(CatalogTab.CHAIRS, new Chair("Adirondack", CustomMaterial.ADIRONDACK, ColorableType.DYE)),
	CHAIR_BEACH(CatalogTab.CHAIRS, new LongChair("Beach Chair", CustomMaterial.BEACH_CHAIR, ColorableType.DYE, Hitbox.light(), .675)),
	// 	Stools
	STOOL_WOODEN_BASIC(CatalogTab.CHAIRS, new Chair("Wooden Stool", CustomMaterial.STOOL_WOODEN_BASIC, ColorableType.STAIN)),
	STOOL_WOODEN_CUSHIONED(CatalogTab.CHAIRS, new Chair("Cushioned Wooden Stool", CustomMaterial.STOOL_WOODEN_CUSHIONED, ColorableType.DYE)),
	STOOL_BAR_WOODEN(CatalogTab.CHAIRS, new Chair("Wooden Bar Stool", CustomMaterial.STOOL_BAR_WOODEN, ColorableType.STAIN, 1.2)),
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
	BENCH_WOODEN(CatalogTab.CHAIRS, new Bench("Wooden Bench", CustomMaterial.BENCH_WOODEN, ColorableType.STAIN)),
	// 	Couches
	COUCH_WOODEN_CUSHIONED_END_LEFT(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Left End", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_LEFT, ColorableType.DYE, CouchPart.END)),
	COUCH_WOODEN_CUSHIONED_END_RIGHT(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Left Right", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_RIGHT, ColorableType.DYE, CouchPart.END)),
	COUCH_WOODEN_CUSHIONED_MIDDLE(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Middle", CustomMaterial.COUCH_WOODEN_CUSHIONED_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),
	COUCH_WOODEN_CUSHIONED_CORNER(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Corner", CustomMaterial.COUCH_WOODEN_CUSHIONED_CORNER, ColorableType.DYE, CouchPart.CORNER)),
	COUCH_WOODEN_CUSHIONED_OTTOMAN(CatalogTab.CHAIRS, new Couch("Cushioned Wooden Couch Ottoman", CustomMaterial.COUCH_WOODEN_CUSHIONED_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),
	COUCH_CLOTH_END_LEFT(CatalogTab.CHAIRS, new Couch("Cloth Couch Left End", CustomMaterial.COUCH_CLOTH_END_LEFT, ColorableType.DYE, CouchPart.END)),
	COUCH_CLOTH_END_RIGHT(CatalogTab.CHAIRS, new Couch("Cloth Couch Left Right", CustomMaterial.COUCH_CLOTH_END_RIGHT, ColorableType.DYE, CouchPart.END)),
	COUCH_CLOTH_MIDDLE(CatalogTab.CHAIRS, new Couch("Cloth Couch Middle", CustomMaterial.COUCH_CLOTH_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),
	COUCH_CLOTH_CORNER(CatalogTab.CHAIRS, new Couch("Cloth Couch Corner", CustomMaterial.COUCH_CLOTH_CORNER, ColorableType.DYE, CouchPart.CORNER)),
	COUCH_CLOTH_OTTOMAN(CatalogTab.CHAIRS, new Couch("Cloth Couch Ottoman", CustomMaterial.COUCH_CLOTH_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),
	// 	Blocks
	DYE_STATION(CatalogTab.NONE, new Block("Dye Station", CustomMaterial.DYE_STATION, RotationType.DEGREE_90)),
	TRASH_CAN(new DyeableFloorThing("Trash Can", CustomMaterial.TRASH_CAN, ColorableType.DYE)),
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
	BIRDHOUSE_FOREST_HORIZONTAL(new BirdHouse("Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL, true)),
	BIRDHOUSE_FOREST_VERTICAL(new BirdHouse("Vertical Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_VERTICAL, false)),
	BIRDHOUSE_FOREST_HANGING(new BirdHouse("Hanging Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HANGING, false)),
	BIRDHOUSE_ENCHANTED_HORIZONTAL(new BirdHouse("Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HORIZONTAL, true)),
	BIRDHOUSE_ENCHANTED_VERTICAL(new BirdHouse("Vertical Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_VERTICAL, false)),
	BIRDHOUSE_ENCHANTED_HANGING(new BirdHouse("Hanging Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HANGING, false)),
	BIRDHOUSE_DEPTHS_HORIZONTAL(new BirdHouse("Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HORIZONTAL, true)),
	BIRDHOUSE_DEPTHS_VERTICAL(new BirdHouse("Vertical Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_VERTICAL, false)),
	BIRDHOUSE_DEPTHS_HANGING(new BirdHouse("Hanging Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HANGING, false)),
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
	PUNCHBOWL(CatalogTab.FOOD, new DyeableFloorThing("Punchbowl", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE)),
	PUNCHBOWL_EGGNOG(CatalogTab.FOOD, new DyeableFloorThing("Eggnog", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE, "FFF4BB")),
	SIDE_SAUCE(CatalogTab.FOOD, new DyeableFloorThing("Sauce Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE)),
	SIDE_SAUCE_CRANBERRIES(CatalogTab.FOOD, new DyeableFloorThing("Cranberries Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE, "C61B1B")),
	SIDE_GREEN_BEAN_CASSEROLE(CatalogTab.FOOD, new FloorThing("Green Bean Casserole Side", CustomMaterial.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),
	SIDE_MAC_AND_CHEESE(CatalogTab.FOOD, new FloorThing("Mac N' Cheese Side", CustomMaterial.FOOD_SIDE_MAC_AND_CHEESE)),
	SIDE_SWEET_POTATOES(CatalogTab.FOOD, new FloorThing("Sweet Potatoes Side", CustomMaterial.FOOD_SIDE_SWEET_POTATOES)),
	SIDE_MASHED_POTATOES(CatalogTab.FOOD, new FloorThing("Mashed Potatoes Side", CustomMaterial.FOOD_SIDE_MASHED_POTATOES)),
	SIDE_ROLLS(CatalogTab.FOOD, new FloorThing("Rolls", CustomMaterial.FOOD_SIDE_ROLLS)),
	CAKE_BATTER(CatalogTab.FOOD, new DyeableFloorThing("Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE)),
	CAKE_BATTER_RED_VELVET(CatalogTab.FOOD, new DyeableFloorThing("Red Velvet Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "720606")),
	CAKE_BATTER_VANILLA(CatalogTab.FOOD, new DyeableFloorThing("Vanilla Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "FFF9CC")),
	CAKE_BATTER_CHOCOLATE(CatalogTab.FOOD, new DyeableFloorThing("Chocolate Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "492804")),
	CAKE_WHITE_CHOCOLATE(CatalogTab.FOOD, new FloorThing("White Chocolate Cake", CustomMaterial.FOOD_CAKE_WHITE_CHOCOLATE)),
	CAKE_BUNDT(CatalogTab.FOOD, new FloorThing("Bundt Cake", CustomMaterial.FOOD_CAKE_BUNDT)),
	CAKE_CHOCOLATE_DRIP(CatalogTab.FOOD, new FloorThing("Chocolate Drip Cake", CustomMaterial.FOOD_CAKE_CHOCOLATE_DRIP)),
	PIE_ROUGH(CatalogTab.FOOD, new DyeableFloorThing("Rough Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE)),
	PIE_ROUGH_PECAN(CatalogTab.FOOD, new DyeableFloorThing("Pecan Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE, "4E3004")),
	PIE_SMOOTH(CatalogTab.FOOD, new DyeableFloorThing("Smooth Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE)),
	PIE_SMOOTH_CHOCOLATE(CatalogTab.FOOD, new DyeableFloorThing("Chocolate Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "734008")),
	PIE_SMOOTH_LEMON(CatalogTab.FOOD, new DyeableFloorThing("Lemon Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "FFE050")),
	PIE_SMOOTH_PUMPKIN(CatalogTab.FOOD, new DyeableFloorThing("Pumpkin Pie Decoration", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "BF7D18")),
	PIE_LATTICED(CatalogTab.FOOD, new DyeableFloorThing("Latticed Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE)),
	PIE_LATTICED_APPLE(CatalogTab.FOOD, new DyeableFloorThing("Apple Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "FDC330")),
	PIE_LATTICED_BLUEBERRY(CatalogTab.FOOD, new DyeableFloorThing("Blueberry Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "4E1892")),
	PIE_LATTICED_CHERRY(CatalogTab.FOOD, new DyeableFloorThing("Cherry Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "B60C0C")),
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
	// 	Appliances
	APPLIANCE_FRIDGE(CatalogTab.KITCHENWARE, new Fridge("Fridge", CustomMaterial.APPLIANCE_FRIDGE, FridgeSize.STANDARD)),
	APPLIANCE_FRIDGE_MAGNETS(CatalogTab.KITCHENWARE, new Fridge("Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MAGNETS, FridgeSize.STANDARD)),
	APPLIANCE_FRIDGE_TALL(CatalogTab.KITCHENWARE, new Fridge("Tall Fridge", CustomMaterial.APPLIANCE_FRIDGE_TALL, FridgeSize.TALL)),
	APPLIANCE_FRIDGE_TALL_MAGNETS(CatalogTab.KITCHENWARE, new Fridge("Tall Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_TALL_MAGNETS, FridgeSize.TALL)),
	APPLIANCE_FRIDGE_MINI(CatalogTab.KITCHENWARE, new Fridge("Mini Fridge", CustomMaterial.APPLIANCE_FRIDGE_MINI, FridgeSize.MINI)),
	APPLIANCE_FRIDGE_MINI_MAGNETS(CatalogTab.KITCHENWARE, new Fridge("Mini Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MINI_MAGNETS, FridgeSize.MINI)),
	APPLIANCE_SLUSHIE_MACHINE(CatalogTab.KITCHENWARE, new DyeableFloorThing("Slushie Machine", CustomMaterial.APPLIANCE_SLUSHIE_MACHINE, ColorableType.DYE, Hitbox.single())),
	APPLIANCE_GRILL_COMMERCIAL(CatalogTab.KITCHENWARE, new Block("Commercial Grill", CustomMaterial.APPLIANCE_GRILL_COMMERCIAL, RotationType.BOTH)),
	APPLIANCE_OVEN_COMMERCIAL(CatalogTab.KITCHENWARE, new Block("Commercial Oven", CustomMaterial.APPLIANCE_OVEN_COMMERCIAL, RotationType.BOTH)),
	APPLIANCE_DEEP_FRYER_COMMERCIAL(CatalogTab.KITCHENWARE, new Block("Commercial Deep Fryer", CustomMaterial.APPLIANCE_DEEP_FRYER_COMMERCIAL, RotationType.BOTH)),
	// Counters
	// 	STEEL
	COUNTER_STEEL_MARBLE_ISLAND(CatalogTab.KITCHENWARE, new Counter("Marble Island Counter (Steel)", CustomMaterial.COUNTER_STEEL_MARBLE_ISLAND, CounterType.ISLAND)),
	COUNTER_STEEL_MARBLE_CORNER(CatalogTab.KITCHENWARE, new Counter("Marble Corner Counter (Steel)", CustomMaterial.COUNTER_STEEL_MARBLE_CORNER, CounterType.CORNER)),
	COUNTER_STEEL_MARBLE_DRAWER(CatalogTab.KITCHENWARE, new Counter("Marble Drawers Counter (Steel)", CustomMaterial.COUNTER_STEEL_MARBLE_DRAWER, CounterType.DRAWER)),
	COUNTER_STEEL_MARBLE_CABINET(CatalogTab.KITCHENWARE, new Counter("Marble Cabinet Counter (Steel)", CustomMaterial.COUNTER_STEEL_MARBLE_CABINET, CounterType.CABINET)),
	COUNTER_STEEL_MARBLE_OVEN(CatalogTab.KITCHENWARE, new Counter("Marble Oven Counter (Steel)", CustomMaterial.COUNTER_STEEL_MARBLE_OVEN, CounterType.OVEN)),
	COUNTER_STEEL_MARBLE_SINK(CatalogTab.KITCHENWARE, new Counter("Marble Sink Counter (Steel)", CustomMaterial.COUNTER_STEEL_MARBLE_SINK, CounterType.SINK)),

	COUNTER_STEEL_SOAPSTONE_ISLAND(CatalogTab.KITCHENWARE, new Counter("Soapstone Island Counter (Steel)", CustomMaterial.COUNTER_STEEL_SOAPSTONE_ISLAND, CounterType.ISLAND)),
	COUNTER_STEEL_SOAPSTONE_CORNER(CatalogTab.KITCHENWARE, new Counter("Soapstone Corner Counter (Steel)", CustomMaterial.COUNTER_STEEL_SOAPSTONE_CORNER, CounterType.CORNER)),
	COUNTER_STEEL_SOAPSTONE_DRAWER(CatalogTab.KITCHENWARE, new Counter("Soapstone Drawers Counter (Steel)", CustomMaterial.COUNTER_STEEL_SOAPSTONE_DRAWER, CounterType.DRAWER)),
	COUNTER_STEEL_SOAPSTONE_CABINET(CatalogTab.KITCHENWARE, new Counter("Soapstone Cabinet Counter (Steel)", CustomMaterial.COUNTER_STEEL_SOAPSTONE_CABINET, CounterType.CABINET)),
	COUNTER_STEEL_SOAPSTONE_OVEN(CatalogTab.KITCHENWARE, new Counter("Soapstone Oven Counter (Steel)", CustomMaterial.COUNTER_STEEL_SOAPSTONE_OVEN, CounterType.OVEN)),
	COUNTER_STEEL_SOAPSTONE_SINK(CatalogTab.KITCHENWARE, new Counter("Soapstone Sink Counter (Steel)", CustomMaterial.COUNTER_STEEL_SOAPSTONE_SINK, CounterType.SINK)),

	// 	BRASS
	COUNTER_BRASS_MARBLE_ISLAND(CatalogTab.KITCHENWARE, new Counter("Marble Island Counter (Brass)", CustomMaterial.COUNTER_BRASS_MARBLE_ISLAND, CounterType.ISLAND)),
	COUNTER_BRASS_MARBLE_CORNER(CatalogTab.KITCHENWARE, new Counter("Marble Corner Counter (Brass)", CustomMaterial.COUNTER_BRASS_MARBLE_CORNER, CounterType.CORNER)),
	COUNTER_BRASS_MARBLE_DRAWER(CatalogTab.KITCHENWARE, new Counter("Marble Drawers Counter (Brass)", CustomMaterial.COUNTER_BRASS_MARBLE_DRAWER, CounterType.DRAWER)),
	COUNTER_BRASS_MARBLE_CABINET(CatalogTab.KITCHENWARE, new Counter("Marble Cabinet Counter (Brass)", CustomMaterial.COUNTER_BRASS_MARBLE_CABINET, CounterType.CABINET)),
	COUNTER_BRASS_MARBLE_OVEN(CatalogTab.KITCHENWARE, new Counter("Marble Oven Counter (Brass)", CustomMaterial.COUNTER_BRASS_MARBLE_OVEN, CounterType.OVEN)),
	COUNTER_BRASS_MARBLE_SINK(CatalogTab.KITCHENWARE, new Counter("Marble Sink Counter (Brass)", CustomMaterial.COUNTER_BRASS_MARBLE_SINK, CounterType.SINK)),

	COUNTER_BRASS_SOAPSTONE_ISLAND(CatalogTab.KITCHENWARE, new Counter("Soapstone Island Counter (Brass)", CustomMaterial.COUNTER_BRASS_SOAPSTONE_ISLAND, CounterType.ISLAND)),
	COUNTER_BRASS_SOAPSTONE_CORNER(CatalogTab.KITCHENWARE, new Counter("Soapstone Corner Counter (Brass)", CustomMaterial.COUNTER_BRASS_SOAPSTONE_CORNER, CounterType.CORNER)),
	COUNTER_BRASS_SOAPSTONE_DRAWER(CatalogTab.KITCHENWARE, new Counter("Soapstone Drawers Counter (Brass)", CustomMaterial.COUNTER_BRASS_SOAPSTONE_DRAWER, CounterType.DRAWER)),
	COUNTER_BRASS_SOAPSTONE_CABINET(CatalogTab.KITCHENWARE, new Counter("Soapstone Cabinet Counter (Brass)", CustomMaterial.COUNTER_BRASS_SOAPSTONE_CABINET, CounterType.CABINET)),
	COUNTER_BRASS_SOAPSTONE_OVEN(CatalogTab.KITCHENWARE, new Counter("Soapstone Oven Counter (Brass)", CustomMaterial.COUNTER_BRASS_SOAPSTONE_OVEN, CounterType.OVEN)),
	COUNTER_BRASS_SOAPSTONE_SINK(CatalogTab.KITCHENWARE, new Counter("Soapstone Sink Counter (Brass)", CustomMaterial.COUNTER_BRASS_SOAPSTONE_SINK, CounterType.SINK)),

	// 	BLACK
	COUNTER_BLACK_MARBLE_ISLAND(CatalogTab.KITCHENWARE, new Counter("Marble Island Counter (Black)", CustomMaterial.COUNTER_BLACK_MARBLE_ISLAND, CounterType.ISLAND)),
	COUNTER_BLACK_MARBLE_CORNER(CatalogTab.KITCHENWARE, new Counter("Marble Corner Counter (Black)", CustomMaterial.COUNTER_BLACK_MARBLE_CORNER, CounterType.CORNER)),
	COUNTER_BLACK_MARBLE_DRAWER(CatalogTab.KITCHENWARE, new Counter("Marble Drawers Counter (Black)", CustomMaterial.COUNTER_BLACK_MARBLE_DRAWER, CounterType.DRAWER)),
	COUNTER_BLACK_MARBLE_CABINET(CatalogTab.KITCHENWARE, new Counter("Marble Cabinet Counter (Black)", CustomMaterial.COUNTER_BLACK_MARBLE_CABINET, CounterType.CABINET)),
	COUNTER_BLACK_MARBLE_OVEN(CatalogTab.KITCHENWARE, new Counter("Marble Oven Counter (Black)", CustomMaterial.COUNTER_BLACK_MARBLE_OVEN, CounterType.OVEN)),
	COUNTER_BLACK_MARBLE_SINK(CatalogTab.KITCHENWARE, new Counter("Marble Sink Counter (Black)", CustomMaterial.COUNTER_BLACK_MARBLE_SINK, CounterType.SINK)),

	COUNTER_BLACK_SOAPSTONE_ISLAND(CatalogTab.KITCHENWARE, new Counter("Soapstone Island Counter (Black)", CustomMaterial.COUNTER_BLACK_SOAPSTONE_ISLAND, CounterType.ISLAND)),
	COUNTER_BLACK_SOAPSTONE_CORNER(CatalogTab.KITCHENWARE, new Counter("Soapstone Corner Counter (Black)", CustomMaterial.COUNTER_BLACK_SOAPSTONE_CORNER, CounterType.CORNER)),
	COUNTER_BLACK_SOAPSTONE_DRAWER(CatalogTab.KITCHENWARE, new Counter("Soapstone Drawers Counter (Black)", CustomMaterial.COUNTER_BLACK_SOAPSTONE_DRAWER, CounterType.DRAWER)),
	COUNTER_BLACK_SOAPSTONE_CABINET(CatalogTab.KITCHENWARE, new Counter("Soapstone Cabinet Counter (Black)", CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET, CounterType.CABINET)),
	COUNTER_BLACK_SOAPSTONE_OVEN(CatalogTab.KITCHENWARE, new Counter("Soapstone Oven Counter (Black)", CustomMaterial.COUNTER_BLACK_SOAPSTONE_OVEN, CounterType.OVEN)),
	COUNTER_BLACK_SOAPSTONE_SINK(CatalogTab.KITCHENWARE, new Counter("Soapstone Sink Counter (Black)", CustomMaterial.COUNTER_BLACK_SOAPSTONE_SINK, CounterType.SINK)),
	// Cabinets
	CABINET_STEEL_WOODEN(CatalogTab.KITCHENWARE, new Cabinet("Wooden Cabinet (Steel)", CustomMaterial.CABINET_STEEL_WOODEN, CabinetType.CABINET)),
	CABINET_STEEL_WOODEN_CORNER(CatalogTab.KITCHENWARE, new Cabinet("Wooden Corner Cabinet (Steel)", CustomMaterial.CABINET_STEEL_WOODEN_CORNER, CabinetType.CORNER)),

	CABINET_BRASS_WOODEN(CatalogTab.KITCHENWARE, new Cabinet("Wooden Cabinet (Brass)", CustomMaterial.CABINET_BRASS_WOODEN, CabinetType.CABINET)),
	CABINET_BRASS_WOODEN_CORNER(CatalogTab.KITCHENWARE, new Cabinet("Wooden Corner Cabinet (Brass)", CustomMaterial.CABINET_BRASS_WOODEN_CORNER, CabinetType.CORNER)),

	CABINET_BLACK_WOODEN(CatalogTab.KITCHENWARE, new Cabinet("Wooden Cabinet (Black)", CustomMaterial.CABINET_BLACK_WOODEN, CabinetType.CABINET)),
	CABINET_BLACK_WOODEN_CORNER(CatalogTab.KITCHENWARE, new Cabinet("Wooden Corner Cabinet (Black)", CustomMaterial.CABINET_BLACK_WOODEN_CORNER, CabinetType.CORNER)),
	// 	???
	TOILET_MODERN(CatalogTab.MISC, new DyeableFloorThing("Toilet Modern", CustomMaterial.TOILET_MODERN, ColorableType.DYE)),
	//	Art
	ART_PAINTING_CHERRY_FOREST(CatalogTab.ART, new Art("Cherry Forest Painting", CustomMaterial.ART_PAINTING_CHERRY_FOREST, ArtSize._1x2v)),
	ART_PAINTING_END_ISLAND(CatalogTab.ART, new Art("End Island Painting", CustomMaterial.ART_PAINTING_END_ISLAND, ArtSize._1x2v)),
	ART_PAINTING_LOST_ENDERMAN(CatalogTab.ART, new Art("Lost Enderman Painting", CustomMaterial.ART_PAINTING_LOST_ENDERMAN, ArtSize._1x2v)),
	ART_PAINTING_PINE_TREE(CatalogTab.ART, new Art("Pine Tree Painting", CustomMaterial.ART_PAINTING_PINE_TREE, ArtSize._1x2v)),
	ART_PAINTING_SUNSET(CatalogTab.ART, new Art("Sunset Painting", CustomMaterial.ART_PAINTING_SUNSET, ArtSize._1x2v)),
	ART_PAINTING_SWAMP_HUT(CatalogTab.ART, new Art("Swamp Hut Painting", CustomMaterial.ART_PAINTING_SWAMP_HUT, ArtSize._1x2v)),

	ART_PAINTING_MOUNTAINS(CatalogTab.ART, new Art("Mountains Painting", CustomMaterial.ART_PAINTING_MOUNTAINS, ArtSize._1x2h)),
	ART_PAINTING_MUDDY_PIG(CatalogTab.ART, new Art("Muddy Pig Painting", CustomMaterial.ART_PAINTING_MUDDY_PIG, ArtSize._1x2h)),
	ART_PAINTING_PURPLE_SHEEP(CatalogTab.ART, new Art("Purple Sheep Painting", CustomMaterial.ART_PAINTING_PURPLE_SHEEP, ArtSize._1x2h)),
	ART_PAINTING_VILLAGE_HAPPY(CatalogTab.ART, new Art("Happy Village Painting", CustomMaterial.ART_PAINTING_VILLAGE_HAPPY, ArtSize._1x2h)),
	ART_PAINTING_VILLAGE_CHAOS(CatalogTab.ART, new Art("Chaos Village Painting", CustomMaterial.ART_PAINTING_VILLAGE_CHAOS, ArtSize._1x2h)),

	ART_PAINTING_SKYBLOCK(CatalogTab.ART, new Art("Skyblock Painting", CustomMaterial.ART_PAINTING_SKYBLOCK, ArtSize._1x1)),
	ART_PAINTING_NETHER_FORTRESS_BRIDGE(CatalogTab.ART, new Art("Nether Fortress Bridge Painting", CustomMaterial.ART_PAINTING_NETHER_FORTRESS_BRIDGE, ArtSize._1x1)),
	ART_PAINTING_NETHER_CRIMSON_FOREST(CatalogTab.ART, new Art("Nether Crimson Forest Painting", CustomMaterial.ART_PAINTING_NETHER_CRIMSON_FOREST, ArtSize._1x1)),
	ART_PAINTING_NETHER_WARPED_FOREST(CatalogTab.ART, new Art("Nether Warped Forest Painting", CustomMaterial.ART_PAINTING_NETHER_WARPED_FOREST, ArtSize._1x1)),
	ART_PAINTING_NETHER_BASALT_DELTAS(CatalogTab.ART, new Art("Nether Basalt Deltas Painting", CustomMaterial.ART_PAINTING_NETHER_BASALT_DELTAS, ArtSize._1x1)),
	ART_PAINTING_NETHER_SOUL_SAND_VALLEY(CatalogTab.ART, new Art("Nether Soul Sand Valley Painting", CustomMaterial.ART_PAINTING_NETHER_SOUL_SAND_VALLEY, ArtSize._1x1)),

	ART_PAINTING_CASTLE(CatalogTab.ART, new Art("Castle Painting", CustomMaterial.ART_PAINTING_CASTLE, ArtSize._2x2)),
	ART_PAINTING_LAKE(CatalogTab.ART, new Art("Lake Painting", CustomMaterial.ART_PAINTING_LAKE, ArtSize._2x2)),
	ART_PAINTING_RIVER(CatalogTab.ART, new Art("River Painting", CustomMaterial.ART_PAINTING_RIVER, ArtSize._2x2)),
	ART_PAINTING_ROAD(CatalogTab.ART, new Art("Road Painting", CustomMaterial.ART_PAINTING_ROAD, ArtSize._2x2)),
	ART_PAINTING_ORIENTAL(CatalogTab.ART, new Art("Oriental Painting", CustomMaterial.ART_PAINTING_ORIENTAL, ArtSize._2x2)),
	ART_PAINTING_CHICKENS(CatalogTab.ART, new Art("Chickens Painting", CustomMaterial.ART_PAINTING_CHICKENS, ArtSize._2x2)),
	ART_PAINTING_OAK_TREE(CatalogTab.ART, new Art("Oak Tree Painting", CustomMaterial.ART_PAINTING_OAK_TREE, ArtSize._2x2)),
	ART_PAINTING_CRAB(CatalogTab.ART, new Art("Crab Painting", CustomMaterial.ART_PAINTING_CRAB, ArtSize._2x2)),
	ART_PAINTING_SATURN_ROCKET(CatalogTab.ART, new Art("Saturn Rocket Painting", CustomMaterial.ART_PAINTING_SATURN_ROCKET, ArtSize._2x2)),
	ART_PAINTING_PARROT(CatalogTab.ART, new Art("Oak Tree Painting", CustomMaterial.ART_PAINTING_PARROT, ArtSize._2x2)),
	ART_PAINTING_DUCKS(CatalogTab.ART, new Art("Ducks Painting", CustomMaterial.ART_PAINTING_DUCKS, ArtSize._2x2)),
	ART_PAINTING_STARRY_PINE_TREE(CatalogTab.ART, new Art("Starry Pine Tree Painting", CustomMaterial.ART_PAINTING_STARRY_PINE_TREE, ArtSize._2x2)),

	ART_PAINTING_FOREST(CatalogTab.ART, new Art("Forest Painting", CustomMaterial.ART_PAINTING_FOREST, ArtSize._1x3h)),

	ART_PAINTING_SAND_DUNES(CatalogTab.ART, new Art("Sand Dunes Painting", CustomMaterial.ART_PAINTING_SAND_DUNES, ArtSize._1x3v)),

	ART_PAINTING_STORY(CatalogTab.ART, new Art("Story Painting", CustomMaterial.ART_PAINTING_STORY, ArtSize._2x3h)),
	//	Potions
	POTION_FILLED_TINY_1(CatalogTab.POTIONS, new DyeableFloorThing("Tiny Potions 1", CustomMaterial.POTION_FILLED_TINY_1, ColorableType.DYE)),
	POTION_FILLED_TINY_2(CatalogTab.POTIONS, new DyeableFloorThing("Tiny Potions 2", CustomMaterial.POTION_FILLED_TINY_2, ColorableType.DYE)),
	POTION_FILLED_SMALL_1(CatalogTab.POTIONS, new DyeableFloorThing("Small Potion 1", CustomMaterial.POTION_FILLED_SMALL_1, ColorableType.DYE)),
	POTION_FILLED_SMALL_2(CatalogTab.POTIONS, new DyeableFloorThing("Small Potion 2", CustomMaterial.POTION_FILLED_SMALL_2, ColorableType.DYE)),
	POTION_FILLED_SMALL_3(CatalogTab.POTIONS, new DyeableFloorThing("Small Potion 3", CustomMaterial.POTION_FILLED_SMALL_3, ColorableType.DYE)),
	POTION_FILLED_MEDIUM_1(CatalogTab.POTIONS, new DyeableFloorThing("Medium Potion 1", CustomMaterial.POTION_FILLED_MEDIUM_1, ColorableType.DYE)),
	POTION_FILLED_MEDIUM_2(CatalogTab.POTIONS, new DyeableFloorThing("Medium Potion 2", CustomMaterial.POTION_FILLED_MEDIUM_2, ColorableType.DYE)),
	POTION_FILLED_WIDE(CatalogTab.POTIONS, new DyeableFloorThing("Wide Potion", CustomMaterial.POTION_FILLED_WIDE, ColorableType.DYE)),
	POTION_FILLED_SKINNY(CatalogTab.POTIONS, new DyeableFloorThing("Skinny Potion", CustomMaterial.POTION_FILLED_SKINNY, ColorableType.DYE)),
	POTION_FILLED_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Tall Potion", CustomMaterial.POTION_FILLED_TALL, ColorableType.DYE)),
	POTION_FILLED_BIG_BOTTLE(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Bottle", CustomMaterial.POTION_FILLED_BIG_BOTTLE, ColorableType.DYE)),
	POTION_FILLED_BIG_TEAR(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Tear", CustomMaterial.POTION_FILLED_BIG_TEAR, ColorableType.DYE)),
	POTION_FILLED_BIG_DONUT(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Donut", CustomMaterial.POTION_FILLED_BIG_DONUT, ColorableType.DYE)),
	POTION_FILLED_BIG_SKULL(CatalogTab.POTIONS, new DyeableFloorThing("Big Potion Skull", CustomMaterial.POTION_FILLED_BIG_SKULL, ColorableType.DYE)),
	POTION_FILLED_GROUP_SMALL(CatalogTab.POTIONS, new DyeableFloorThing("Small Potions", CustomMaterial.POTION_FILLED_GROUP_SMALL, ColorableType.DYE)),
	POTION_FILLED_GROUP_MEDIUM(CatalogTab.POTIONS, new DyeableFloorThing("Medium Potions", CustomMaterial.POTION_FILLED_GROUP_MEDIUM, ColorableType.DYE)),
	POTION_FILLED_GROUP_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Tall Potions", CustomMaterial.POTION_FILLED_GROUP_TALL, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_1(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 1", CustomMaterial.POTION_FILLED_GROUP_RANDOM_1, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_2(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 2", CustomMaterial.POTION_FILLED_GROUP_RANDOM_2, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_3(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 3", CustomMaterial.POTION_FILLED_GROUP_RANDOM_3, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_4(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 4", CustomMaterial.POTION_FILLED_GROUP_RANDOM_4, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_5(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 5", CustomMaterial.POTION_FILLED_GROUP_RANDOM_5, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_6(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 6", CustomMaterial.POTION_FILLED_GROUP_RANDOM_6, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_7(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 7", CustomMaterial.POTION_FILLED_GROUP_RANDOM_7, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_8(CatalogTab.POTIONS, new DyeableFloorThing("Random Potions 8", CustomMaterial.POTION_FILLED_GROUP_RANDOM_8, ColorableType.DYE)),

	POTION_EMPTY_SMALL_1(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potion 1", CustomMaterial.POTION_EMPTY_SMALL_1, ColorableType.DYE)),
	POTION_EMPTY_SMALL_2(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potion 2", CustomMaterial.POTION_EMPTY_SMALL_2, ColorableType.DYE)),
	POTION_EMPTY_SMALL_3(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potion 3", CustomMaterial.POTION_EMPTY_SMALL_3, ColorableType.DYE)),
	POTION_EMPTY_MEDIUM_1(CatalogTab.POTIONS, new DyeableFloorThing("Empty Medium Potion 1", CustomMaterial.POTION_EMPTY_MEDIUM_1, ColorableType.DYE)),
	POTION_EMPTY_MEDIUM_2(CatalogTab.POTIONS, new DyeableFloorThing("Empty Medium Potion 2", CustomMaterial.POTION_EMPTY_MEDIUM_2, ColorableType.DYE)),
	POTION_EMPTY_WIDE(CatalogTab.POTIONS, new DyeableFloorThing("Empty Wide Potion", CustomMaterial.POTION_EMPTY_WIDE, ColorableType.DYE)),
	POTION_EMPTY_SKINNY(CatalogTab.POTIONS, new DyeableFloorThing("Empty Skinny Potion", CustomMaterial.POTION_EMPTY_SKINNY, ColorableType.DYE)),
	POTION_EMPTY_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Tall Potion", CustomMaterial.POTION_EMPTY_TALL, ColorableType.DYE)),
	POTION_EMPTY_BIG_BOTTLE(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Bottle", CustomMaterial.POTION_EMPTY_BIG_BOTTLE, ColorableType.DYE)),
	POTION_EMPTY_BIG_TEAR(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Tear", CustomMaterial.POTION_EMPTY_BIG_TEAR, ColorableType.DYE)),
	POTION_EMPTY_BIG_DONUT(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Donut", CustomMaterial.POTION_EMPTY_BIG_DONUT, ColorableType.DYE)),
	POTION_EMPTY_BIG_SKULL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Big Potion Skull", CustomMaterial.POTION_EMPTY_BIG_SKULL, ColorableType.DYE)),
	POTION_EMPTY_GROUP_SMALL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Small Potions", CustomMaterial.POTION_EMPTY_GROUP_SMALL, ColorableType.DYE)),
	POTION_EMPTY_GROUP_MEDIUM(CatalogTab.POTIONS, new DyeableFloorThing("Empty Medium Potions", CustomMaterial.POTION_EMPTY_GROUP_MEDIUM, ColorableType.DYE)),
	POTION_EMPTY_GROUP_TALL(CatalogTab.POTIONS, new DyeableFloorThing("Empty Tall Potions", CustomMaterial.POTION_EMPTY_GROUP_TALL, ColorableType.DYE)),

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
	MAILBOX(new DyeableFloorThing("Mailbox", CustomMaterial.MAILBOX, ColorableType.DYE, List.of(Hitbox.origin(), Hitbox.offset(BlockFace.UP)))),
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
