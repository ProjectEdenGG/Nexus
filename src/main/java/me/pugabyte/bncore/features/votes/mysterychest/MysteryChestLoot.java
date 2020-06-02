package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum MysteryChestLoot {

	ONE("Dragon Fighter",
			new ItemStack(Material.GOLDEN_APPLE, 3),
			new ItemStack(Material.END_CRYSTAL, 4)),
	TWO("Flower Power",
			new ItemBuilder(Material.CYAN_SHULKER_BOX).name("Flower Power Box").shulkerBox(
				new ItemBuilder(Material.PLAYER_HEAD).name("&eCoupon for 1 HDB Head").lore("&5Mystery Chest Loot").build(),
				new ItemStack(Material.FLOWER_POT, 9),
				new ItemStack(Material.ITEM_FRAME, 4),
				new ItemStack(Material.PAINTING, 4),
				new ItemStack(Material.CACTUS),
				new ItemStack(Material.OXEYE_DAISY, 3),
				new ItemStack(Material.WHITE_TULIP, 2),
				new ItemStack(Material.RED_TULIP, 2),
				new ItemStack(Material.ORANGE_TULIP, 3),
				new ItemStack(Material.PINK_TULIP, 2),
				new ItemStack(Material.AZURE_BLUET, 2),
				new ItemStack(Material.ALLIUM, 2),
				new ItemStack(Material.BLUE_ORCHID, 2),
				new ItemStack(Material.POPPY, 2),
				new ItemStack(Material.DANDELION, 2),

				new ItemBuilder(Material.LIGHT_BLUE_BANNER, 2)
						.pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM)
						.pattern(DyeColor.CYAN, PatternType.TRIANGLE_BOTTOM)
						.pattern(DyeColor.BLACK, PatternType.CREEPER)
						.pattern(DyeColor.BLACK, PatternType.FLOWER)
						.pattern(DyeColor.GREEN, PatternType.BORDER)
						.pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE)
						.pattern(DyeColor.WHITE, PatternType.CROSS)
						.pattern(DyeColor.BROWN, PatternType.SKULL)
						.pattern(DyeColor.WHITE, PatternType.TRIANGLE_BOTTOM)
						.pattern(DyeColor.GREEN, PatternType.TRIANGLE_TOP)
						.pattern(DyeColor.GREEN, PatternType.TRIANGLE_TOP)
						.pattern(DyeColor.GREEN, PatternType.STRIPE_TOP)
						.build(),

				new ItemBuilder(Material.LIGHT_BLUE_BANNER, 2)
						.pattern(DyeColor.WHITE, PatternType.BRICKS)
						.pattern(DyeColor.BROWN, PatternType.STRIPE_BOTTOM)
						.pattern(DyeColor.LIGHT_BLUE, PatternType.BORDER)
						.pattern(DyeColor.BROWN, PatternType.TRIANGLE_BOTTOM)
						.pattern(DyeColor.PINK, PatternType.RHOMBUS_MIDDLE)
						.pattern(DyeColor.BLACK, PatternType.FLOWER)
						.pattern(DyeColor.BLACK, PatternType.CREEPER)
						.pattern(DyeColor.BROWN, PatternType.SKULL)
						.pattern(DyeColor.LIGHT_BLUE, PatternType.TRIANGLE_TOP)
						.pattern(DyeColor.LIGHT_BLUE, PatternType.STRIPE_TOP)
						.pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE)
						.pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE)
						.build(),

				new ItemBuilder(Material.YELLOW_BANNER, 2)
						.pattern(DyeColor.CYAN, PatternType.STRIPE_LEFT)
						.pattern(DyeColor.CYAN, PatternType.STRIPE_RIGHT)
						.pattern(DyeColor.WHITE, PatternType.FLOWER)
						.build(),

				new ItemBuilder(Material.CYAN_BANNER, 2)
						.pattern(DyeColor.YELLOW, PatternType.STRIPE_LEFT)
						.pattern(DyeColor.YELLOW, PatternType.STRIPE_RIGHT)
						.pattern(DyeColor.WHITE, PatternType.FLOWER)
						.build()
			).build()),
	THREE("Lumberjack",
			new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2),
			new ItemBuilder(Material.DIAMOND_AXE)
					.enchant(Enchantment.FIRE_ASPECT, 1)
					.enchant(Enchantment.LOOT_BONUS_MOBS, 2)
					.enchant(Enchantment.DURABILITY, 2)
					.build()
	),
	FOUR("Super Shulkers",
			new ItemStack(Material.YELLOW_SHULKER_BOX),
			new ItemStack(Material.CYAN_SHULKER_BOX),
			new ItemBuilder(Material.ENCHANTED_BOOK)
					.enchant(Enchantment.MENDING)
					.enchant(Enchantment.LOOT_BONUS_MOBS, 2)
					.build()
	),
	FIVE("Wing Effect",
			new ItemBuilder(Material.PAPER)
					.name("&eCoupon for 1 Wing Effect")
					.lore("&3Redeem this with an admin")
					.lore("&3to receive your wings")
					.lore("")
					.lore("&5Mystery Chest Loot")
					.build()
	),
	SIX("Sing Song",
			new ItemBuilder(Material.PAPER)
					.name("&eCoupon for 1 Powder Song")
					.lore("&3Redeem this with an admin")
					.lore("&3to receive your song")
					.lore("")
					.lore("&5Mystery Chest Loot")
					.build()
	),
	SEVEN("Looting Champion",
			new ItemBuilder(Material.DIAMOND_CHESTPLATE)
					.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
					.enchant(Enchantment.THORNS, 4)
					.enchant(Enchantment.DURABILITY, 2)
					.build(),
			new ItemBuilder(Material.DIAMOND_SWORD)
					.enchant(Enchantment.DAMAGE_ALL, 3)
					.enchant(Enchantment.LOOT_BONUS_MOBS, 4)
					.enchant(Enchantment.DURABILITY, 2)
					.build()
	),
	EIGHT("Guardian Fighter",
			new ItemStack(Material.TOTEM_OF_UNDYING),
			new ItemStack(Material.SPONGE, 3),
			new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3)
	),
	NINE("Enchanter",
			new ItemStack(Material.ENCHANTING_TABLE),
			new ItemStack(Material.BOOKSHELF, 6),
			new ItemStack(Material.LAPIS_BLOCK, 8),
			new ItemBuilder(Material.PAPER)
					.name("&eCoupon for 75 XP Levels")
					.lore("&3Claim this with an admin")
					.lore("&3to receive your levels")
					.lore("")
					.lore("&5Mystery Chest Loot")
					.build()
	),
	TEN("McMMO Leveler",
			new ItemBuilder(Material.PAPER)
					.name("&eCoupon for 10 McMMO Levels")
					.lore("&3Redeem this with an admin")
					.lore("&3to receive your levels")
					.lore("")
					.lore("&5Mystery Chest Loot")
					.build()
	),
	ELEVEN("Sharpshooter",
			new ItemBuilder(Material.BOW)
					.enchant(Enchantment.MENDING)
					.enchant(Enchantment.ARROW_INFINITE)
					.enchant(Enchantment.ARROW_DAMAGE, 5)
					.build()
	),
	TWELVE("Beacon",
			new ItemStack(Material.BEACON),
			new ItemStack(Material.IRON_BLOCK, 34)),
	THIRTEEN("Nokia 3310",
			new ItemBuilder(Material.DIAMOND_CHESTPLATE)
					.name("&5Nokia 3310 Chestplate")
					.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
					.enchant(Enchantment.PROTECTION_PROJECTILE, 5)
					.enchant(Enchantment.PROTECTION_FIRE, 5)
					.enchant(Enchantment.DURABILITY, 10)
					.build()
	);

	ItemStack[] loot;
	String name;

	MysteryChestLoot(String name, ItemStack... items) {
		this.loot = items;
		this.name = name;
	}

	public ItemStack[] getLoot() {
		return loot;
	}

	public ClickableItem[] getMenuLoot() {
		ClickableItem[] items = new ClickableItem[loot.length];
		for (int i = 0; i < loot.length; i++)
			items[i] = ClickableItem.empty(loot[i]);
		return items;
	}

	public String getName() {
		return name;
	}

}
