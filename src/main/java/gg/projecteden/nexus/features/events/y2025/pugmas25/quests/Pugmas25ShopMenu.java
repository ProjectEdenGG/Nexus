package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.NPCShopMenuBuilder;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.quests.QuestShopMenu;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@AllArgsConstructor
public enum Pugmas25ShopMenu implements QuestShopMenu {

	BLACKSMITH(Pugmas25NPC.BLACKSMITH, NPCShopMenu.builder().title("Blacksmith")
		.products(List.of(
			new Product(Material.STICK).price(Currency.COIN_POUCH, Price.of(5)),
			new Product(Material.STONE_PICKAXE).price(Currency.COIN_POUCH, Price.of(25)),
			new Product(Material.NETHERITE_SCRAP).price(Currency.COIN_POUCH, Price.of(10000)),
			new Product(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE).price(Currency.COIN_POUCH, Price.of(25000))
		))
	),

	ADVENTURER(Pugmas25NPC.ADVENTURER, NPCShopMenu.builder().title("Adventurer")
		.products(List.of(
			new Product(Material.IRON_SWORD).price(Currency.COIN_POUCH, Price.of(75)),
			new Product(Material.SHIELD).price(Currency.COIN_POUCH, Price.of(100)),
			new Product(Material.BOW).price(Currency.COIN_POUCH, Price.of(50)),
			new Product(new ItemStack(Material.ARROW, 16)).price(Currency.COIN_POUCH, Price.of(16))
		))
	),

	ARTIST(Pugmas25NPC.ARTIST, NPCShopMenu.builder().title("Artist")
		.products(MaterialTag.DYES.getValues().stream()
			.map(dye -> new Product(dye).price(Currency.COIN_POUCH, Price.of(8))).toList())
	),

	TINKERER(Pugmas25NPC.TINKERER, NPCShopMenu.builder().title("Tinkerer")
		.products(List.of(
			new Product(Pugmas25QuestItem.MAGIC_MIRROR)
				.price(Currency.COIN_POUCH, Price.of(50000))
				.predicate(Pugmas25QuestItem.MAGIC_MIRROR::isNotInInventoryOf),

			new Product(Pugmas25QuestItem.GNOMIFIER)
				.price(Currency.COIN_POUCH, Price.of(10000))
				.predicate(Pugmas25QuestItem.GNOMIFIER::isNotInInventoryOf),

			new Product(Pugmas25QuestItem.SHOCK_ABSORBENT_BOOTS)
				.price(Currency.COIN_POUCH, Price.of(25000)),

			new Product(Pugmas25QuestItem.FISH_FINDER)
				.price(Currency.ITEMS, Price.of(List.of(
					Pugmas25QuestItem.FISHING_POCKET_GUIDE.get(),
					Pugmas25QuestItem.SEXTANT.get(),
					Pugmas25QuestItem.WEATHER_RADIO.get()
				)))
				.predicate(Pugmas25QuestItem.FISH_FINDER::isNotInInventoryOf),

			new Product(Pugmas25QuestItem.GPS)
				.price(Currency.ITEMS, Price.of(List.of(
					Pugmas25QuestItem.ADVENTURE_POCKET_GUIDE.get(),
					Pugmas25QuestItem.COMPASS.get(),
					Pugmas25QuestItem.GOLD_WATCH.get()
				)))
				.predicate(Pugmas25QuestItem.GPS::isNotInInventoryOf),

			new Product(Pugmas25QuestItem.PDA)
				.price(Currency.ITEMS, Price.of(List.of(
					Pugmas25QuestItem.FISH_FINDER.get(),
					Pugmas25QuestItem.GPS.get(),
					Pugmas25QuestItem.MAGIC_MIRROR.get()
				)))
				.predicate(Pugmas25QuestItem.PDA::isNotInInventoryOf),

			new Product(CommonQuestItem.BASIC_BACKPACK)
				.price(Currency.COIN_POUCH, Price.of(5000)),

			new Product(new ItemBuilder(Material.SHULKER_SHELL, 4).build())
				.displayItemStack(new ItemBuilder(Material.SHULKER_SHELL, 4).lore("&7Used for upgrading your backpack"))
				.price(Currency.COIN_POUCH, Price.of(2000)),

			new Product(Pugmas25QuestItem.FISHING_ROD_WOOD)
				.price(Currency.COIN_POUCH, Price.of(100)) // cheap
				.predicate(Pugmas25QuestItem.FISHING_ROD_WOOD::isNotInInventoryOf),

			new Product(Pugmas25QuestItem.FISHING_ROD_REINFORCED)
				.price(Currency.COIN_POUCH, Price.of(1000)) // expensive
				.predicate(player -> {
					Pugmas25UserService userService = new Pugmas25UserService();
					return (userService.get(player).getCompletedAnglerQuests() >= 26 && Pugmas25QuestItem.FISHING_ROD_REINFORCED.isNotInInventoryOf(player));
				})
		))
	),
	;

	private final InteractableNPC NPC;
	private final NPCShopMenuBuilder shopBuilder;
}
