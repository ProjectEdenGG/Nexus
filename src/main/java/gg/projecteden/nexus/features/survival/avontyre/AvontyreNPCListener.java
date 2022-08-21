package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.nexus.features.shops.ShopUtils.prettyMoney;

// Temporary listener until jobs are complete
public class AvontyreNPCListener implements Listener {

	public AvontyreNPCListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		if (!AvontyreNPCs.HUNTER__NULL.is(event.getNPC()))
			return;

		new HunterJobMenu().open(event.getClicker());
	}

	@Rows(3)
	@Title("Hunter Shop")
	private static class HunterJobMenu extends InventoryProvider {
		private final BankerService bankerService = new BankerService();

		private static final Map<ItemStack, Double> ITEMS = Map.of(
			CustomMaterial.MOB_NET.getNamedItem(), 5000d
		);

		@Override
		public void init() {
			addCloseItem();

			final List<ClickableItem> items = new ArrayList<>();

			ITEMS.forEach((item, price) -> {
				final boolean canAfford = bankerService.get(viewer).has(price, ShopGroup.SURVIVAL);
				final ItemBuilder displayItem = new ItemBuilder(item).lore("&3Price: " + (canAfford ? "&a" : "&c") + prettyMoney(price));
				items.add(ClickableItem.of(displayItem, e -> {
					if (canAfford)
						ConfirmationMenu.builder()
							.onConfirm(e2 -> {
								try {
									bankerService.withdraw(viewer, price, ShopGroup.SURVIVAL, TransactionCause.MARKET_PURCHASE);
									PlayerUtils.giveItem(viewer, item);
									Shop.log(UUID0, viewer.getUniqueId(), ShopGroup.SURVIVAL, StringUtils.pretty(item).split(" ", 2)[1], 1, ExchangeType.SELL, String.valueOf(price), "");
								} catch (Exception ex) {
									MenuUtils.handleException(viewer, StringUtils.getPrefix("Jobs"), ex);
								}
							})
							.onFinally(e2 -> refresh())
							.open(viewer);
				}));
			});

			paginator().items(items).perPage(18).build();
		}

	}

}
