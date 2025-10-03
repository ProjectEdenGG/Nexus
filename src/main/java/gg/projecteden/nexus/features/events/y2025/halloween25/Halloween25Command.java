package gg.projecteden.nexus.features.events.y2025.halloween25;

import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.halloween25.Halloween25User;
import gg.projecteden.nexus.models.halloween25.Halloween25UserService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.Currency;
import lombok.NonNull;

import java.util.List;

public class Halloween25Command extends CustomCommand {

	public Halloween25Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("buy basket")
	void buy_basket() {
		Product candyBasket = new Product(BackpackTier.HALLOWEEN.create())
			.price(Currency.BALANCE, 2500)
			.onPurchase((player, inv) -> new Halloween25UserService().edit(player, Halloween25User::incrementCandyBaskets));

		NPCShopMenu.builder()
			.title("Want a new candy basket?")
			.npcId(VuLan24NPC.TOUR_GUIDE.getNpcId())
			.products(List.of(candyBasket))
			.shopGroup(ShopGroup.of(player()))
			.closeAfterPurchase(true)
			.open(player());
	}

}
