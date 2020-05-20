package me.pugabyte.bncore.features.votes.mysterychest;

import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum MysteryChestLoot {

	ONE(new ItemStack(Material.GOLDEN_APPLE, 3),
			new ItemStack(Material.END_CRYSTAL, 4)),
	TWO(),
	THREE(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2),
			new ItemBuilder(Material.DIAMOND_AXE)
					.enchant(Enchantment.FIRE_ASPECT, 1)
					.enchant(Enchantment.LOOT_BONUS_MOBS, 2)
					.enchant(Enchantment.DURABILITY, 2)
					.build()
	),
	FOUR(new ItemStack(Material.YELLOW_SHULKER_BOX),
			new ItemStack(Material.CYAN_SHULKER_BOX),
			new ItemBuilder(Material.ENCHANTED_BOOK)
					.enchant(Enchantment.MENDING)
					.enchant(Enchantment.LOOT_BONUS_MOBS, 2)
					.build()
	),
	FIVE(new ItemBuilder(Material.PAPER)
			.name("&eCoupon for 1 Wing Effect")
			.lore("&3Redeem this with an admin")
			.lore("&3to receive your wings")
			.build()
	),
	SIX(new ItemBuilder(Material.PAPER)
			.name("&eCoupon for 1 Powder Song")
			.lore("&3Redeem this with an admin")
			.lore("&3to receive your song")
			.build()
	),
	SEVEN(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
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
	EIGHT(new ItemStack(Material.TOTEM_OF_UNDYING),
			new ItemStack(Material.SPONGE, 3),
			new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3)
	),
	NINE(new ItemStack(Material.ENCHANTING_TABLE),
			new ItemStack(Material.BOOKSHELF, 6),
			new ItemStack(Material.LAPIS_BLOCK, 8),
			new ItemBuilder(Material.PAPER)
					.name("&eCoupon for 75 XP Levels")
					.lore("&3Claim this with an admin")
					.lore("&3to receive your levels")
					.build()
	),
	TEN(new ItemBuilder(Material.PAPER)
			.name("&eCoupon for 10 McMMO Levels")
			.lore("&3Redeem this with an admin")
			.lore("&3to receive your levels")
			.build()
	),
	ELEVEN(new ItemBuilder(Material.BOW)
			.enchant(Enchantment.MENDING)
			.enchant(Enchantment.ARROW_INFINITE)
			.enchant(Enchantment.ARROW_DAMAGE, 5)
			.build()
	),
	TWELVE(new ItemStack(Material.BEACON),
			new ItemStack(Material.IRON_BLOCK, 34)),
	THIRTEEN(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
			.name("&5Nokia 3310 Chestplate")
			.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
			.enchant(Enchantment.PROTECTION_PROJECTILE, 5)
			.enchant(Enchantment.PROTECTION_FIRE, 5)
			.enchant(Enchantment.DURABILITY, 10)
			.build()
	);

	ItemStack[] loot;

	MysteryChestLoot(ItemStack... items) {
		this.loot = items;
	}

	public ItemStack[] getLoot() {
		return loot;
	}

}
