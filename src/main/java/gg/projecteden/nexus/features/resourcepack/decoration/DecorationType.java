package gg.projecteden.nexus.features.resourcepack.decoration;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Hitbox;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Seat.DyedPart;
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
	TABLE_WOODEN_1x1(new Table("Wooden Table 1x1", 300, Table.TableSize._1x1)),
	TABLE_WOODEN_1x2(new Table("Wooden Table 1x2", 301, Table.TableSize._1x2)),
	TABLE_WOODEN_2x2(new Table("Wooden Table 2x2", 302, Table.TableSize._2x2)),
	TABLE_WOODEN_2x3(new Table("Wooden Table 2x3", 303, Table.TableSize._2x3)),
	TABLE_WOODEN_3x3(new Table("Wooden Table 3x3", 304, Table.TableSize._3x3)),
	// Chairs
	CHAIR_WOODEN_BASIC(new Chair("Wooden Chair", 400, DyedPart.WHOLE)),
	CHAIR_WOODEN_CUSHION(new Chair("Cushioned Wooden Chair", 401, DyedPart.CUSHION)),
	// Stools
	STOOL_WOODEN_BASIC(new Chair("Wooden Stool", 500, DyedPart.WHOLE)),
	STOOL_WOODEN_CUSHION(new Chair("Cushioned Wooden Stool", 501, DyedPart.CUSHION)),
	// Benches
	BENCH_WOODEN(new Bench("Wooden Bench", 450, DyedPart.WHOLE)),
	// Couches
	COUCH_WOODEN_END_LEFT(new Couch("Wooden Couch Left End", 525, CouchPart.END)),
	COUCH_WOODEN_END_RIGHT(new Couch("Wooden Couch Left Right", 526, CouchPart.END)),
	COUCH_WOODEN_MIDDLE(new Couch("Wooden Couch Middle", 527, CouchPart.STRAIGHT)),
	COUCH_WOODEN_CORNER(new Couch("Wooden Couch Corner", 528, CouchPart.CORNER)),
	COUCH_WOODEN_OTTOMAN(new Couch("Wooden Couch Ottoman", 529, CouchPart.STRAIGHT)),
	COUCH_MODERN_END_LEFT(new Couch("Modern Couch Left End", 530, CouchPart.END)),
	COUCH_MODERN_END_RIGHT(new Couch("Modern Couch Left Right", 531, CouchPart.END)),
	COUCH_MODERN_MIDDLE(new Couch("Modern Couch Middle", 532, CouchPart.STRAIGHT)),
	COUCH_MODERN_CORNER(new Couch("Modern Couch Corner", 533, CouchPart.CORNER)),
	COUCH_MODERN_OTTOMAN(new Couch("Modern Couch Ottoman", 534, CouchPart.STRAIGHT)),
	// Blocks
	DYE_STATION(new Block("Dye Station", 1, Material.CRAFTING_TABLE)),
	// Fireplaces: TODO
	// Gravestones
	GRAVESTONE_SMALL(new Decoration("Small Gravestone", 2, Material.STONE)),
	GRAVESTONE_CROSS(new Decoration("Gravestone Cross", 1, Material.STONE, Hitbox.single(Material.IRON_BARS))),
	GRAVESTONE_PLAQUE(new Decoration("Gravestone Plaque", 4, Material.STONE)),
	GRAVESTONE_STACK(new Decoration("Rock Stack Gravestone", 3, Material.STONE)),
	GRAVESTONE_FLOWERBED(new Decoration("Flowerbed Gravestone", 6, Material.STONE)),
	GRAVESTONE_TALL(new Decoration("Tall Gravestone", 5, Material.STONE, List.of(Hitbox.origin(Material.IRON_BARS), Hitbox.offset(Material.IRON_BARS, BlockFace.UP)))),
	// Food
	PIZZA_BOX_SINGLE(new Decoration("Pizza Box", 1, Material.BREAD)),
	PIZZA_BOX_SINGLE_OPENED(new Decoration("Opened Pizza Box", 3, Material.BREAD)),
	PIZZA_BOX_STACK(new Decoration("Pizza Box Stack", 2, Material.BREAD)),
	SOUP_MUSHROOM(new Decoration("Mushroom Soup", 4, Material.BOWL)),
	SOUP_BEETROOT(new Decoration("Beetroot Soup", 5, Material.BOWL)),
	SOUP_RABBIT(new Decoration("Rabbit Soup", 6, Material.BOWL)),
	BREAD_LOAF(new Decoration("Loaf of Bread", 4, Material.BREAD)),
	BREAD_LOAF_CUT(new Decoration("Cut Loaf of Bread", 5, Material.BREAD)),
	BROWNIES_CHOCOLATE(new Decoration("Chocolate Brownies", 101)),
	BROWNIES_VANILLA(new Decoration("Vanilla Brownies", 102)),
	COOKIES_CHOCOLATE(new Decoration("Chocolate Cookies", 103)),
	COOKIES_CHOCOLATE_CHIP(new Decoration("Chocolate Chip Cookies", 104)),
	COOKIES_SUGAR(new Decoration("Sugar Cookies", 105)),
	MILK_AND_COOKIES(new Decoration("Milk and Cookies", 109)),
	MUFFINS_CHOCOLATE(new Decoration("Chocolate Muffins", 110)),
	MUFFINS_CHOCOLATE_CHIP(new Decoration("Chocolate Chip Muffins", 111)),
	MUFFINS_LEMON(new Decoration("Lemon Muffins", 112)),
	DINNER_HAM(new Decoration("Ham Dinner", 106)),
	DINNER_ROAST(new Decoration("Roast Dinner", 107)),
	DINNER_TURKEY(new Decoration("Turkey Dinner", 108)),
	//	PUNCHBOWL_EGGNOG(new Dyeable("name", 113)), // TODO: Make dyeable
//	SIDE_CRANBERRIES(new Dyeable("name", 114)), // TODO: Make dyeable
	SIDE_GREEN_BEAN_CASSEROLE(new Decoration("Green Bean Casserole Side", 115)),
	SIDE_MAC_AND_CHEESE(new Decoration("Mac N' Cheese Side", 116)),
	SIDE_SWEET_POTATOES(new Decoration("Sweet Potatoes Side", 119)),
	SIDE_MASHED_POTATOES(new Decoration("Mashed Potatoes Side", 117)),
	SIDE_ROLLS(new Decoration("Rolls", 118)),
	//	CAKE_BATTER(new Dyeable("name", -1)), // TODO: Make dyeable
	CAKE_WHITE_CHOCOLATE(new Decoration("White Chocolate Cake", 122)),
	CAKE_BUNDT(new Decoration("Bundt Cake", 123)),
	CAKE_CHOCOLATE_DRIP(new Decoration("Chocolate Drip Cake", 125)),
//	PIE(new Dyeable("name", -1)), // TODO: Make dyeable
//	PIE_LATTICED(new Dyeable("name", -1)), // TODO: Make dyeable
	// Kitchenware
	WINE_BOTTLE(new Decoration("Wine Bottle", 400, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_BOTTLE_GROUP(new Decoration("Wine Bottles", 454, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_BOTTLE_GROUP_RANDOM(new Decoration("Random Wine Bottles", 453, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_BOTTLE_GROUP_SIDE(new Decoration("Wine Bottles on Side", 455, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_GLASS(new Decoration("Wine Glass", 401, Material.BLUE_STAINED_GLASS_PANE)),
	WINE_GLASS_FULL(new Decoration("Full Wine Glass", 402, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_GLASS(new Decoration("Glass Mug", 403, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_GLASS_FULL(new Decoration("Full Glass Mug", 404, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_WOODEN(new Decoration("Wooden Mug", 406, Material.BLUE_STAINED_GLASS_PANE)),
	MUG_WOODEN_FULL(new Decoration("Full Wooden Mug", 405, Material.BLUE_STAINED_GLASS_PANE)),
	GLASSWARE_GROUP_1(new Decoration("Random Glassware 1", 450, Material.BLUE_STAINED_GLASS_PANE)),
	GLASSWARE_GROUP_2(new Decoration("Random Glassware 2", 451, Material.BLUE_STAINED_GLASS_PANE)),
	GLASSWARE_GROUP_3(new Decoration("Random Glassware 3", 452, Material.BLUE_STAINED_GLASS_PANE)),
	JAR(new Decoration("Jar", 501, Material.BLUE_STAINED_GLASS_PANE)),
	JAR_HONEY(new Decoration("Honey Jar", 502, Material.BLUE_STAINED_GLASS_PANE)),
	JAR_COOKIES(new Decoration("Cookie Jar", 503, Material.BLUE_STAINED_GLASS_PANE)),
	JAR_WIDE(new Decoration("Wide Jar", 500, Material.BLUE_STAINED_GLASS_PANE)),
	BOWL(new Decoration("Wooden Bowl", 3, Material.BOWL)),
	MIXING_BOWL(new Decoration("Mixing Bowl", 133)),
	PAN_CAKE(new Decoration("Cake Pan", 134)),
	PAN_CASSEROLE(new Decoration("Casserole Pan", 135)),
	PAN_COOKIE(new Decoration("Cookie Pan", 136)),
	PAN_MUFFIN(new Decoration("Muffin Pan", 137)),
	PAN_PIE(new Decoration("Pie Pan", 138)),
	// Potions
	POTION_TINY_1(new Dyeable("Tiny Potions 1", 27)),
	POTION_TINY_2(new Dyeable("Tiny Potions 2", 28)),
	POTION_SMALL_1(new Dyeable("Small Potion 1", 6)),
	POTION_SMALL_2(new Dyeable("Small Potion 2", 7)),
	POTION_SMALL_3(new Dyeable("Small Potion 3", 8)),
	POTION_MEDIUM_1(new Dyeable("Medium Potion 1", 9)),
	POTION_MEDIUM_2(new Dyeable("Medium Potion 2", 10)),
	POTION_WIDE(new Dyeable("Wide Potion", 11)),
	POTION_SKINNY(new Dyeable("Skinny Potion", 12)),
	POTION_TALL(new Dyeable("Tall Potion", 13)),
	POTION_BIG_BOTTLE(new Dyeable("Big Potion Bottle", 14)),
	POTION_BIG_TEAR(new Dyeable("Big Potion Tear", 15)),
	POTION_BIG_DONUT(new Dyeable("Big Potion Donut", 16)),
	POTION_BIG_SKULL(new Dyeable("Big Potion Skull", 17)),
	POTION_GROUP_SMALL(new Dyeable("Small Potions", 18)),
	POTION_GROUP_MEDIUM(new Dyeable("Medium Potions", 21)),
	POTION_GROUP_TALL(new Dyeable("Tall Potions", 26)),
	POTION_GROUP_RANDOM_1(new Dyeable("Random Potions 1", 29)),
	POTION_GROUP_RANDOM_2(new Dyeable("Random Potions 2", 30)),
	POTION_GROUP_RANDOM_3(new Dyeable("Random Potions 3", 31)),
	POTION_GROUP_RANDOM_4(new Dyeable("Random Potions 4", 32)),
	POTION_GROUP_RANDOM_5(new Dyeable("Random Potions 5", 33)),
	POTION_GROUP_RANDOM_6(new Dyeable("Random Potions 6", 34)),
	POTION_GROUP_RANDOM_7(new Dyeable("Random Potions 7", 35)),
	POTION_GROUP_RANDOM_8(new Dyeable("Random Potions 8", 36)),
	// Misc
	INKWELL(new Decoration("Inkwell", 1, Material.FEATHER)),
	WHEEL_SMALL(new Decoration("Small Wheel", 2, Material.MUSIC_DISC_11)),
	;

	@Getter
	final Decoration decoration;

	public ItemStack getItem() {
		return decoration.getItem().clone();
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
			hitboxTypes.addAll(decorationType.getDecoration().getHitboxes()
				.stream()
				.map(Hitbox::getMaterial)
				.filter(material -> !MaterialTag.ALL_AIR.isTagged(material))
				.toList()));

		return hitboxTypes;
	}

}
