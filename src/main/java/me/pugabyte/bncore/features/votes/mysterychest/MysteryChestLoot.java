package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.ClickableItem;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum MysteryChestLoot {

	ONE("Dragon Fighter",
			new ItemStack(Material.GOLDEN_APPLE, 3),
			new ItemStack(Material.END_CRYSTAL, 4)),
	TWO("Flower Power",
			MysteryChest.getSecondLootBox()),
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
