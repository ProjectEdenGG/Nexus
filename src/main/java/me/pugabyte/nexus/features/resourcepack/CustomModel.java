package me.pugabyte.nexus.features.resourcepack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum CustomModel {
	DIAMOND_TOTEM(Material.TOTEM_OF_UNDYING, 1, "&bDiamond Totem Of Undying", "&7Activates from anywhere", "&7in your inventory"),
	BUNDLE(Material.CHEST, 1, "Bundle"),
	HOOK(Material.TRIPWIRE_HOOK, 1, "Hook"),

	PIRATE_HAT_WHITE_CAVALIER(Material.STONE_BUTTON, 1, "White Cavalier"),
	PIRATE_HAT_WHITE_BICORN(Material.STONE_BUTTON, 2, "White Bicorn"),
	PIRATE_HAT_WHITE_BICORN_SIDE(Material.STONE_BUTTON, 3, "White Bicorn Side"),
	PIRATE_HAT_WHITE_TRICORN(Material.STONE_BUTTON, 4, "White Tricorn"),
	PIRATE_HAT_LIGHT_GRAY_CAVALIER(Material.STONE_BUTTON, 5, "Light Gray Cavalier"),
	PIRATE_HAT_LIGHT_GRAY_BICORN(Material.STONE_BUTTON, 6, "Light Gray Bicorn"),
	PIRATE_HAT_LIGHT_GRAY_BICORN_SIDE(Material.STONE_BUTTON, 7, "Light Gray Bicorn Side"),
	PIRATE_HAT_LIGHT_GRAY_TRICORN(Material.STONE_BUTTON, 8, "Light Gray Tricorn"),
	PIRATE_HAT_GRAY_CAVALIER(Material.STONE_BUTTON, 9, "Gray Cavalier"),
	PIRATE_HAT_GRAY_BICORN(Material.STONE_BUTTON, 10, "Gray Bicorn"),
	PIRATE_HAT_GRAY_BICORN_SIDE(Material.STONE_BUTTON, 11, "Gray Bicorn Side"),
	PIRATE_HAT_GRAY_TRICORN(Material.STONE_BUTTON, 12, "Gray Tricorn"),
	PIRATE_HAT_BLACK_CAVALIER(Material.STONE_BUTTON, 13, "Black Cavalier"),
	PIRATE_HAT_BLACK_BICORN(Material.STONE_BUTTON, 14, "Black Bicorn"),
	PIRATE_HAT_BLACK_BICORN_SIDE(Material.STONE_BUTTON, 15, "Black Bicorn Side"),
	PIRATE_HAT_BLACK_TRICORN(Material.STONE_BUTTON, 16, "Black Tricorn"),
	PIRATE_HAT_BROWN_CAVALIER(Material.STONE_BUTTON, 17, "Brown Cavalier"),
	PIRATE_HAT_BROWN_BICORN(Material.STONE_BUTTON, 18, "Brown Bicorn"),
	PIRATE_HAT_BROWN_BICORN_SIDE(Material.STONE_BUTTON, 19, "Brown Bicorn Side"),
	PIRATE_HAT_BROWN_TRICORN(Material.STONE_BUTTON, 20, "Brown Tricorn"),
	PIRATE_HAT_LEATHER_CAVALIER(Material.STONE_BUTTON, 21, "Leather Cavalier"),
	PIRATE_HAT_LEATHER_BICORN(Material.STONE_BUTTON, 22, "Leather Bicorn"),
	PIRATE_HAT_LEATHER_BICORN_SIDE(Material.STONE_BUTTON, 23, "Leather Bicorn Side"),
	PIRATE_HAT_LEATHER_TRICORN(Material.STONE_BUTTON, 24, "Leather Tricorn"),
	PIRATE_HAT_RED_CAVALIER(Material.STONE_BUTTON, 25, "Red Cavalier"),
	PIRATE_HAT_RED_BICORN(Material.STONE_BUTTON, 26, "Red Bicorn"),
	PIRATE_HAT_RED_BICORN_SIDE(Material.STONE_BUTTON, 27, "Red Bicorn Side"),
	PIRATE_HAT_RED_TRICORN(Material.STONE_BUTTON, 28, "Red Tricorn"),
	PIRATE_HAT_ORANGE_CAVALIER(Material.STONE_BUTTON, 29, "Orange Cavalier"),
	PIRATE_HAT_ORANGE_BICORN(Material.STONE_BUTTON, 30, "Orange Bicorn"),
	PIRATE_HAT_ORANGE_BICORN_SIDE(Material.STONE_BUTTON, 31, "Orange Bicorn Side"),
	PIRATE_HAT_ORANGE_TRICORN(Material.STONE_BUTTON, 32, "Orange Tricorn"),
	PIRATE_HAT_YELLOW_CAVALIER(Material.STONE_BUTTON, 33, "Yellow Cavalier"),
	PIRATE_HAT_YELLOW_BICORN(Material.STONE_BUTTON, 34, "Yellow Bicorn"),
	PIRATE_HAT_YELLOW_BICORN_SIDE(Material.STONE_BUTTON, 35, "Yellow Bicorn Side"),
	PIRATE_HAT_YELLOW_TRICORN(Material.STONE_BUTTON, 36, "Yellow Tricorn"),
	PIRATE_HAT_GREEN_CAVALIER(Material.STONE_BUTTON, 37, "Green Cavalier"),
	PIRATE_HAT_GREEN_BICORN(Material.STONE_BUTTON, 38, "Green Bicorn"),
	PIRATE_HAT_GREEN_BICORN_SIDE(Material.STONE_BUTTON, 39, "Green Bicorn Side"),
	PIRATE_HAT_GREEN_TRICORN(Material.STONE_BUTTON, 40, "Green Tricorn"),
	PIRATE_HAT_LIME_CAVALIER(Material.STONE_BUTTON, 41, "Lime Cavalier"),
	PIRATE_HAT_LIME_BICORN(Material.STONE_BUTTON, 42, "Lime Bicorn"),
	PIRATE_HAT_LIME_BICORN_SIDE(Material.STONE_BUTTON, 43, "Lime Bicorn Side"),
	PIRATE_HAT_LIME_TRICORN(Material.STONE_BUTTON, 44, "Lime Tricorn"),
	PIRATE_HAT_MINT_CAVALIER(Material.STONE_BUTTON, 45, "Mint Cavalier"),
	PIRATE_HAT_MINT_BICORN(Material.STONE_BUTTON, 46, "Mint Bicorn"),
	PIRATE_HAT_MINT_BICORN_SIDE(Material.STONE_BUTTON, 47, "Mint Bicorn Side"),
	PIRATE_HAT_MINT_TRICORN(Material.STONE_BUTTON, 48, "Mint Tricorn"),
	PIRATE_HAT_CYAN_CAVALIER(Material.STONE_BUTTON, 49, "Cyan Cavalier"),
	PIRATE_HAT_CYAN_BICORN(Material.STONE_BUTTON, 50, "Cyan Bicorn"),
	PIRATE_HAT_CYAN_BICORN_SIDE(Material.STONE_BUTTON, 51, "Cyan Bicorn Side"),
	PIRATE_HAT_CYAN_TRICORN(Material.STONE_BUTTON, 52, "Cyan Tricorn"),
	PIRATE_HAT_LIGHT_BLUE_CAVALIER(Material.STONE_BUTTON, 53, "Light Blue Cavalier"),
	PIRATE_HAT_LIGHT_BLUE_BICORN(Material.STONE_BUTTON, 54, "Light Blue Bicorn"),
	PIRATE_HAT_LIGHT_BLUE_BICORN_SIDE(Material.STONE_BUTTON, 55, "Light Blue Bicorn Side"),
	PIRATE_HAT_LIGHT_BLUE_TRICORN(Material.STONE_BUTTON, 56, "Light Blue Tricorn"),
	PIRATE_HAT_BLUE_CAVALIER(Material.STONE_BUTTON, 57, "Blue Cavalier"),
	PIRATE_HAT_BLUE_BICORN(Material.STONE_BUTTON, 58, "Blue Bicorn"),
	PIRATE_HAT_BLUE_BICORN_SIDE(Material.STONE_BUTTON, 59, "Blue Bicorn Side"),
	PIRATE_HAT_BLUE_TRICORN(Material.STONE_BUTTON, 60, "Blue Tricorn"),
	PIRATE_HAT_PURPLE_CAVALIER(Material.STONE_BUTTON, 61, "Purple Cavalier"),
	PIRATE_HAT_PURPLE_BICORN(Material.STONE_BUTTON, 62, "Purple Bicorn"),
	PIRATE_HAT_PURPLE_BICORN_SIDE(Material.STONE_BUTTON, 63, "Purple Bicorn Side"),
	PIRATE_HAT_PURPLE_TRICORN(Material.STONE_BUTTON, 64, "Purple Tricorn"),
	PIRATE_HAT_MAGENTA_CAVALIER(Material.STONE_BUTTON, 65, "Magenta Cavalier"),
	PIRATE_HAT_MAGENTA_BICORN(Material.STONE_BUTTON, 66, "Magenta Bicorn"),
	PIRATE_HAT_MAGENTA_BICORN_SIDE(Material.STONE_BUTTON, 67, "Magenta Bicorn Side"),
	PIRATE_HAT_MAGENTA_TRICORN(Material.STONE_BUTTON, 68, "Magenta Tricorn"),
	PIRATE_HAT_PINK_CAVALIER(Material.STONE_BUTTON, 69, "Pink Cavalier"),
	PIRATE_HAT_PINK_BICORN(Material.STONE_BUTTON, 70, "Pink Bicorn"),
	PIRATE_HAT_PINK_BICORN_SIDE(Material.STONE_BUTTON, 71, "Pink Bicorn Side"),
	PIRATE_HAT_PINK_TRICORN(Material.STONE_BUTTON, 72, "Pink Tricorn"),

	GEM_SAPPHIRE(Material.EMERALD, 1, "Sapphire"),
	GEM_ALEXANDRITE(Material.EMERALD, 2, "Alexandrite"),
	GEM_AMBER(Material.EMERALD, 3, "Amber"),
	GEM_AMETHYST(Material.EMERALD, 4, "Amethyst"),
	GEM_AQUAMARINE(Material.EMERALD, 5, "Aquamarine"),
	GEM_BLACK_OPAL(Material.EMERALD, 6, "Black Opal"),
	GEM_GARNET(Material.EMERALD, 7, "Garnet"),
	GEM_JADE(Material.EMERALD, 8, "Jade"),
	GEM_JASPER(Material.EMERALD, 9, "Jasper"),
	GEM_MALACHITE(Material.EMERALD, 10, "Malachite"),
	GEM_ONYX(Material.EMERALD, 11, "Onyx"),
	GEM_OPAL(Material.EMERALD, 12, "Opal"),
	GEM_PERIDOT(Material.EMERALD, 13, "Peridot"),
	GEM_ROSE_QUARTZ(Material.EMERALD, 14, "Rose Quartz"),
	GEM_RUBY(Material.EMERALD, 15, "Ruby"),
	GEM_SPINEL(Material.EMERALD, 16, "Spinel"),
	GEM_SUGILITE(Material.EMERALD, 17, "Sugilite"),
	GEM_TRANZANITE(Material.EMERALD, 18, "Tranzanite"),
	GEM_TOPAZ(Material.EMERALD, 19, "Topaz"),
	GEM_TOURMALINE(Material.EMERALD, 20, "Tourmaline"),

	BACKPACK(Material.SHULKER_BOX, 1, "Backpack"),
	BACKPACK_WHITE(Material.WHITE_SHULKER_BOX, 1, "Backpack"),
	BACKPACK_ORANGE(Material.ORANGE_SHULKER_BOX, 1, "&6Backpack"),
	BACKPACK_MAGENTA(Material.MAGENTA_SHULKER_BOX, 1, "&dBackpack"),
	BACKPACK_LIGHT_BLUE(Material.LIGHT_BLUE_SHULKER_BOX, 1, "&aBackpack"),
	BACKPACK_YELLOW(Material.YELLOW_SHULKER_BOX, 1, "&eBackpack"),
	BACKPACK_LIME(Material.LIME_SHULKER_BOX, 1, "&aBackpack"),
	BACKPACK_PINK(Material.PINK_SHULKER_BOX, 1, "&dBackpack"),
	BACKPACK_GRAY(Material.GRAY_SHULKER_BOX, 1, "&8Backpack"),
	BACKPACK_LIGHT_GRAY(Material.LIGHT_GRAY_SHULKER_BOX, 1, "&7Backpack"),
	BACKPACK_CYAN(Material.CYAN_SHULKER_BOX, 1, "&3Backpack"),
	BACKPACK_PURPLE(Material.PURPLE_SHULKER_BOX, 1, "&5Backpack"),
	BACKPACK_BLUE(Material.BLUE_SHULKER_BOX, 1, "&1Backpack"),
	BACKPACK_BROWN(Material.BROWN_SHULKER_BOX, 1, "&9Backpack"),
	BACKPACK_GREEN(Material.GREEN_SHULKER_BOX, 1, "&2Backpack"),
	BACKPACK_RED(Material.RED_SHULKER_BOX, 1, "&cBackpack"),
	BACKPACK_BLACK(Material.BLACK_SHULKER_BOX, 1, "&0Backpack"),

	BAKEWARE(Material.FLOWER_POT, 1, "Bakeware"),
	CUTTING_BOARD(Material.IRON_SWORD, 1, "Cutting Board"),
	GRINDER(Material.BOWL, 1, "Grinder"),
	JUICER(Material.FLOWER_POT, 2, "Juicer"),
	MIXING_BOWL(Material.BOWL, 2, "Mixing Bowl"),
	POT(Material.BUCKET, 1, "Pot"),
	ROLLER(Material.STICK, 1, "Roller"),
	SAUCEPAN(Material.BUCKET, 2, "Saucepan"),
	SKILLET(Material.BUCKET, 3, "Skillet"),

	GLOWSHROOM_GREEN(Material.BROWN_MUSHROOM, 1, "Green Glowshroom"),
	GLOWSHROOM_BLUE(Material.BROWN_MUSHROOM, 2, "Blue Glowshroom"),
	MUSHROOM_TEAL(Material.BROWN_MUSHROOM, 3, "Teal Mushroom"),
	STRANGE_PLANT_ORANGE(Material.BROWN_MUSHROOM, 4, "Flame Leaf"),
	STRANGE_PLANT_PURPLE(Material.BROWN_MUSHROOM, 5, "Purple Basil"),
	STRANGE_PLANT_GREEN(Material.BROWN_MUSHROOM, 6, "Green Hosta"),
	;

	private final Material material;
	private final int data;
	private final String name;
	private final List<String> lore;

	CustomModel(Material material, int data, String name, String... lore) {
		this.material = material;
		this.data = data;
		this.name = name;
		this.lore = Arrays.asList(lore);
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).customModelData(data).name(name).lore(lore).build();
	}

	public String getName() {
		return "&f" + name;
	}
}