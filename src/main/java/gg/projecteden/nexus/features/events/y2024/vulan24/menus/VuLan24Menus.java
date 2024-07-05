package gg.projecteden.nexus.features.events.y2024.vulan24.menus;

import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.NPCShopMenuBuilder;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VuLan24Menus {

	public static NPCShopMenuBuilder getBoatPicker() {
		return NPCShopMenu.builder()
				.title("Pick a boat")
				.npcId(VuLan24NPC.BOAT_SALESMAN.getNpcId())
				.shopGroup(null)
				.products(new ArrayList<>() {{
					for (Material boatType : MaterialTag.BOATS.getValues()) {
						add(Product.free(new ItemStack(boatType))
								.onPurchase((player, provider) -> {
									PlayerUtils.send(player, "TODO: DATABASE STUFF");
									// TODO: Take old boat from player
									// TODO: Save new boat to database
								}));
					}
				}});
	}

	public static NPCShopMenuBuilder getBambooHatShop() {
		return NPCShopMenu.builder()
				.title("Buy this Bamboo Hat costume?")
				.npcId(VuLan24NPC.HAT_SALESMAN.getNpcId())
				.shopGroup(null)
				.products(new ArrayList<>() {{
					add(new Product(CustomMaterial.COSTUMES_BAMBOO_HAT).price(Currency.EVENT_TOKENS, Price.of(100)));
				}});
	}

	public static NPCShopMenuBuilder getMinerShop() {
		return NPCShopMenu.builder()
				.title("Trade for a pickaxe?")
				.npcId(VuLan24NPC.MINER.getNpcId())
				.shopGroup(null)
				.products(new ArrayList<>() {{
					add(new Product(new ItemStack(Material.IRON_PICKAXE))
							.price(Currency.ITEM, Price.of(new ItemStack(Material.APPLE)))
					);
				}});
	}

	public static NPCShopMenuBuilder getGuideShop() {
		return NPCShopMenu.builder()
				.title("Want a backpack?")
				.npcId(VuLan24NPC.TOUR_GUIDE.getNpcId())
				.shopGroup(null)
				.products(List.of(Product.free(Backpacks.getBackpack())));
	}


}
