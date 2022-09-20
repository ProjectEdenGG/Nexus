package gg.projecteden.nexus.features.resourcepack.models;

import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Getter
@AllArgsConstructor
public enum CustomMaterial {
	PRESENCE_OFFLINE(Material.PAPER, 25000),
	PRESENCE_ACTIVE(Material.PAPER, 25001),

	CRATE_KEY_VOTE(Material.PAPER, 10000),
	CRATE_KEY_WITHER(Material.PAPER, 10001),
	CRATE_KEY_MYSTERY(Material.PAPER, 10002),
	CRATE_KEY_WAKKA(Material.PAPER, 10003),
	CRATE_KEY_BEARFAIR(Material.PAPER, 10004),
	CRATE_KEY_PUGMAS(Material.PAPER, 10005),
	CRATE_KEY_HALLOWEEN(Material.PAPER, 10006),
	CRATE_KEY_EASTER(Material.PAPER, 10007),
	CRATE_KEY_MINIGAMES(Material.PAPER, 10008),
	CRATE_KEY_PRIDE(Material.PAPER, 10009),
	CRATE_KEY_DRAGON(Material.PAPER, 10010),

	GOLD_COINS_1(Material.PAPER, 25200),
	GOLD_COINS_2(Material.PAPER, 25201),
	GOLD_COINS_3(Material.PAPER, 25202),
	GOLD_COINS_4(Material.PAPER, 25203),
	GOLD_COINS_5(Material.PAPER, 25204),
	GOLD_COINS_6(Material.PAPER, 25205),
	GOLD_COINS_7(Material.PAPER, 25206),
	GOLD_COINS_8(Material.PAPER, 25207),
	GOLD_COINS_9(Material.PAPER, 25208),

	SILVER_COINS_1(Material.PAPER, 25210),
	SILVER_COINS_2(Material.PAPER, 25211),
	SILVER_COINS_3(Material.PAPER, 25212),
	SILVER_COINS_4(Material.PAPER, 25213),
	SILVER_COINS_5(Material.PAPER, 25214),
	SILVER_COINS_6(Material.PAPER, 25215),
	SILVER_COINS_7(Material.PAPER, 25216),
	SILVER_COINS_8(Material.PAPER, 25217),
	SILVER_COINS_9(Material.PAPER, 25218),

	BACKPACK(Material.SHULKER_BOX, 1),
	MOB_NET(Material.PAPER, 5904),
	INFINITE_WATER_BUCKET(Material.PAPER, 5903),

	ARMOR_OUTLINE_HELMET(Material.PAPER, 1501),
	ARMOR_OUTLINE_CHESTPLATE(Material.PAPER, 1502),
	ARMOR_OUTLINE_LEGGINGS(Material.PAPER, 1503),
	ARMOR_OUTLINE_BOOTS(Material.PAPER, 1504),
	ARMOR_FILLED_HELMET(Material.PAPER, 1505),
	ARMOR_FILLED_CHESTPLATE(Material.PAPER, 1506),
	ARMOR_FILLED_LEGGINGS(Material.PAPER, 1507),
	ARMOR_FILLED_BOOTS(Material.PAPER, 1508),

	EMOJI_100(Material.PAPER, 1320),
	EVENT_TOKEN(Material.PAPER, 1510),
	ENVELOPE_1(Material.PAPER, 2050),
	ENVELOPE_2(Material.PAPER, 2051),
	SAND_PAPER(Material.PAPER, 2057),
	RED_SAND_PAPER(Material.PAPER, 2058),
	INVISIBLE(Material.PAPER, 8500),
	SAW_HALF(Material.PAPER, 6212),
	SAW_FULL(Material.PAPER, 6213),
	EXCLAMATION(Material.PAPER, 6214),
	CHAT_GAMES(Material.PAPER, 1511),

	UI_NUMBERS_0(Material.LEATHER_HORSE_ARMOR, 2000),
	IMAGES_OUTLINE_4x3(Material.PAPER, 1299),

	WITHER_FRAGMENT(Material.PAPER, 2056),
	WITHER_HELMET(Material.IRON_HELMET, 1),
	WITHER_CHESTPLATE(Material.IRON_CHESTPLATE, 1),
	WITHER_LEGGINGS(Material.IRON_LEGGINGS, 1),
	WITHER_BOOTS(Material.IRON_BOOTS, 1),

	DYE_STATION(Material.PAPER, 5900),
	DYE_STATION_DYE(Material.PAPER, 2053),
	DYE_STATION_STAIN(Material.PAPER, 2054),
	DYE_STATION_BUTTON_DYE(Material.LEATHER_HORSE_ARMOR, 1),
	DYE_STATION_BUTTON_STAIN(Material.LEATHER_HORSE_ARMOR, 2),

	TRASH_CAN(Material.LEATHER_HORSE_ARMOR, 100),

	WINDCHIMES_IRON(Material.PAPER, 5000),
	WINDCHIMES_GOLD(Material.PAPER, 5001),
	WINDCHIMES_COPPER(Material.PAPER, 5002),
	WINDCHIMES_AMETHYST(Material.PAPER, 5003),
	WINDCHIMES_LAPIS(Material.PAPER, 5004),
	WINDCHIMES_NETHERITE(Material.PAPER, 5005),
	WINDCHIMES_DIAMOND(Material.PAPER, 5006),
	WINDCHIMES_REDSTONE(Material.PAPER, 5007),
	WINDCHIMES_EMERALD(Material.PAPER, 5008),
	WINDCHIMES_QUARTZ(Material.PAPER, 5009),
	WINDCHIMES_COAL(Material.PAPER, 5010),
	WINDCHIMES_ICE(Material.PAPER, 5011),

	BIRDHOUSE_FOREST_HORIZONTAL(Material.PAPER, 5020),
	BIRDHOUSE_FOREST_VERTICAL(Material.PAPER, 5021),
	BIRDHOUSE_FOREST_HANGING(Material.PAPER, 5022),
	BIRDHOUSE_ENCHANTED_HORIZONTAL(Material.PAPER, 5023),
	BIRDHOUSE_ENCHANTED_VERTICAL(Material.PAPER, 5024),
	BIRDHOUSE_ENCHANTED_HANGING(Material.PAPER, 5025),
	BIRDHOUSE_DEPTHS_HORIZONTAL(Material.PAPER, 5026),
	BIRDHOUSE_DEPTHS_VERTICAL(Material.PAPER, 5027),
	BIRDHOUSE_DEPTHS_HANGING(Material.PAPER, 5028),

	GEM_SAPPHIRE(Material.PAPER, 9000),
	GEM_BLACK_OPAL(Material.PAPER, 9005),

	ELECTRONICS_MOTHERBOARD(Material.PAPER, 2100),
	ELECTRONICS_SCREEN(Material.PAPER, 2104),
	ELECTRONICS_BATTERY(Material.PAPER, 2105),
	ELECTRONICS_CPU(Material.PAPER, 2106),
	ELECTRONICS_HARD_DRIVE(Material.PAPER, 2107),
	ELECTRONICS_LAPTOP(Material.PAPER, 6805),

	FOOD_COOKIE_TRAY_CHOCOLATE_CHIP(Material.PAPER, 6033),
	FOOD_CANDY_CORN(Material.COOKIE, 110),
	FOOD_CANDY_CHOCOLATE_BAR(Material.COOKIE, 111),
	FOOD_MILK_CARTON(Material.PAPER, 6071),
	FOOD_BAG_OF_FLOUR(Material.PAPER, 6072),

	FOOD_BEETROOT_SOUP(Material.COOKIE, 10000),
	FOOD_MUSHROOM_STEW(Material.COOKIE, 10001),
	FOOD_RABBIT_STEW(Material.COOKIE, 10002),

	SABOTAGE_DIVERT_POWER(Material.PAPER, 7302),
	SABOTAGE_KEY_CARD(Material.PAPER, 7303),
	SABOTAGE_REACTOR_MELTDOWN(Material.PAPER, 7304),
	SABOTAGE_FIX_LIGHTS(Material.PAPER, 7305),

	MINIGOLF_PUTTER(Material.PAPER, 7400),
	MINIGOLF_WEDGE(Material.PAPER, 7402),
	MINIGOLF_WHISTLE(Material.PAPER, 7403),
	MINIGOLF_BALL(Material.SNOWBALL, 901),

	PRIDE21_TROPHY(Material.GOLD_INGOT, 1),
	PRIDE_FLAG_BASE(Material.PAPER, 8000),
	PRIDE_BUNTING_BASE(Material.PAPER, 8050),

	BEARFAIR21_MINIGOLF(Material.GOLD_INGOT, 2),
	BEARFAIR21_CAKE(Material.GOLD_INGOT, 5),
	BEARFAIR21_CARP(Material.PAPER, 4100),
	BEARFAIR21_SALMON(Material.PAPER, 4101),
	BEARFAIR21_TROPICAL_FISH(Material.PAPER, 4102),
	BEARFAIR21_PUFFERFISH(Material.PAPER, 4103),
	BEARFAIR21_BULLHEAD(Material.PAPER, 4104),
	BEARFAIR21_STURGEON(Material.PAPER, 4105),
	BEARFAIR21_WOODSKIP(Material.PAPER, 4106),
	BEARFAIR21_VOID_SALMON(Material.PAPER, 4107),
	BEARFAIR21_RED_SNAPPER(Material.PAPER, 4108),
	BEARFAIR21_RED_MULLET(Material.PAPER, 4109),
	BEARFAIR21_OLD_BOOTS(Material.PAPER, 4120),
	BEARFAIR21_RUSTY_SPOON(Material.PAPER, 4118),
	BEARFAIR21_BROKEN_CD(Material.PAPER, 4121),
	BEARFAIR21_LOST_BOOK(Material.PAPER, 4117),
	BEARFAIR21_SOGGY_NEWSPAPER(Material.PAPER, 4125),
	BEARFAIR21_DRIFTWOOD(Material.PAPER, 4124),
	BEARFAIR21_SEAWEED(Material.PAPER, 4119),
	BEARFAIR21_TREASURE_CHEST(Material.PAPER, 4122),
	BEARFAIR21_MIDNIGHT_CARP(Material.PAPER, 4110),
	BEARFAIR21_SUNFISH(Material.PAPER, 4111),
	BEARFAIR21_STONEFISH(Material.PAPER, 4112),
	BEARFAIR21_TIGER_TROUT(Material.PAPER, 4113),
	BEARFAIR21_SEA_CUCUMBER(Material.PAPER, 4123),
	BEARFAIR21_GLACIERFISH(Material.PAPER, 4114),
	BEARFAIR21_CRIMSONFISH(Material.PAPER, 4115),
	BEARFAIR21_BLOBFISH(Material.PAPER, 4116),
	BEARFAIR21_KEYBOARD(Material.PAPER, 4133),

	BIRTHDAY21_TROPHY(Material.PAPER, 6070),

	PUGMAS_2021_TROPHY(Material.GOLD_INGOT, 3),
	PUGMAS21_TRAIN_1(Material.PAPER, 11001),
	PUGMAS21_HOT_AIR_BALLOON_1(Material.PAPER, 11031),
	PUGMAS21_CANDY_CANE_CANNON(Material.PAPER, 4208),
	PUGMAS21_CANDY_CANE_RED(Material.COOKIE, 100),
	PUGMAS21_CANDY_CANE_GREEN(Material.COOKIE, 101),
	PUGMAS21_CANDY_CANE_YELLOW(Material.COOKIE, 102),
	PUGMAS21_PRESENT_ADVENT(Material.PAPER, 4213),
	PUGMAS21_PRESENT_ADVENT_OPENED(Material.PAPER, 4214),
	PUGMAS21_PRESENT_OUTLINED(Material.PAPER, 4215),
	PUGMAS21_PRESENT_COLORED(Material.PAPER, 4216),
	PUGMAS21_PRESENT_OPENED(Material.PAPER, 4217),
	PUGMAS21_PRESENT_LOCKED(Material.PAPER, 4218),

	EASTER_2022_TROPHY(Material.GOLD_INGOT, 4),
	EASTER22_EASTERS_PAINTBRUSH(Material.PAPER, 4000),
	EASTER22_EASTER_EGG(Material.PAPER, 4001),
	EASTER22_PAINTBRUSH(Material.PAPER, 4021),
	EASTER22_PRISTINE_EGG(Material.PAPER, 4022),
	EASTER22_PAINTED_EGG(Material.PAPER, 4023),

	COSTUMES_GG_HAT(Material.PAPER, 22307),
	COSTUMES_PIRATE_HAT_LEATHER_CAVALIER(Material.PAPER, 22100),
	COSTUMES_PIRATE_HAT_LEATHER_BICORN(Material.PAPER, 22101),
	COSTUMES_PIRATE_HAT_LEATHER_BICORN_SIDE(Material.PAPER, 22102),
	COSTUMES_PIRATE_HAT_LEATHER_TRICORN(Material.PAPER, 22103),
	COSTUMES_PIRATE_HAT_CAVALIER(Material.LEATHER_HORSE_ARMOR, 4000),
	COSTUMES_PIRATE_HAT_BICORN(Material.LEATHER_HORSE_ARMOR, 4001),
	COSTUMES_PIRATE_HAT_BICORN_SIDE(Material.LEATHER_HORSE_ARMOR, 4002),
	COSTUMES_PIRATE_HAT_TRICORN(Material.LEATHER_HORSE_ARMOR, 4003),

	BALLOON_SHORT(Material.LEATHER_HORSE_ARMOR, 3),
	BALLOON_MEDIUM(Material.LEATHER_HORSE_ARMOR, 4),
	BALLOON_TALL(Material.LEATHER_HORSE_ARMOR, 5),

	PLAYER_PLUSHIE_SITTING(Material.LAPIS_LAZULI, 1),
	PLAYER_PLUSHIE_STANDING(Material.LAPIS_LAZULI, 10001),
	PLAYER_PLUSHIE_DABBING(Material.LAPIS_LAZULI, 20001),

	TABLE_WOODEN_1X1(Material.LEATHER_HORSE_ARMOR, 300),
	TABLE_WOODEN_1X2(Material.LEATHER_HORSE_ARMOR, 301),
	TABLE_WOODEN_2X2(Material.LEATHER_HORSE_ARMOR, 302),
	TABLE_WOODEN_2X3(Material.LEATHER_HORSE_ARMOR, 303),
	TABLE_WOODEN_3X3(Material.LEATHER_HORSE_ARMOR, 304),
	CHAIR_WOODEN_BASIC(Material.LEATHER_HORSE_ARMOR, 400),
	CHAIR_WOODEN_CUSHIONED(Material.LEATHER_HORSE_ARMOR, 401),
	CHAIR_CLOTH(Material.LEATHER_HORSE_ARMOR, 402),
	STOOL_WOODEN_BASIC(Material.LEATHER_HORSE_ARMOR, 500),
	STOOL_WOODEN_CUSHIONED(Material.LEATHER_HORSE_ARMOR, 501),
	STOOL_BAR_WOODEN(Material.LEATHER_HORSE_ARMOR, 502),
	STOOL_STUMP_OAK(Material.PAPER, 6300),
	STOOL_STUMP_OAK_ROOTS(Material.PAPER, 6301),
	STOOL_STUMP_SPRUCE(Material.PAPER, 6302),
	STOOL_STUMP_SPRUCE_ROOTS(Material.PAPER, 6303),
	STOOL_STUMP_BIRCH(Material.PAPER, 6304),
	STOOL_STUMP_BIRCH_ROOTS(Material.PAPER, 6305),
	STOOL_STUMP_JUNGLE(Material.PAPER, 6306),
	STOOL_STUMP_JUNGLE_ROOTS(Material.PAPER, 6307),
	STOOL_STUMP_ACACIA(Material.PAPER, 6308),
	STOOL_STUMP_ACACIA_ROOTS(Material.PAPER, 6309),
	STOOL_STUMP_DARK_OAK(Material.PAPER, 6310),
	STOOL_STUMP_DARK_OAK_ROOTS(Material.PAPER, 6311),
	STOOL_STUMP_MANGROVE(Material.PAPER, 6312),
	STOOL_STUMP_MANGROVE_ROOTS(Material.PAPER, 6313),
	BENCH_WOODEN(Material.LEATHER_HORSE_ARMOR, 450),
	ADIRONDACK(Material.LEATHER_HORSE_ARMOR, 451),
	BEACH_CHAIR(Material.LEATHER_HORSE_ARMOR, 452),
	COUCH_WOODEN_CUSHIONED_END_LEFT(Material.LEATHER_HORSE_ARMOR, 525),
	COUCH_WOODEN_CUSHIONED_END_RIGHT(Material.LEATHER_HORSE_ARMOR, 526),
	COUCH_WOODEN_CUSHIONED_MIDDLE(Material.LEATHER_HORSE_ARMOR, 527),
	COUCH_WOODEN_CUSHIONED_CORNER(Material.LEATHER_HORSE_ARMOR, 528),
	COUCH_WOODEN_CUSHIONED_OTTOMAN(Material.LEATHER_HORSE_ARMOR, 529),
	COUCH_CLOTH_END_LEFT(Material.LEATHER_HORSE_ARMOR, 530),
	COUCH_CLOTH_END_RIGHT(Material.LEATHER_HORSE_ARMOR, 531),
	COUCH_CLOTH_MIDDLE(Material.LEATHER_HORSE_ARMOR, 532),
	COUCH_CLOTH_CORNER(Material.LEATHER_HORSE_ARMOR, 533),
	COUCH_CLOTH_OTTOMAN(Material.LEATHER_HORSE_ARMOR, 534),

	FIREPLACE_DARK(Material.PAPER, 6215),
	FIREPLACE_BROWN(Material.PAPER, 6216),
	FIREPLACE_LIGHT(Material.PAPER, 6217),

	GRAVESTONE_SMALL(Material.PAPER, 7101),
	GRAVESTONE_CROSS(Material.PAPER, 7100),
	GRAVESTONE_PLAQUE(Material.PAPER, 7103),
	GRAVESTONE_STACK(Material.PAPER, 7102),
	GRAVESTONE_FLOWERBED(Material.PAPER, 7105),
	GRAVESTONE_TALL(Material.PAPER, 7104),

	FOOD_PIZZA_BOX_SINGLE(Material.PAPER, 6065),
	FOOD_PIZZA_BOX_SINGLE_OPENED(Material.PAPER, 6067),
	FOOD_PIZZA_BOX_STACK(Material.PAPER, 6066),
	FOOD_SOUP_MUSHROOM(Material.PAPER, 6062),
	FOOD_SOUP_BEETROOT(Material.PAPER, 6063),
	FOOD_SOUP_RABBIT(Material.PAPER, 6064),
	FOOD_BREAD_LOAF(Material.PAPER, 6068),
	FOOD_BREAD_LOAF_CUT(Material.PAPER, 6069),
	FOOD_BROWNIES_CHOCOLATE(Material.PAPER, 6030),
	FOOD_BROWNIES_VANILLA(Material.PAPER, 6031),
	FOOD_COOKIES_CHOCOLATE(Material.PAPER, 6032),
	FOOD_COOKIES_CHOCOLATE_CHIP(Material.PAPER, 6033),
	FOOD_COOKIES_SUGAR(Material.PAPER, 6034),
	FOOD_MILK_AND_COOKIES(Material.PAPER, 6038),
	FOOD_MUFFINS_CHOCOLATE(Material.PAPER, 6039),
	FOOD_MUFFINS_CHOCOLATE_CHIP(Material.PAPER, 6040),
	FOOD_MUFFINS_LEMON(Material.PAPER, 6041),
	FOOD_DINNER_HAM(Material.PAPER, 6035),
	FOOD_DINNER_ROAST(Material.PAPER, 6036),
	FOOD_DINNER_TURKEY(Material.PAPER, 6037),
	FOOD_PUNCHBOWL(Material.LEATHER_HORSE_ARMOR, 800),
	FOOD_SIDE_GREEN_BEAN_CASSEROLE(Material.PAPER, 6044),
	FOOD_SIDE_MAC_AND_CHEESE(Material.PAPER, 6045),
	FOOD_SIDE_SWEET_POTATOES(Material.PAPER, 6048),
	FOOD_SIDE_MASHED_POTATOES(Material.PAPER, 6046),
	FOOD_SIDE_SAUCE(Material.LEATHER_HORSE_ARMOR, 801),
	FOOD_SIDE_ROLLS(Material.PAPER, 6047),
	FOOD_CAKE_WHITE_CHOCOLATE(Material.PAPER, 6051),
	FOOD_CAKE_BUNDT(Material.PAPER, 6052),
	FOOD_CAKE_CHOCOLATE_DRIP(Material.PAPER, 6054),
	FOOD_CAKE_BATTER(Material.LEATHER_HORSE_ARMOR, 802),
	FOOD_PIE_ROUGH(Material.LEATHER_HORSE_ARMOR, 803),
	FOOD_PIE_SMOOTH(Material.LEATHER_HORSE_ARMOR, 804),
	FOOD_PIE_LATTICED(Material.LEATHER_HORSE_ARMOR, 805),

	KITCHENWARE_WINE_BOTTLE(Material.PAPER, 6115),
	KITCHENWARE_WINE_BOTTLE_GROUP(Material.PAPER, 6126),
	KITCHENWARE_WINE_BOTTLE_GROUP_RANDOM(Material.PAPER, 6125),
	KITCHENWARE_WINE_BOTTLE_GROUP_SIDE(Material.PAPER, 6127),
	KITCHENWARE_WINE_GLASS(Material.PAPER, 6116),
	KITCHENWARE_WINE_GLASS_FULL(Material.PAPER, 6117),
	KITCHENWARE_MUG_GLASS(Material.PAPER, 6118),
	KITCHENWARE_MUG_GLASS_FULL(Material.PAPER, 6119),
	KITCHENWARE_MUG_WOODEN(Material.PAPER, 6121),
	KITCHENWARE_MUG_WOODEN_FULL(Material.PAPER, 6120),
	KITCHENWARE_GLASSWARE_GROUP_1(Material.PAPER, 6122),
	KITCHENWARE_GLASSWARE_GROUP_2(Material.PAPER, 6123),
	KITCHENWARE_GLASSWARE_GROUP_3(Material.PAPER, 6124),
	KITCHENWARE_JAR(Material.PAPER, 6129),
	KITCHENWARE_JAR_HONEY(Material.PAPER, 6130),
	KITCHENWARE_JAR_COOKIES(Material.PAPER, 6131),
	KITCHENWARE_JAR_WIDE(Material.PAPER, 6128),
	KITCHENWARE_BOWL(Material.PAPER, 6086),
	KITCHENWARE_MIXING_BOWL(Material.PAPER, 6080),
	KITCHENWARE_PAN_CAKE(Material.PAPER, 6081),
	KITCHENWARE_PAN_CASSEROLE(Material.PAPER, 6082),
	KITCHENWARE_PAN_COOKIE(Material.PAPER, 6083),
	KITCHENWARE_PAN_MUFFIN(Material.PAPER, 6084),
	KITCHENWARE_PAN_PIE(Material.PAPER, 6085),

	APPLIANCE_FRIDGE(Material.LEATHER_HORSE_ARMOR, 900),
	APPLIANCE_FRIDGE_MAGNETS(Material.LEATHER_HORSE_ARMOR, 901),
	APPLIANCE_FRIDGE_TALL(Material.LEATHER_HORSE_ARMOR, 902),
	APPLIANCE_FRIDGE_TALL_MAGNETS(Material.LEATHER_HORSE_ARMOR, 903),
	APPLIANCE_FRIDGE_MINI(Material.LEATHER_HORSE_ARMOR, 904),
	APPLIANCE_FRIDGE_MINI_MAGNETS(Material.LEATHER_HORSE_ARMOR, 905),
	APPLIANCE_SLUSHIE_MACHINE(Material.LEATHER_HORSE_ARMOR, 906),
	APPLIANCE_GRILL_COMMERCIAL(Material.PAPER, 6132),
	APPLIANCE_OVEN_COMMERCIAL(Material.PAPER, 6133),
	APPLIANCE_DEEP_FRYER(Material.PAPER, 6134),

	TOILET_MODERN(Material.LEATHER_HORSE_ARMOR, 907),

	ART_PAINTING_CHERRY_FOREST(Material.PAPER, 7500),
	ART_PAINTING_END_ISLAND(Material.PAPER, 7501),
	ART_PAINTING_LOST_ENDERMAN(Material.PAPER, 7502),
	ART_PAINTING_PINE_TREE(Material.PAPER, 7503),
	ART_PAINTING_SUNSET(Material.PAPER, 7504),
	ART_PAINTING_SWAMP_HUT(Material.PAPER, 7505),

	ART_PAINTING_MOUNTAINS(Material.PAPER, 7550),
	ART_PAINTING_MUDDY_PIG(Material.PAPER, 7551),
	ART_PAINTING_PURPLE_SHEEP(Material.PAPER, 7552),
	ART_PAINTING_VILLAGE_HAPPY(Material.PAPER, 7553),
	ART_PAINTING_VILLAGE_CHAOS(Material.PAPER, 7554),

	ART_PAINTING_SKYBLOCK(Material.PAPER, 7600),
	ART_PAINTING_NETHER_FORTRESS_BRIDGE(Material.PAPER, 7601),
	ART_PAINTING_NETHER_CRIMSON_FOREST(Material.PAPER, 7602),
	ART_PAINTING_NETHER_WARPED_FOREST(Material.PAPER, 7603),
	ART_PAINTING_NETHER_BASALT_DELTAS(Material.PAPER, 7604),
	ART_PAINTING_NETHER_SOUL_SAND_VALLEY(Material.PAPER, 7605),

	ART_PAINTING_CASTLE(Material.PAPER, 7650),
	ART_PAINTING_LAKE(Material.PAPER, 7651),
	ART_PAINTING_RIVER(Material.PAPER, 7652),
	ART_PAINTING_ROAD(Material.PAPER, 7653),
	ART_PAINTING_ORIENTAL(Material.PAPER, 7654),
	ART_PAINTING_CHICKENS(Material.PAPER, 7655),

	POTION_FILLED_TINY_1(Material.LEATHER_HORSE_ARMOR, 27),
	POTION_FILLED_TINY_2(Material.LEATHER_HORSE_ARMOR, 28),
	POTION_FILLED_SMALL_1(Material.LEATHER_HORSE_ARMOR, 6),
	POTION_FILLED_SMALL_2(Material.LEATHER_HORSE_ARMOR, 7),
	POTION_FILLED_SMALL_3(Material.LEATHER_HORSE_ARMOR, 8),
	POTION_FILLED_MEDIUM_1(Material.LEATHER_HORSE_ARMOR, 9),
	POTION_FILLED_MEDIUM_2(Material.LEATHER_HORSE_ARMOR, 10),
	POTION_FILLED_WIDE(Material.LEATHER_HORSE_ARMOR, 11),
	POTION_FILLED_SKINNY(Material.LEATHER_HORSE_ARMOR, 12),
	POTION_FILLED_TALL(Material.LEATHER_HORSE_ARMOR, 13),
	POTION_FILLED_BIG_BOTTLE(Material.LEATHER_HORSE_ARMOR, 14),
	POTION_FILLED_BIG_TEAR(Material.LEATHER_HORSE_ARMOR, 15),
	POTION_FILLED_BIG_DONUT(Material.LEATHER_HORSE_ARMOR, 16),
	POTION_FILLED_BIG_SKULL(Material.LEATHER_HORSE_ARMOR, 17),
	POTION_FILLED_GROUP_SMALL(Material.LEATHER_HORSE_ARMOR, 18),
	POTION_FILLED_GROUP_MEDIUM(Material.LEATHER_HORSE_ARMOR, 21),
	POTION_FILLED_GROUP_TALL(Material.LEATHER_HORSE_ARMOR, 26),
	POTION_FILLED_GROUP_RANDOM_1(Material.LEATHER_HORSE_ARMOR, 29),
	POTION_FILLED_GROUP_RANDOM_2(Material.LEATHER_HORSE_ARMOR, 30),
	POTION_FILLED_GROUP_RANDOM_3(Material.LEATHER_HORSE_ARMOR, 31),
	POTION_FILLED_GROUP_RANDOM_4(Material.LEATHER_HORSE_ARMOR, 32),
	POTION_FILLED_GROUP_RANDOM_5(Material.LEATHER_HORSE_ARMOR, 33),
	POTION_FILLED_GROUP_RANDOM_6(Material.LEATHER_HORSE_ARMOR, 34),
	POTION_FILLED_GROUP_RANDOM_7(Material.LEATHER_HORSE_ARMOR, 35),
	POTION_FILLED_GROUP_RANDOM_8(Material.LEATHER_HORSE_ARMOR, 36),

	POTION_EMPTY_SMALL_1(Material.LEATHER_HORSE_ARMOR, 6100),
	POTION_EMPTY_SMALL_2(Material.LEATHER_HORSE_ARMOR, 6101),
	POTION_EMPTY_SMALL_3(Material.LEATHER_HORSE_ARMOR, 6102),
	POTION_EMPTY_MEDIUM_1(Material.LEATHER_HORSE_ARMOR, 6103),
	POTION_EMPTY_MEDIUM_2(Material.LEATHER_HORSE_ARMOR, 6104),
	POTION_EMPTY_WIDE(Material.LEATHER_HORSE_ARMOR, 6105),
	POTION_EMPTY_SKINNY(Material.LEATHER_HORSE_ARMOR, 6106),
	POTION_EMPTY_TALL(Material.LEATHER_HORSE_ARMOR, 6107),
	POTION_EMPTY_BIG_BOTTLE(Material.LEATHER_HORSE_ARMOR, 6110),
	POTION_EMPTY_BIG_TEAR(Material.LEATHER_HORSE_ARMOR, 6108),
	POTION_EMPTY_BIG_DONUT(Material.LEATHER_HORSE_ARMOR, 6109),
	POTION_EMPTY_BIG_SKULL(Material.LEATHER_HORSE_ARMOR, 6111),
	POTION_EMPTY_GROUP_SMALL(Material.LEATHER_HORSE_ARMOR, 6112),
	POTION_EMPTY_GROUP_MEDIUM(Material.LEATHER_HORSE_ARMOR, 6113),
	POTION_EMPTY_GROUP_TALL(Material.LEATHER_HORSE_ARMOR, 6114),

	// Custom Blocks
	TRIPWIRE_CROSS(Material.PAPER, 21000),
	BLOCKS_CRATE_APPLE(Material.PAPER, 20051),
	BLOCKS_ROCKS(Material.PAPER, 21103),
	BLOCKS_ROCKS_PEBBLES(Material.PAPER, 21106),

	// Misc
	INKWELL(Material.PAPER, 6204),
	WHEEL_SMALL(Material.PAPER, 6206),
	TELESCOPE(Material.PAPER, 6220),
	MICROSCOPE(Material.PAPER, 6221),
	MICROSCOPE_WITH_GEM(Material.PAPER, 6222),
	HELM(Material.PAPER, 6223),
	TRAFFIC_BLOCKADE(Material.PAPER, 6209),
	TRAFFIC_BLOCKADE_LIGHTS(Material.PAPER, 6210),
	TRAFFIC_CONE(Material.PAPER, 6211),
	POSTBOX(Material.PAPER, 6205),
	SANDWICH_SIGN(Material.PAPER, 6207),
	SANDWICH_SIGN_TALL(Material.PAPER, 6208),
	FIRE_HYDRANT(Material.PAPER, 6218),
	WAYSTONE(Material.PAPER, 5901),
	WAYSTONE_ACTIVATED(Material.PAPER, 5902),

	// Holiday stuff
	FIREPLACE_DARK_XMAS(Material.PAPER, 6700),
	FIREPLACE_BROWN_XMAS(Material.PAPER, 6701),
	FIREPLACE_LIGHT_XMAS(Material.PAPER, 6702),
	CHRISTMAS_TREE_COLORED(Material.PAPER, 6729),
	CHRISTMAS_TREE_WHITE(Material.PAPER, 6730),
	TOY_TRAIN(Material.PAPER, 6732),
	MISTLETOE(Material.PAPER, 6731),
	WREATH(Material.PAPER, 4200),
	STOCKINGS_SINGLE(Material.PAPER, 6707),
	STOCKINGS_DOUBLE(Material.PAPER, 6708),
	BUNTING_PHRASE_HAPPY_HOLIDAYS(Material.PAPER, 8150),
	BUNTING_PHRASE_HAPPY_NEW_YEAR(Material.PAPER, 8151),
	BUNTING_PHRASE_MERRY_CHRISTMAS(Material.PAPER, 8152),
	SNOWMAN_PLAIN(Material.PAPER, 6709),
	SNOWMAN_FANCY(Material.PAPER, 6710),
	SNOWBALLS_SMALL(Material.PAPER, 6711),
	SNOWBALLS_BIG(Material.PAPER, 6712),
	ICICLE_LIGHT_CENTER(Material.PAPER, 6704),
	ICICLE_LIGHT_LEFT(Material.PAPER, 6705),
	ICICLE_LIGHT_RIGHT(Material.PAPER, 6706),
	;

	private final Material material;
	private final int modelId;

	public static CustomMaterial of(ItemStack item) {
		if (isNullOrAir(item))
			return null;

		return of(new ItemBuilder(item));
	}

	public static CustomMaterial of(ItemBuilder item) {
		if (isNullOrAir(item))
			return null;

		for (CustomMaterial customMaterial : values())
			if (customMaterial.getMaterial() == item.material())
				if (customMaterial.getModelId() == item.modelId())
					return customMaterial;

		return null;
	}

	public CustomModel getCustomModel() {
		return CustomModel.of(this);
	}

	public boolean canBePlaced() {
		return switch (this) {
			case INFINITE_WATER_BUCKET -> true;
			default -> false;
		};
	}

	public ItemStack getItem() {
		return new ItemBuilder(this).build();
	}

	public ItemStack getNamedItem() {
		return new ItemBuilder(this).name(camelCase(this)).build();
	}

}
