package gg.projecteden.nexus.features.shops.providers.common;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener.CustomInventoryHolder;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.BrowseProductsProvider.ShulkerContentsProvider;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import static gg.projecteden.nexus.utils.ItemUtils.getShulkerContents;

public abstract class ShopProvider extends InventoryProvider {
	protected final ShopService service = new ShopService();
	protected ShopGroup shopGroup;
	@Getter
	protected ShopProvider previousMenu;
	@Getter
	protected int page = 0;
	@Getter
	protected ShopHolder holder = new ShopHolder();

	public static class ShopHolder extends CustomInventoryHolder {}

	@Override
	public void open(Player player) {
		try {
			open(player, page);
		} catch (Exception ex) {
			MenuUtils.handleException(player, Shops.PREFIX, ex);
		}
	}

	public void open(Player player, int page) {
		this.page = page;
		this.shopGroup = ShopGroup.of(player);
		super.open(player, page);
	}

	@Override
	public void init() {
		if (previousMenu == null)
			addCloseItem();
		else
			addBackItem(e -> previousMenu.open(player));

		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT).name("&e&lBalance")
				.lore("&f" + new BankerService().getBalanceFormatted(player, shopGroup)).build()));
	}

	protected boolean handleRightClick(Product product, ItemClickData clickData) {
		if (!(clickData.getEvent() instanceof InventoryClickEvent event))
			return false;

		if (event.getClick() != ClickType.RIGHT)
			return false;

		if (getShulkerContents(product.getItem()).isEmpty())
			return false;

		new ShulkerContentsProvider(this, product).open(clickData.getPlayer());
		return true;
	}

}
