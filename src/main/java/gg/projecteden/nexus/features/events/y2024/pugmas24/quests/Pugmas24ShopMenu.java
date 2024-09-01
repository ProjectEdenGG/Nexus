package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.NPCShopMenuBuilder;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.features.quests.QuestShopMenu;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public enum Pugmas24ShopMenu implements QuestShopMenu {

	BLACKSMITH(Pugmas24NPC.BLACKSMITH, NPCShopMenu.builder().title("Blacksmith")
		.products(new ArrayList<>() {{
			add(new Product(Material.STONE_PICKAXE).price(Currency.COIN_POUCH, Price.of(10)));
		}})),

	;

	private final InteractableNPC NPC;
	private final NPCShopMenuBuilder shopBuilder;
}
