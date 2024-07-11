package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.y2024.vulan24.models.BoatTracker;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.NPCShopMenuBuilder;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.utils.BoatType;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.ItemBuilder;
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
					for (BoatType boatType : BoatType.values()) {
						add(Product.free(new ItemStack(boatType.getBoatMaterial()))
								.onPurchase((player, provider) -> BoatTracker.selectBoat(player, boatType)));
					}
				}});
	}

	public static NPCShopMenuBuilder getBambooHatShop() {
		String costumeId = "hat/misc/bamboo_hat";
		Costume costume = Costume.of(costumeId);
		if (costume == null)
			throw new InvalidInputException("Costume '" + costumeId + "' is null");

		ItemBuilder costumeItem = new ItemBuilder(costume.getItem()).name("&eBamboo Hat Costume");

		return NPCShopMenu.builder()
				.title("Buy this costume?")
				.npcId(VuLan24NPC.HAT_SALESMAN.getNpcId())
				.shopGroup(null)
				.products(new ArrayList<>() {{
					add(Product.virtual(costumeItem, Currency.EVENT_TOKENS, Price.of(100), (player, provider) -> {
						new CostumeUserService().edit(player.getUniqueId(), user -> user.getOwnedCostumes().add(costume.getId()));
						VuLan24.get().send(player, "You now own the Bamboo Hat costume!");
					}));
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
