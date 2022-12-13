package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.Catalog.Tab;
import gg.projecteden.nexus.features.resourcepack.decoration.Catalog.Theme;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Colorable.ColorableType;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.RotationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art.ArtSize;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet.CabinetMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Cabinet.CabinetType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterMaterial;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.CounterType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Counter.HandleType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fireplace;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fridge;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Fridge.FridgeSize;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Furniture;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Furniture.FurnitureSize;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Furniture.FurnitureSurface;
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

import java.util.Arrays;
import java.util.List;

/*
	TODO:
		- finish adding rest of decorations
		- add catalogs menu
		- middle click creative copy
		- cant interact with decorations with item in offhand, affects paintbrush too
		- Mob Plushies
		- add "Structure" type
 */

@AllArgsConstructor
public enum DecorationType {
	// Catalog: Holiday
	//	Fireplaces
	FIREPLACE_DARK_XMAS(Theme.HOLIDAY, new Fireplace("Dark XMas Fireplace", CustomMaterial.FIREPLACE_DARK_XMAS)),
	FIREPLACE_BROWN_XMAS(Theme.HOLIDAY, new Fireplace("Brown XMas Fireplace", CustomMaterial.FIREPLACE_BROWN_XMAS)),
	FIREPLACE_LIGHT_XMAS(Theme.HOLIDAY, new Fireplace("Light XMas Fireplace", CustomMaterial.FIREPLACE_LIGHT_XMAS)),
	CHRISTMAS_TREE_COLOR(Theme.HOLIDAY, new FloorThing("Christmas Tree Colored", CustomMaterial.CHRISTMAS_TREE_COLORED)),
	CHRISTMAS_TREE_WHITE(Theme.HOLIDAY, new FloorThing("Christmas Tree White", CustomMaterial.CHRISTMAS_TREE_WHITE)),
	//	TOY_TRAIN(Theme.HOLIDAY, new FloorThing("Toy Train", CustomMaterial.TOY_TRAIN)), // Add as part of a Christmas tree structure
	MISTLETOE(Theme.HOLIDAY, new CeilingThing("Mistletoe", CustomMaterial.MISTLETOE)),
	WREATH(Theme.HOLIDAY, new DecorationConfig("Wreath", CustomMaterial.WREATH)),
	STOCKINGS_SINGLE(Theme.HOLIDAY, new DecorationConfig("Single Stocking", CustomMaterial.STOCKINGS_SINGLE)),
	STOCKINGS_DOUBLE(Theme.HOLIDAY, new DecorationConfig("Double Stocking", CustomMaterial.STOCKINGS_DOUBLE)),
	BUNTING_PHRASE_HAPPY_HOLIDAYS(Theme.HOLIDAY, new DecorationConfig("Happy Holidays Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_HOLIDAYS)),
	BUNTING_PHRASE_HAPPY_NEW_YEAR(Theme.HOLIDAY, new DecorationConfig("Happy New Year Bunting", CustomMaterial.BUNTING_PHRASE_HAPPY_NEW_YEAR)),
	BUNTING_PHRASE_MERRY_CHRISTMAS(Theme.HOLIDAY, new DecorationConfig("Merry Christmas Bunting", CustomMaterial.BUNTING_PHRASE_MERRY_CHRISTMAS)),
	SNOWMAN_PLAIN(Theme.HOLIDAY, new FloorThing("Snowman Plain", CustomMaterial.SNOWMAN_PLAIN)),
	SNOWMAN_FANCY(Theme.HOLIDAY, new FloorThing("Snowman Fancy", CustomMaterial.SNOWMAN_FANCY)),
	SNOWBALLS_SMALL(Theme.HOLIDAY, new FloorThing("Snowballs Small Pile", CustomMaterial.SNOWBALLS_SMALL)),
	SNOWBALLS_BIG(Theme.HOLIDAY, new FloorThing("Snowballs Big Pile", CustomMaterial.SNOWBALLS_BIG)),
	ICICLE_LIGHT_CENTER(Theme.HOLIDAY, new DecorationConfig("Icicle Lights Center", CustomMaterial.ICICLE_LIGHT_CENTER)),
	ICICLE_LIGHT_LEFT(Theme.HOLIDAY, new DecorationConfig("Icicle Lights Left", CustomMaterial.ICICLE_LIGHT_LEFT)),
	ICICLE_LIGHT_RIGHT(Theme.HOLIDAY, new DecorationConfig("Icicle Lights Right", CustomMaterial.ICICLE_LIGHT_RIGHT)),

	// Catalog: Spooky
	// 	Gravestones
	GRAVESTONE_SMALL(Theme.SPOOKY, new FloorThing("Small Gravestone", CustomMaterial.GRAVESTONE_SMALL)),
	GRAVESTONE_CROSS(Theme.SPOOKY, new FloorThing("Gravestone Cross", CustomMaterial.GRAVESTONE_CROSS, Hitbox.single(Material.IRON_BARS))),
	GRAVESTONE_PLAQUE(Theme.SPOOKY, new FloorThing("Gravestone Plaque", CustomMaterial.GRAVESTONE_PLAQUE)),
	GRAVESTONE_STACK(Theme.SPOOKY, new FloorThing("Rock Stack Gravestone", CustomMaterial.GRAVESTONE_STACK)),
	GRAVESTONE_FLOWERBED(Theme.SPOOKY, new FloorThing("Flowerbed Gravestone", CustomMaterial.GRAVESTONE_FLOWERBED)),
	GRAVESTONE_TALL(Theme.SPOOKY, new FloorThing("Tall Gravestone", CustomMaterial.GRAVESTONE_TALL, List.of(Hitbox.origin(Material.IRON_BARS), Hitbox.offset(Material.IRON_BARS, BlockFace.UP)))),

	// Catalog: General
	// 	Tables
	TABLE_WOODEN_1x1(Tab.TABLES, new Table("Wooden Table 1x1", CustomMaterial.TABLE_WOODEN_1X1, Table.TableSize._1x1)),
	TABLE_WOODEN_1x2(Tab.TABLES, new Table("Wooden Table 1x2", CustomMaterial.TABLE_WOODEN_1X2, Table.TableSize._1x2)),
	TABLE_WOODEN_1x3(Tab.TABLES, new Table("Wooden Table 1x3", CustomMaterial.TABLE_WOODEN_1X3, Table.TableSize._1x3)),
	TABLE_WOODEN_2x2(Tab.TABLES, new Table("Wooden Table 2x2", CustomMaterial.TABLE_WOODEN_2X2, Table.TableSize._2x2)),
	TABLE_WOODEN_2x3(Tab.TABLES, new Table("Wooden Table 2x3", CustomMaterial.TABLE_WOODEN_2X3, Table.TableSize._2x3)),
	TABLE_WOODEN_3x3(Tab.TABLES, new Table("Wooden Table 3x3", CustomMaterial.TABLE_WOODEN_3X3, Table.TableSize._3x3)),

	// 	Chairs
	CHAIR_WOODEN_BASIC(Tab.CHAIRS, new Chair("Wooden Chair", CustomMaterial.CHAIR_WOODEN_BASIC, ColorableType.STAIN)),
	CHAIR_WOODEN_CUSHIONED(Tab.CHAIRS, new Chair("Cushioned Wooden Chair", CustomMaterial.CHAIR_WOODEN_CUSHIONED, ColorableType.DYE)),
	CHAIR_CLOTH(Tab.CHAIRS, new Chair("Cloth Chair", CustomMaterial.CHAIR_CLOTH, ColorableType.DYE)),
	ADIRONDACK(Tab.CHAIRS, new Chair("Adirondack", CustomMaterial.ADIRONDACK, ColorableType.STAIN)),
	CHAIR_BEACH(Tab.CHAIRS, new LongChair("Beach Chair", CustomMaterial.BEACH_CHAIR, ColorableType.DYE, Hitbox.light(), .675)),

	// 	Stools
	STOOL_WOODEN_BASIC(Tab.CHAIRS, new Chair("Wooden Stool", CustomMaterial.STOOL_WOODEN_BASIC, ColorableType.STAIN)),
	STOOL_WOODEN_CUSHIONED(Tab.CHAIRS, new Chair("Cushioned Wooden Stool", CustomMaterial.STOOL_WOODEN_CUSHIONED, ColorableType.DYE)),
	STOOL_BAR_WOODEN(Tab.CHAIRS, new Chair("Wooden Bar Stool", CustomMaterial.STOOL_BAR_WOODEN, ColorableType.STAIN, 1.2)),
	STOOL_STUMP_OAK(Tab.CHAIRS, new Stump("Oak Stump", CustomMaterial.STOOL_STUMP_OAK)),
	STOOL_STUMP_OAK_ROOTS(Tab.CHAIRS, new Stump("Rooted Oak Stump", CustomMaterial.STOOL_STUMP_OAK_ROOTS)),
	STOOL_STUMP_SPRUCE(Tab.CHAIRS, new Stump("Spruce Stump", CustomMaterial.STOOL_STUMP_SPRUCE)),
	STOOL_STUMP_SPRUCE_ROOTS(Tab.CHAIRS, new Stump("Rooted Spruce Stump", CustomMaterial.STOOL_STUMP_SPRUCE_ROOTS)),
	STOOL_STUMP_BIRCH(Tab.CHAIRS, new Stump("Birch Stump", CustomMaterial.STOOL_STUMP_BIRCH)),
	STOOL_STUMP_BIRCH_ROOTS(Tab.CHAIRS, new Stump("Rooted Birch Stump", CustomMaterial.STOOL_STUMP_BIRCH_ROOTS)),
	STOOL_STUMP_JUNGLE(Tab.CHAIRS, new Stump("Jungle Stump", CustomMaterial.STOOL_STUMP_JUNGLE)),
	STOOL_STUMP_JUNGLE_ROOTS(Tab.CHAIRS, new Stump("Rooted Jungle Stump", CustomMaterial.STOOL_STUMP_JUNGLE_ROOTS)),
	STOOL_STUMP_ACACIA(Tab.CHAIRS, new Stump("Acacia Stump", CustomMaterial.STOOL_STUMP_ACACIA)),
	STOOL_STUMP_ACACIA_ROOTS(Tab.CHAIRS, new Stump("Rooted Acacia Stump", CustomMaterial.STOOL_STUMP_ACACIA_ROOTS)),
	STOOL_STUMP_DARK_OAK(Tab.CHAIRS, new Stump("Dark Oak Stump", CustomMaterial.STOOL_STUMP_DARK_OAK)),
	STOOL_STUMP_DARK_OAK_ROOTS(Tab.CHAIRS, new Stump("Rooted Dark Oak Stump", CustomMaterial.STOOL_STUMP_DARK_OAK_ROOTS)),
	STOOL_STUMP_MANGROVE(Tab.CHAIRS, new Stump("Mangrove Stump", CustomMaterial.STOOL_STUMP_MANGROVE)),
	STOOL_STUMP_MANGROVE_ROOTS(Tab.CHAIRS, new Stump("Rooted Mangrove Stump", CustomMaterial.STOOL_STUMP_MANGROVE_ROOTS)),
	STOOL_STUMP_CRIMSON(Tab.CHAIRS, new Stump("Crimson Stump", CustomMaterial.STOOL_STUMP_CRIMSON)),
	STOOL_STUMP_CRIMSON_ROOTS(Tab.CHAIRS, new Stump("Rooted Crimson Stump", CustomMaterial.STOOL_STUMP_CRIMSON_ROOTS)),
	STOOL_STUMP_WARPED(Tab.CHAIRS, new Stump("Warped Stump", CustomMaterial.STOOL_STUMP_WARPED)),
	STOOL_STUMP_WARPED_ROOTS(Tab.CHAIRS, new Stump("Rooted Warped Stump", CustomMaterial.STOOL_STUMP_WARPED_ROOTS)),

	// 	Benches
	BENCH_WOODEN(Tab.CHAIRS, new Bench("Wooden Bench", CustomMaterial.BENCH_WOODEN, ColorableType.STAIN)),

	// 	Couches
	COUCH_WOODEN_CUSHIONED_END_LEFT(Tab.CHAIRS, new Couch("Cushioned Wooden Couch Left End", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_LEFT, ColorableType.DYE, CouchPart.END)),
	COUCH_WOODEN_CUSHIONED_END_RIGHT(Tab.CHAIRS, new Couch("Cushioned Wooden Couch Left Right", CustomMaterial.COUCH_WOODEN_CUSHIONED_END_RIGHT, ColorableType.DYE, CouchPart.END)),
	COUCH_WOODEN_CUSHIONED_MIDDLE(Tab.CHAIRS, new Couch("Cushioned Wooden Couch Middle", CustomMaterial.COUCH_WOODEN_CUSHIONED_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),
	COUCH_WOODEN_CUSHIONED_CORNER(Tab.CHAIRS, new Couch("Cushioned Wooden Couch Corner", CustomMaterial.COUCH_WOODEN_CUSHIONED_CORNER, ColorableType.DYE, CouchPart.CORNER)),
	COUCH_WOODEN_CUSHIONED_OTTOMAN(Tab.CHAIRS, new Couch("Cushioned Wooden Couch Ottoman", CustomMaterial.COUCH_WOODEN_CUSHIONED_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),
	COUCH_CLOTH_END_LEFT(Tab.CHAIRS, new Couch("Cloth Couch Left End", CustomMaterial.COUCH_CLOTH_END_LEFT, ColorableType.DYE, CouchPart.END)),
	COUCH_CLOTH_END_RIGHT(Tab.CHAIRS, new Couch("Cloth Couch Left Right", CustomMaterial.COUCH_CLOTH_END_RIGHT, ColorableType.DYE, CouchPart.END)),
	COUCH_CLOTH_MIDDLE(Tab.CHAIRS, new Couch("Cloth Couch Middle", CustomMaterial.COUCH_CLOTH_MIDDLE, ColorableType.DYE, CouchPart.STRAIGHT)),
	COUCH_CLOTH_CORNER(Tab.CHAIRS, new Couch("Cloth Couch Corner", CustomMaterial.COUCH_CLOTH_CORNER, ColorableType.DYE, CouchPart.CORNER)),
	COUCH_CLOTH_OTTOMAN(Tab.CHAIRS, new Couch("Cloth Couch Ottoman", CustomMaterial.COUCH_CLOTH_OTTOMAN, ColorableType.DYE, CouchPart.STRAIGHT)),

	// 	Blocks
	DYE_STATION(Tab.NONE, new Block("Dye Station", CustomMaterial.DYE_STATION, RotationType.DEGREE_90)),
	TRASH_CAN(new DyeableFloorThing("Trash Can", CustomMaterial.TRASH_CAN, ColorableType.DYE, "C7C7C7")),

	// 	Fireplaces
	FIREPLACE_DARK(new Fireplace("Dark Fireplace", CustomMaterial.FIREPLACE_DARK)),
	FIREPLACE_BROWN(new Fireplace("Brown Fireplace", CustomMaterial.FIREPLACE_BROWN)),
	FIREPLACE_LIGHT(new Fireplace("Light Fireplace", CustomMaterial.FIREPLACE_LIGHT)),

	//	Windchimes
	WINDCHIME_IRON(Tab.WINDCHIMES, new WindChime("Iron Windchimes", WindChimeType.IRON)),
	WINDCHIME_GOLD(Tab.WINDCHIMES, new WindChime("Gold Windchimes", WindChimeType.GOLD)),
	WINDCHIME_COPPER(Tab.WINDCHIMES, new WindChime("Copper Windchimes", WindChimeType.COPPER)),
	WINDCHIME_AMETHYST(Tab.WINDCHIMES, new WindChime("Amethyst Windchimes", WindChimeType.AMETHYST)),
	WINDCHIME_LAPIS(Tab.WINDCHIMES, new WindChime("Lapis Windchimes", WindChimeType.LAPIS)),
	WINDCHIME_NETHERITE(Tab.WINDCHIMES, new WindChime("Netherite Windchimes", WindChimeType.NETHERITE)),
	WINDCHIME_DIAMOND(Tab.WINDCHIMES, new WindChime("Diamond Windchimes", WindChimeType.DIAMOND)),
	WINDCHIME_REDSTONE(Tab.WINDCHIMES, new WindChime("Redstone Windchimes", WindChimeType.REDSTONE)),
	WINDCHIME_EMERALD(Tab.WINDCHIMES, new WindChime("Emerald Windchimes", WindChimeType.EMERALD)),
	WINDCHIME_QUARTZ(Tab.WINDCHIMES, new WindChime("Quartz Windchimes", WindChimeType.QUARTZ)),
	WINDCHIME_COAL(Tab.WINDCHIMES, new WindChime("Coal Windchimes", WindChimeType.COAL)),
	WINDCHIME_ICE(Tab.WINDCHIMES, new WindChime("Ice Windchimes", WindChimeType.ICE)),

	// 	Birdhouses
	BIRDHOUSE_FOREST_HORIZONTAL(new BirdHouse("Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HORIZONTAL, true)),
	BIRDHOUSE_FOREST_VERTICAL(Tab.INVISIBLE, new BirdHouse("Vertical Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_VERTICAL, false)),
	BIRDHOUSE_FOREST_HANGING(Tab.INVISIBLE, new BirdHouse("Hanging Forest Birdhouse", CustomMaterial.BIRDHOUSE_FOREST_HANGING, false)),
	BIRDHOUSE_ENCHANTED_HORIZONTAL(new BirdHouse("Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HORIZONTAL, true)),
	BIRDHOUSE_ENCHANTED_VERTICAL(Tab.INVISIBLE, new BirdHouse("Vertical Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_VERTICAL, false)),
	BIRDHOUSE_ENCHANTED_HANGING(Tab.INVISIBLE, new BirdHouse("Hanging Enchanted Birdhouse", CustomMaterial.BIRDHOUSE_ENCHANTED_HANGING, false)),
	BIRDHOUSE_DEPTHS_HORIZONTAL(new BirdHouse("Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HORIZONTAL, true)),
	BIRDHOUSE_DEPTHS_VERTICAL(Tab.INVISIBLE, new BirdHouse("Vertical Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_VERTICAL, false)),
	BIRDHOUSE_DEPTHS_HANGING(Tab.INVISIBLE, new BirdHouse("Hanging Depths Birdhouse", CustomMaterial.BIRDHOUSE_DEPTHS_HANGING, false)),

	//	Food
	PIZZA_BOX_SINGLE(Tab.FOOD, new FloorThing("Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE)),
	PIZZA_BOX_SINGLE_OPENED(Tab.FOOD, new FloorThing("Opened Pizza Box", CustomMaterial.FOOD_PIZZA_BOX_SINGLE_OPENED)),
	PIZZA_BOX_STACK(Tab.FOOD, new FloorThing("Pizza Box Stack", CustomMaterial.FOOD_PIZZA_BOX_STACK)),
	SOUP_MUSHROOM(Tab.FOOD, new FloorThing("Mushroom Soup", CustomMaterial.FOOD_SOUP_MUSHROOM)),
	SOUP_BEETROOT(Tab.FOOD, new FloorThing("Beetroot Soup", CustomMaterial.FOOD_SOUP_BEETROOT)),
	SOUP_RABBIT(Tab.FOOD, new FloorThing("Rabbit Soup", CustomMaterial.FOOD_SOUP_RABBIT)),
	BREAD_LOAF(Tab.FOOD, new FloorThing("Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF)),
	BREAD_LOAF_CUT(Tab.FOOD, new FloorThing("Cut Loaf of Bread", CustomMaterial.FOOD_BREAD_LOAF_CUT)),
	BROWNIES_CHOCOLATE(Tab.FOOD, new FloorThing("Chocolate Brownies", CustomMaterial.FOOD_BROWNIES_CHOCOLATE)),
	BROWNIES_VANILLA(Tab.FOOD, new FloorThing("Vanilla Brownies", CustomMaterial.FOOD_BROWNIES_VANILLA)),
	COOKIES_CHOCOLATE(Tab.FOOD, new FloorThing("Chocolate Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE)),
	COOKIES_CHOCOLATE_CHIP(Tab.FOOD, new FloorThing("Chocolate Chip Cookies", CustomMaterial.FOOD_COOKIES_CHOCOLATE_CHIP)),
	COOKIES_SUGAR(Tab.FOOD, new FloorThing("Sugar Cookies", CustomMaterial.FOOD_COOKIES_SUGAR)),
	MILK_AND_COOKIES(Tab.FOOD, new FloorThing("Milk and Cookies", CustomMaterial.FOOD_MILK_AND_COOKIES)),
	MUFFINS_CHOCOLATE(Tab.FOOD, new FloorThing("Chocolate Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE)),
	MUFFINS_CHOCOLATE_CHIP(Tab.FOOD, new FloorThing("Chocolate Chip Muffins", CustomMaterial.FOOD_MUFFINS_CHOCOLATE_CHIP)),
	MUFFINS_LEMON(Tab.FOOD, new FloorThing("Lemon Muffins", CustomMaterial.FOOD_MUFFINS_LEMON)),
	DINNER_HAM(Tab.FOOD, new FloorThing("Ham Dinner", CustomMaterial.FOOD_DINNER_HAM)),
	DINNER_ROAST(Tab.FOOD, new FloorThing("Roast Dinner", CustomMaterial.FOOD_DINNER_ROAST)),
	DINNER_TURKEY(Tab.FOOD, new FloorThing("Turkey Dinner", CustomMaterial.FOOD_DINNER_TURKEY)),
	PUNCHBOWL(Tab.FOOD, new DyeableFloorThing("Punchbowl", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE)),
	PUNCHBOWL_EGGNOG(Tab.FOOD, new DyeableFloorThing("Eggnog", CustomMaterial.FOOD_PUNCHBOWL, ColorableType.DYE, "FFF4BB")),
	SIDE_SAUCE(Tab.FOOD, new DyeableFloorThing("Sauce Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE)),
	SIDE_SAUCE_CRANBERRIES(Tab.FOOD, new DyeableFloorThing("Cranberries Side", CustomMaterial.FOOD_SIDE_SAUCE, ColorableType.DYE, "C61B1B")),
	SIDE_GREEN_BEAN_CASSEROLE(Tab.FOOD, new FloorThing("Green Bean Casserole Side", CustomMaterial.FOOD_SIDE_GREEN_BEAN_CASSEROLE)),
	SIDE_MAC_AND_CHEESE(Tab.FOOD, new FloorThing("Mac N' Cheese Side", CustomMaterial.FOOD_SIDE_MAC_AND_CHEESE)),
	SIDE_SWEET_POTATOES(Tab.FOOD, new FloorThing("Sweet Potatoes Side", CustomMaterial.FOOD_SIDE_SWEET_POTATOES)),
	SIDE_MASHED_POTATOES(Tab.FOOD, new FloorThing("Mashed Potatoes Side", CustomMaterial.FOOD_SIDE_MASHED_POTATOES)),
	SIDE_ROLLS(Tab.FOOD, new FloorThing("Rolls", CustomMaterial.FOOD_SIDE_ROLLS)),
	CAKE_BATTER(Tab.FOOD, new DyeableFloorThing("Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE)),
	CAKE_BATTER_RED_VELVET(Tab.FOOD, new DyeableFloorThing("Red Velvet Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "720606")),
	CAKE_BATTER_VANILLA(Tab.FOOD, new DyeableFloorThing("Vanilla Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "FFF9CC")),
	CAKE_BATTER_CHOCOLATE(Tab.FOOD, new DyeableFloorThing("Chocolate Cake Batter", CustomMaterial.FOOD_CAKE_BATTER, ColorableType.DYE, "492804")),
	CAKE_WHITE_CHOCOLATE(Tab.FOOD, new FloorThing("White Chocolate Cake", CustomMaterial.FOOD_CAKE_WHITE_CHOCOLATE)),
	CAKE_BUNDT(Tab.FOOD, new FloorThing("Bundt Cake", CustomMaterial.FOOD_CAKE_BUNDT)),
	CAKE_CHOCOLATE_DRIP(Tab.FOOD, new FloorThing("Chocolate Drip Cake", CustomMaterial.FOOD_CAKE_CHOCOLATE_DRIP)),
	PIE_ROUGH(Tab.FOOD, new DyeableFloorThing("Rough Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE)),
	PIE_ROUGH_PECAN(Tab.FOOD, new DyeableFloorThing("Pecan Pie", CustomMaterial.FOOD_PIE_ROUGH, ColorableType.DYE, "4E3004")),
	PIE_SMOOTH(Tab.FOOD, new DyeableFloorThing("Smooth Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE)),
	PIE_SMOOTH_CHOCOLATE(Tab.FOOD, new DyeableFloorThing("Chocolate Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "734008")),
	PIE_SMOOTH_LEMON(Tab.FOOD, new DyeableFloorThing("Lemon Pie", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "FFE050")),
	PIE_SMOOTH_PUMPKIN(Tab.FOOD, new DyeableFloorThing("Pumpkin Pie Decoration", CustomMaterial.FOOD_PIE_SMOOTH, ColorableType.DYE, "BF7D18")),
	PIE_LATTICED(Tab.FOOD, new DyeableFloorThing("Latticed Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE)),
	PIE_LATTICED_APPLE(Tab.FOOD, new DyeableFloorThing("Apple Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "FDC330")),
	PIE_LATTICED_BLUEBERRY(Tab.FOOD, new DyeableFloorThing("Blueberry Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "4E1892")),
	PIE_LATTICED_CHERRY(Tab.FOOD, new DyeableFloorThing("Cherry Pie", CustomMaterial.FOOD_PIE_LATTICED, ColorableType.DYE, "B60C0C")),

	//	Kitchenware
	WINE_BOTTLE(Tab.KITCHENWARE, new FloorThing("Wine Bottle", CustomMaterial.KITCHENWARE_WINE_BOTTLE)),
	WINE_BOTTLE_GROUP(Tab.KITCHENWARE, new FloorThing("Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP)),
	WINE_BOTTLE_GROUP_RANDOM(Tab.KITCHENWARE, new FloorThing("Random Wine Bottles", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM)),
	WINE_BOTTLE_GROUP_SIDE(Tab.KITCHENWARE, new FloorThing("Wine Bottles on Side", CustomMaterial.KITCHENWARE_WINE_BOTTLE_GROUP_SIDE)),
	WINE_GLASS(Tab.KITCHENWARE, new FloorThing("Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS)),
	WINE_GLASS_FULL(Tab.KITCHENWARE, new FloorThing("Full Wine Glass", CustomMaterial.KITCHENWARE_WINE_GLASS_FULL)),
	MUG_GLASS(Tab.KITCHENWARE, new FloorThing("Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS)),
	MUG_GLASS_FULL(Tab.KITCHENWARE, new FloorThing("Full Glass Mug", CustomMaterial.KITCHENWARE_MUG_GLASS_FULL)),
	MUG_WOODEN(Tab.KITCHENWARE, new FloorThing("Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN)),
	MUG_WOODEN_FULL(Tab.KITCHENWARE, new FloorThing("Full Wooden Mug", CustomMaterial.KITCHENWARE_MUG_WOODEN_FULL)),
	GLASSWARE_GROUP_1(Tab.KITCHENWARE, new FloorThing("Random Glassware 1", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_1)),
	GLASSWARE_GROUP_2(Tab.KITCHENWARE, new FloorThing("Random Glassware 2", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_2)),
	GLASSWARE_GROUP_3(Tab.KITCHENWARE, new FloorThing("Random Glassware 3", CustomMaterial.KITCHENWARE_GLASSWARE_GROUP_3)),
	JAR(Tab.KITCHENWARE, new FloorThing("Jar", CustomMaterial.KITCHENWARE_JAR)),
	JAR_HONEY(Tab.KITCHENWARE, new FloorThing("Honey Jar", CustomMaterial.KITCHENWARE_JAR_HONEY)),
	JAR_COOKIES(Tab.KITCHENWARE, new FloorThing("Cookie Jar", CustomMaterial.KITCHENWARE_JAR_COOKIES)),
	JAR_WIDE(Tab.KITCHENWARE, new FloorThing("Wide Jar", CustomMaterial.KITCHENWARE_JAR_WIDE)),
	BOWL(Tab.KITCHENWARE, new FloorThing("Wooden Bowl", CustomMaterial.KITCHENWARE_BOWL)),
	MIXING_BOWL(Tab.KITCHENWARE, new FloorThing("Mixing Bowl", CustomMaterial.KITCHENWARE_MIXING_BOWL)),
	PAN_CAKE(Tab.KITCHENWARE, new FloorThing("Cake Pan", CustomMaterial.KITCHENWARE_PAN_CAKE)),
	PAN_CASSEROLE(Tab.KITCHENWARE, new FloorThing("Casserole Pan", CustomMaterial.KITCHENWARE_PAN_CASSEROLE)),
	PAN_COOKIE(Tab.KITCHENWARE, new FloorThing("Cookie Pan", CustomMaterial.KITCHENWARE_PAN_COOKIE)),
	PAN_MUFFIN(Tab.KITCHENWARE, new FloorThing("Muffin Pan", CustomMaterial.KITCHENWARE_PAN_MUFFIN)),
	PAN_PIE(Tab.KITCHENWARE, new FloorThing("Pie Pan", CustomMaterial.KITCHENWARE_PAN_PIE)),

	// 	Appliances
	APPLIANCE_FRIDGE(Tab.FURNITURE, new Fridge("Fridge", CustomMaterial.APPLIANCE_FRIDGE, FridgeSize.STANDARD)),
	APPLIANCE_FRIDGE_MAGNETS(Tab.FURNITURE, new Fridge("Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MAGNETS, FridgeSize.STANDARD)),
	APPLIANCE_FRIDGE_TALL(Tab.FURNITURE, new Fridge("Tall Fridge", CustomMaterial.APPLIANCE_FRIDGE_TALL, FridgeSize.TALL)),
	APPLIANCE_FRIDGE_TALL_MAGNETS(Tab.FURNITURE, new Fridge("Tall Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_TALL_MAGNETS, FridgeSize.TALL)),
	APPLIANCE_FRIDGE_MINI(Tab.FURNITURE, new Fridge("Mini Fridge", CustomMaterial.APPLIANCE_FRIDGE_MINI, FridgeSize.MINI)),
	APPLIANCE_FRIDGE_MINI_MAGNETS(Tab.FURNITURE, new Fridge("Mini Fridge With Magnets", CustomMaterial.APPLIANCE_FRIDGE_MINI_MAGNETS, FridgeSize.MINI)),
	APPLIANCE_SLUSHIE_MACHINE(Tab.FURNITURE, new DyeableFloorThing("Slushie Machine", CustomMaterial.APPLIANCE_SLUSHIE_MACHINE, ColorableType.DYE, Hitbox.single())),
	APPLIANCE_GRILL_COMMERCIAL(Tab.FURNITURE, new Block("Commercial Grill", CustomMaterial.APPLIANCE_GRILL_COMMERCIAL, RotationType.BOTH)),
	APPLIANCE_OVEN_COMMERCIAL(Tab.FURNITURE, new Block("Commercial Oven", CustomMaterial.APPLIANCE_OVEN_COMMERCIAL, RotationType.BOTH)),
	APPLIANCE_DEEP_FRYER_COMMERCIAL(Tab.FURNITURE, new Block("Commercial Deep Fryer", CustomMaterial.APPLIANCE_DEEP_FRYER_COMMERCIAL, RotationType.BOTH)),

	// Counters - STEEL HANDLES
	COUNTER_STEEL_MARBLE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_ISLAND, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.ISLAND)),
	COUNTER_STEEL_MARBLE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_CORNER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CORNER)),
	COUNTER_STEEL_MARBLE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_DRAWER, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.DRAWER)),
	COUNTER_STEEL_MARBLE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_CABINET, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.CABINET)),
	COUNTER_STEEL_MARBLE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_OVEN, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.OVEN)),
	COUNTER_STEEL_MARBLE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_SINK, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.SINK)),
	COUNTER_STEEL_MARBLE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_MARBLE_BAR, HandleType.STEEL, CounterMaterial.MARBLE, CounterType.BAR)),

	COUNTER_STEEL_SOAPSTONE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_ISLAND, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),
	COUNTER_STEEL_SOAPSTONE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_CORNER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CORNER)),
	COUNTER_STEEL_SOAPSTONE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_DRAWER, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),
	COUNTER_STEEL_SOAPSTONE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_CABINET, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.CABINET)),
	COUNTER_STEEL_SOAPSTONE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_OVEN, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.OVEN)),
	COUNTER_STEEL_SOAPSTONE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_SINK, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.SINK)),
	COUNTER_STEEL_SOAPSTONE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_SOAPSTONE_BAR, HandleType.STEEL, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	COUNTER_STEEL_STONE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_STONE_ISLAND, HandleType.STEEL, CounterMaterial.STONE, CounterType.ISLAND)),
	COUNTER_STEEL_STONE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_STONE_CORNER, HandleType.STEEL, CounterMaterial.STONE, CounterType.CORNER)),
	COUNTER_STEEL_STONE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_STONE_DRAWER, HandleType.STEEL, CounterMaterial.STONE, CounterType.DRAWER)),
	COUNTER_STEEL_STONE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_STONE_CABINET, HandleType.STEEL, CounterMaterial.STONE, CounterType.CABINET)),
	COUNTER_STEEL_STONE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_STONE_OVEN, HandleType.STEEL, CounterMaterial.STONE, CounterType.OVEN)),
	COUNTER_STEEL_STONE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_STONE_SINK, HandleType.STEEL, CounterMaterial.STONE, CounterType.SINK)),
	COUNTER_STEEL_STONE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_STONE_BAR, HandleType.STEEL, CounterMaterial.STONE, CounterType.BAR)),

	COUNTER_STEEL_WOODEN_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_ISLAND, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.ISLAND)),
	COUNTER_STEEL_WOODEN_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_CORNER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CORNER)),
	COUNTER_STEEL_WOODEN_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_DRAWER, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.DRAWER)),
	COUNTER_STEEL_WOODEN_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_CABINET, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.CABINET)),
	COUNTER_STEEL_WOODEN_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_OVEN, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.OVEN)),
	COUNTER_STEEL_WOODEN_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_SINK, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.SINK)),
	COUNTER_STEEL_WOODEN_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_STEEL_WOODEN_BAR, HandleType.STEEL, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - BRASS HANDLES
	COUNTER_BRASS_MARBLE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_ISLAND, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.ISLAND)),
	COUNTER_BRASS_MARBLE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_CORNER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CORNER)),
	COUNTER_BRASS_MARBLE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_DRAWER, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.DRAWER)),
	COUNTER_BRASS_MARBLE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_CABINET, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.CABINET)),
	COUNTER_BRASS_MARBLE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_OVEN, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.OVEN)),
	COUNTER_BRASS_MARBLE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_SINK, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.SINK)),
	COUNTER_BRASS_MARBLE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_MARBLE_BAR, HandleType.BRASS, CounterMaterial.MARBLE, CounterType.BAR)),

	COUNTER_BRASS_SOAPSTONE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_ISLAND, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),
	COUNTER_BRASS_SOAPSTONE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_CORNER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CORNER)),
	COUNTER_BRASS_SOAPSTONE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_DRAWER, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),
	COUNTER_BRASS_SOAPSTONE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_CABINET, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.CABINET)),
	COUNTER_BRASS_SOAPSTONE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_OVEN, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.OVEN)),
	COUNTER_BRASS_SOAPSTONE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_SINK, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.SINK)),
	COUNTER_BRASS_SOAPSTONE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_SOAPSTONE_BAR, HandleType.BRASS, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	COUNTER_BRASS_STONE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_STONE_ISLAND, HandleType.BRASS, CounterMaterial.STONE, CounterType.ISLAND)),
	COUNTER_BRASS_STONE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_STONE_CORNER, HandleType.BRASS, CounterMaterial.STONE, CounterType.CORNER)),
	COUNTER_BRASS_STONE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_STONE_DRAWER, HandleType.BRASS, CounterMaterial.STONE, CounterType.DRAWER)),
	COUNTER_BRASS_STONE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_STONE_CABINET, HandleType.BRASS, CounterMaterial.STONE, CounterType.CABINET)),
	COUNTER_BRASS_STONE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_STONE_OVEN, HandleType.BRASS, CounterMaterial.STONE, CounterType.OVEN)),
	COUNTER_BRASS_STONE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_STONE_SINK, HandleType.BRASS, CounterMaterial.STONE, CounterType.SINK)),
	COUNTER_BRASS_STONE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_STONE_BAR, HandleType.BRASS, CounterMaterial.STONE, CounterType.BAR)),

	COUNTER_BRASS_WOODEN_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_ISLAND, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.ISLAND)),
	COUNTER_BRASS_WOODEN_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_CORNER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CORNER)),
	COUNTER_BRASS_WOODEN_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_DRAWER, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.DRAWER)),
	COUNTER_BRASS_WOODEN_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_CABINET, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.CABINET)),
	COUNTER_BRASS_WOODEN_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_OVEN, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.OVEN)),
	COUNTER_BRASS_WOODEN_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_SINK, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.SINK)),
	COUNTER_BRASS_WOODEN_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BRASS_WOODEN_BAR, HandleType.BRASS, CounterMaterial.WOODEN, CounterType.BAR)),

	// Counters - LACK HANDLES
	COUNTER_BLACK_MARBLE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_ISLAND, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.ISLAND)),
	COUNTER_BLACK_MARBLE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_CORNER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CORNER)),
	COUNTER_BLACK_MARBLE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_DRAWER, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.DRAWER)),
	COUNTER_BLACK_MARBLE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_CABINET, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.CABINET)),
	COUNTER_BLACK_MARBLE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_OVEN, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.OVEN)),
	COUNTER_BLACK_MARBLE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_SINK, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.SINK)),
	COUNTER_BLACK_MARBLE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_MARBLE_BAR, HandleType.BLACK, CounterMaterial.MARBLE, CounterType.BAR)),

	COUNTER_BLACK_SOAPSTONE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_ISLAND, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.ISLAND)),
	COUNTER_BLACK_SOAPSTONE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CORNER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CORNER)),
	COUNTER_BLACK_SOAPSTONE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_DRAWER, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.DRAWER)),
	COUNTER_BLACK_SOAPSTONE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.CABINET)),
	COUNTER_BLACK_SOAPSTONE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_OVEN, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.OVEN)),
	COUNTER_BLACK_SOAPSTONE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_SINK, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.SINK)),
	COUNTER_BLACK_SOAPSTONE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_SOAPSTONE_BAR, HandleType.BLACK, CounterMaterial.SOAPSTONE, CounterType.BAR)),

	COUNTER_BLACK_STONE_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_STONE_ISLAND, HandleType.BLACK, CounterMaterial.STONE, CounterType.ISLAND)),
	COUNTER_BLACK_STONE_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_STONE_CORNER, HandleType.BLACK, CounterMaterial.STONE, CounterType.CORNER)),
	COUNTER_BLACK_STONE_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_STONE_DRAWER, HandleType.BLACK, CounterMaterial.STONE, CounterType.DRAWER)),
	COUNTER_BLACK_STONE_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_STONE_CABINET, HandleType.BLACK, CounterMaterial.STONE, CounterType.CABINET)),
	COUNTER_BLACK_STONE_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_STONE_OVEN, HandleType.BLACK, CounterMaterial.STONE, CounterType.OVEN)),
	COUNTER_BLACK_STONE_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_STONE_SINK, HandleType.BLACK, CounterMaterial.STONE, CounterType.SINK)),
	COUNTER_BLACK_STONE_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_STONE_BAR, HandleType.BLACK, CounterMaterial.STONE, CounterType.BAR)),

	COUNTER_BLACK_WOODEN_ISLAND(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_ISLAND, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.ISLAND)),
	COUNTER_BLACK_WOODEN_CORNER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_CORNER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CORNER)),
	COUNTER_BLACK_WOODEN_DRAWER(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_DRAWER, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.DRAWER)),
	COUNTER_BLACK_WOODEN_CABINET(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_CABINET, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.CABINET)),
	COUNTER_BLACK_WOODEN_OVEN(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_OVEN, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.OVEN)),
	COUNTER_BLACK_WOODEN_SINK(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_SINK, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.SINK)),
	COUNTER_BLACK_WOODEN_BAR(Tab.FURNITURE, new Counter(CustomMaterial.COUNTER_BLACK_WOODEN_BAR, HandleType.BLACK, CounterMaterial.WOODEN, CounterType.BAR)),

	// Cabinets - STEEL HANDLES
	CABINET_STEEL_WOODEN(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.CABINET)),
	CABINET_STEEL_WOODEN_CORNER(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.CORNER)),
	CABINET_STEEL_WOODEN_HOOD(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_STEEL_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.STEEL, CabinetType.HOOD)),

	// Cabinets - BRASS HANDLES
	CABINET_BRASS_WOODEN(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.CABINET)),
	CABINET_BRASS_WOODEN_CORNER(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.CORNER)),
	CABINET_BRASS_WOODEN_HOOD(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_BRASS_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BRASS, CabinetType.HOOD)),

	// Cabinets - BLACK HANDLES
	CABINET_BLACK_WOODEN(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.CABINET)),
	CABINET_BLACK_WOODEN_CORNER(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN_CORNER, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.CORNER)),
	CABINET_BLACK_WOODEN_HOOD(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_BLACK_WOODEN_HOOD, CabinetMaterial.WOODEN, HandleType.BLACK, CabinetType.HOOD)),

	// Cabinets - GENERIC
	CABINET_HOOD(Tab.FURNITURE, new Cabinet(CustomMaterial.CABINET_HOOD, CabinetMaterial.NONE, HandleType.NONE, CabinetType.HOOD)),

	// 	???
	TOILET_MODERN(new DyeableFloorThing("Toilet Modern", CustomMaterial.TOILET_MODERN, ColorableType.DYE, Hitbox.single())),

	WARDROBE(Tab.FURNITURE, new Furniture("Wardrobe", CustomMaterial.WARDROBE, FurnitureSurface.FLOOR, FurnitureSize._2x3V)),
	CUPBOARD_SHORT(Tab.FURNITURE, new Furniture("Short Cupboard", CustomMaterial.CUPBOARD_SHORT, FurnitureSurface.FLOOR, FurnitureSize._1x2H)),
	CUPBOARD_SHORT_BOOKSHELF(Tab.FURNITURE, new Furniture("Short Bookshelf Cupboard", CustomMaterial.CUPBOARD_SHORT_BOOKSHELF, FurnitureSurface.FLOOR, FurnitureSize._1x2H)),
	SHELF_WALL(Tab.FURNITURE, new Furniture("Wall Shelf", CustomMaterial.SHELF_WALL, FurnitureSurface.WALL, FurnitureSize._1x2H)),
	SHELF_STORAGE(Tab.FURNITURE, new Furniture("Storage Shelf", CustomMaterial.SHELF_STORAGE, FurnitureSurface.FLOOR, FurnitureSize._2x3V, true)),

	//	Art
	ART_PAINTING_CHERRY_FOREST(Tab.ART, new Art("Cherry Forest Painting", CustomMaterial.ART_PAINTING_CHERRY_FOREST, ArtSize._1x2v)),
	ART_PAINTING_END_ISLAND(Tab.ART, new Art("End Island Painting", CustomMaterial.ART_PAINTING_END_ISLAND, ArtSize._1x2v)),
	ART_PAINTING_LOST_ENDERMAN(Tab.ART, new Art("Lost Enderman Painting", CustomMaterial.ART_PAINTING_LOST_ENDERMAN, ArtSize._1x2v)),
	ART_PAINTING_PINE_TREE(Tab.ART, new Art("Pine Tree Painting", CustomMaterial.ART_PAINTING_PINE_TREE, ArtSize._1x2v)),
	ART_PAINTING_SUNSET(Tab.ART, new Art("Sunset Painting", CustomMaterial.ART_PAINTING_SUNSET, ArtSize._1x2v)),
	ART_PAINTING_SWAMP_HUT(Tab.ART, new Art("Swamp Hut Painting", CustomMaterial.ART_PAINTING_SWAMP_HUT, ArtSize._1x2v)),

	ART_PAINTING_MOUNTAINS(Tab.ART, new Art("Mountains Painting", CustomMaterial.ART_PAINTING_MOUNTAINS, ArtSize._1x2h)),
	ART_PAINTING_MUDDY_PIG(Tab.ART, new Art("Muddy Pig Painting", CustomMaterial.ART_PAINTING_MUDDY_PIG, ArtSize._1x2h)),
	ART_PAINTING_PURPLE_SHEEP(Tab.ART, new Art("Purple Sheep Painting", CustomMaterial.ART_PAINTING_PURPLE_SHEEP, ArtSize._1x2h)),
	ART_PAINTING_VILLAGE_HAPPY(Tab.ART, new Art("Happy Village Painting", CustomMaterial.ART_PAINTING_VILLAGE_HAPPY, ArtSize._1x2h)),
	ART_PAINTING_VILLAGE_CHAOS(Tab.ART, new Art("Chaos Village Painting", CustomMaterial.ART_PAINTING_VILLAGE_CHAOS, ArtSize._1x2h)),

	ART_PAINTING_SKYBLOCK(Tab.ART, new Art("Skyblock Painting", CustomMaterial.ART_PAINTING_SKYBLOCK, ArtSize._1x1)),
	ART_PAINTING_NETHER_FORTRESS_BRIDGE(Tab.ART, new Art("Nether Fortress Bridge Painting", CustomMaterial.ART_PAINTING_NETHER_FORTRESS_BRIDGE, ArtSize._1x1)),
	ART_PAINTING_NETHER_CRIMSON_FOREST(Tab.ART, new Art("Nether Crimson Forest Painting", CustomMaterial.ART_PAINTING_NETHER_CRIMSON_FOREST, ArtSize._1x1)),
	ART_PAINTING_NETHER_WARPED_FOREST(Tab.ART, new Art("Nether Warped Forest Painting", CustomMaterial.ART_PAINTING_NETHER_WARPED_FOREST, ArtSize._1x1)),
	ART_PAINTING_NETHER_BASALT_DELTAS(Tab.ART, new Art("Nether Basalt Deltas Painting", CustomMaterial.ART_PAINTING_NETHER_BASALT_DELTAS, ArtSize._1x1)),
	ART_PAINTING_NETHER_SOUL_SAND_VALLEY(Tab.ART, new Art("Nether Soul Sand Valley Painting", CustomMaterial.ART_PAINTING_NETHER_SOUL_SAND_VALLEY, ArtSize._1x1)),

	ART_PAINTING_CASTLE(Tab.ART, new Art("Castle Painting", CustomMaterial.ART_PAINTING_CASTLE, ArtSize._2x2)),
	ART_PAINTING_LAKE(Tab.ART, new Art("Lake Painting", CustomMaterial.ART_PAINTING_LAKE, ArtSize._2x2)),
	ART_PAINTING_RIVER(Tab.ART, new Art("River Painting", CustomMaterial.ART_PAINTING_RIVER, ArtSize._2x2)),
	ART_PAINTING_ROAD(Tab.ART, new Art("Road Painting", CustomMaterial.ART_PAINTING_ROAD, ArtSize._2x2)),
	ART_PAINTING_ORIENTAL(Tab.ART, new Art("Oriental Painting", CustomMaterial.ART_PAINTING_ORIENTAL, ArtSize._2x2)),
	ART_PAINTING_CHICKENS(Tab.ART, new Art("Chickens Painting", CustomMaterial.ART_PAINTING_CHICKENS, ArtSize._2x2)),
	ART_PAINTING_OAK_TREE(Tab.ART, new Art("Oak Tree Painting", CustomMaterial.ART_PAINTING_OAK_TREE, ArtSize._2x2)),
	ART_PAINTING_CRAB(Tab.ART, new Art("Crab Painting", CustomMaterial.ART_PAINTING_CRAB, ArtSize._2x2)),
	ART_PAINTING_SATURN_ROCKET(Tab.ART, new Art("Saturn Rocket Painting", CustomMaterial.ART_PAINTING_SATURN_ROCKET, ArtSize._2x2)),
	ART_PAINTING_PARROT(Tab.ART, new Art("Oak Tree Painting", CustomMaterial.ART_PAINTING_PARROT, ArtSize._2x2)),
	ART_PAINTING_DUCKS(Tab.ART, new Art("Ducks Painting", CustomMaterial.ART_PAINTING_DUCKS, ArtSize._2x2)),
	ART_PAINTING_STARRY_PINE_TREE(Tab.ART, new Art("Starry Pine Tree Painting", CustomMaterial.ART_PAINTING_STARRY_PINE_TREE, ArtSize._2x2)),

	ART_PAINTING_FOREST(Tab.ART, new Art("Forest Painting", CustomMaterial.ART_PAINTING_FOREST, ArtSize._1x3h)),

	ART_PAINTING_SAND_DUNES(Tab.ART, new Art("Sand Dunes Painting", CustomMaterial.ART_PAINTING_SAND_DUNES, ArtSize._1x3v)),

	ART_PAINTING_STORY(Tab.ART, new Art("Story Painting", CustomMaterial.ART_PAINTING_STORY, ArtSize._2x3h)),

	//	Potions
	POTION_FILLED_TINY_1(Tab.POTIONS, new DyeableFloorThing("Tiny Potions 1", CustomMaterial.POTION_FILLED_TINY_1, ColorableType.DYE)),
	POTION_FILLED_TINY_2(Tab.POTIONS, new DyeableFloorThing("Tiny Potions 2", CustomMaterial.POTION_FILLED_TINY_2, ColorableType.DYE)),
	POTION_FILLED_SMALL_1(Tab.POTIONS, new DyeableFloorThing("Small Potion 1", CustomMaterial.POTION_FILLED_SMALL_1, ColorableType.DYE)),
	POTION_FILLED_SMALL_2(Tab.POTIONS, new DyeableFloorThing("Small Potion 2", CustomMaterial.POTION_FILLED_SMALL_2, ColorableType.DYE)),
	POTION_FILLED_SMALL_3(Tab.POTIONS, new DyeableFloorThing("Small Potion 3", CustomMaterial.POTION_FILLED_SMALL_3, ColorableType.DYE)),
	POTION_FILLED_MEDIUM_1(Tab.POTIONS, new DyeableFloorThing("Medium Potion 1", CustomMaterial.POTION_FILLED_MEDIUM_1, ColorableType.DYE)),
	POTION_FILLED_MEDIUM_2(Tab.POTIONS, new DyeableFloorThing("Medium Potion 2", CustomMaterial.POTION_FILLED_MEDIUM_2, ColorableType.DYE)),
	POTION_FILLED_WIDE(Tab.POTIONS, new DyeableFloorThing("Wide Potion", CustomMaterial.POTION_FILLED_WIDE, ColorableType.DYE)),
	POTION_FILLED_SKINNY(Tab.POTIONS, new DyeableFloorThing("Skinny Potion", CustomMaterial.POTION_FILLED_SKINNY, ColorableType.DYE)),
	POTION_FILLED_TALL(Tab.POTIONS, new DyeableFloorThing("Tall Potion", CustomMaterial.POTION_FILLED_TALL, ColorableType.DYE)),
	POTION_FILLED_BIG_BOTTLE(Tab.POTIONS, new DyeableFloorThing("Big Potion Bottle", CustomMaterial.POTION_FILLED_BIG_BOTTLE, ColorableType.DYE)),
	POTION_FILLED_BIG_TEAR(Tab.POTIONS, new DyeableFloorThing("Big Potion Tear", CustomMaterial.POTION_FILLED_BIG_TEAR, ColorableType.DYE)),
	POTION_FILLED_BIG_DONUT(Tab.POTIONS, new DyeableFloorThing("Big Potion Donut", CustomMaterial.POTION_FILLED_BIG_DONUT, ColorableType.DYE)),
	POTION_FILLED_BIG_SKULL(Tab.POTIONS, new DyeableFloorThing("Big Potion Skull", CustomMaterial.POTION_FILLED_BIG_SKULL, ColorableType.DYE)),
	POTION_FILLED_GROUP_SMALL(Tab.POTIONS, new DyeableFloorThing("Small Potions", CustomMaterial.POTION_FILLED_GROUP_SMALL, ColorableType.DYE)),
	POTION_FILLED_GROUP_MEDIUM(Tab.POTIONS, new DyeableFloorThing("Medium Potions", CustomMaterial.POTION_FILLED_GROUP_MEDIUM, ColorableType.DYE)),
	POTION_FILLED_GROUP_TALL(Tab.POTIONS, new DyeableFloorThing("Tall Potions", CustomMaterial.POTION_FILLED_GROUP_TALL, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_1(Tab.POTIONS, new DyeableFloorThing("Random Potions 1", CustomMaterial.POTION_FILLED_GROUP_RANDOM_1, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_2(Tab.POTIONS, new DyeableFloorThing("Random Potions 2", CustomMaterial.POTION_FILLED_GROUP_RANDOM_2, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_3(Tab.POTIONS, new DyeableFloorThing("Random Potions 3", CustomMaterial.POTION_FILLED_GROUP_RANDOM_3, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_4(Tab.POTIONS, new DyeableFloorThing("Random Potions 4", CustomMaterial.POTION_FILLED_GROUP_RANDOM_4, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_5(Tab.POTIONS, new DyeableFloorThing("Random Potions 5", CustomMaterial.POTION_FILLED_GROUP_RANDOM_5, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_6(Tab.POTIONS, new DyeableFloorThing("Random Potions 6", CustomMaterial.POTION_FILLED_GROUP_RANDOM_6, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_7(Tab.POTIONS, new DyeableFloorThing("Random Potions 7", CustomMaterial.POTION_FILLED_GROUP_RANDOM_7, ColorableType.DYE)),
	POTION_FILLED_GROUP_RANDOM_8(Tab.POTIONS, new DyeableFloorThing("Random Potions 8", CustomMaterial.POTION_FILLED_GROUP_RANDOM_8, ColorableType.DYE)),

	POTION_EMPTY_SMALL_1(Tab.POTIONS, new DyeableFloorThing("Empty Small Potion 1", CustomMaterial.POTION_EMPTY_SMALL_1, ColorableType.DYE)),
	POTION_EMPTY_SMALL_2(Tab.POTIONS, new DyeableFloorThing("Empty Small Potion 2", CustomMaterial.POTION_EMPTY_SMALL_2, ColorableType.DYE)),
	POTION_EMPTY_SMALL_3(Tab.POTIONS, new DyeableFloorThing("Empty Small Potion 3", CustomMaterial.POTION_EMPTY_SMALL_3, ColorableType.DYE)),
	POTION_EMPTY_MEDIUM_1(Tab.POTIONS, new DyeableFloorThing("Empty Medium Potion 1", CustomMaterial.POTION_EMPTY_MEDIUM_1, ColorableType.DYE)),
	POTION_EMPTY_MEDIUM_2(Tab.POTIONS, new DyeableFloorThing("Empty Medium Potion 2", CustomMaterial.POTION_EMPTY_MEDIUM_2, ColorableType.DYE)),
	POTION_EMPTY_WIDE(Tab.POTIONS, new DyeableFloorThing("Empty Wide Potion", CustomMaterial.POTION_EMPTY_WIDE, ColorableType.DYE)),
	POTION_EMPTY_SKINNY(Tab.POTIONS, new DyeableFloorThing("Empty Skinny Potion", CustomMaterial.POTION_EMPTY_SKINNY, ColorableType.DYE)),
	POTION_EMPTY_TALL(Tab.POTIONS, new DyeableFloorThing("Empty Tall Potion", CustomMaterial.POTION_EMPTY_TALL, ColorableType.DYE)),
	POTION_EMPTY_BIG_BOTTLE(Tab.POTIONS, new DyeableFloorThing("Empty Big Potion Bottle", CustomMaterial.POTION_EMPTY_BIG_BOTTLE, ColorableType.DYE)),
	POTION_EMPTY_BIG_TEAR(Tab.POTIONS, new DyeableFloorThing("Empty Big Potion Tear", CustomMaterial.POTION_EMPTY_BIG_TEAR, ColorableType.DYE)),
	POTION_EMPTY_BIG_DONUT(Tab.POTIONS, new DyeableFloorThing("Empty Big Potion Donut", CustomMaterial.POTION_EMPTY_BIG_DONUT, ColorableType.DYE)),
	POTION_EMPTY_BIG_SKULL(Tab.POTIONS, new DyeableFloorThing("Empty Big Potion Skull", CustomMaterial.POTION_EMPTY_BIG_SKULL, ColorableType.DYE)),
	POTION_EMPTY_GROUP_SMALL(Tab.POTIONS, new DyeableFloorThing("Empty Small Potions", CustomMaterial.POTION_EMPTY_GROUP_SMALL, ColorableType.DYE)),
	POTION_EMPTY_GROUP_MEDIUM(Tab.POTIONS, new DyeableFloorThing("Empty Medium Potions", CustomMaterial.POTION_EMPTY_GROUP_MEDIUM, ColorableType.DYE)),
	POTION_EMPTY_GROUP_TALL(Tab.POTIONS, new DyeableFloorThing("Empty Tall Potions", CustomMaterial.POTION_EMPTY_GROUP_TALL, ColorableType.DYE)),

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
	MAILBOX(new DyeableFloorThing("Mailbox", CustomMaterial.MAILBOX, ColorableType.DYE, "C7C7C7", List.of(Hitbox.origin(), Hitbox.offset(BlockFace.UP)))),
	SANDWICH_SIGN(new FloorThing("Sandwich Sign", CustomMaterial.SANDWICH_SIGN)),
	SANDWICH_SIGN_TALL(new FloorThing("Sandwhich Sign Tall", CustomMaterial.SANDWICH_SIGN_TALL)),
	FIRE_HYDRANT(new FloorThing("Fire Hydrant", CustomMaterial.FIRE_HYDRANT)),
	WAYSTONE(new FloorThing("Waystone", CustomMaterial.WAYSTONE)),
	WAYSTONE_ACTIVATED(new FloorThing("Waystone Activated", CustomMaterial.WAYSTONE_ACTIVATED)),

	// Testing
	TEST(Tab.INVISIBLE, new TestThing("Test Thing", CustomMaterial.WAYSTONE_ACTIVATED)),
	;

	@Getter
	private final Theme theme;
	@Getter
	private final Tab tab;
	@Getter
	private final DecorationConfig config;

	DecorationType(DecorationConfig config) {
		this.config = config;
		this.theme = Theme.GENERAL;
		this.tab = Tab.NONE;
	}

	DecorationType(Tab tab, DecorationConfig config) {
		this.config = config;
		this.theme = Theme.GENERAL;
		this.tab = tab;
	}

	DecorationType(Theme theme, DecorationConfig config) {
		this.config = config;
		this.theme = theme;
		this.tab = Tab.NONE;
	}

	public static void init() {}

	public static List<DecorationType> getBy(Tab tab, Theme theme) {
		return Arrays.stream(values())
			.filter(decorationType -> decorationType.getTab() == tab)
			.filter(decorationType -> decorationType.getTheme() == theme)
			.toList();
	}

}
