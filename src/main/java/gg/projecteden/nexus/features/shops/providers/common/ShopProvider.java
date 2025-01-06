package gg.projecteden.nexus.features.shops.providers.common;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.ItemClickData;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.shops.Shops;
import gg.projecteden.nexus.features.shops.providers.BrowseProductsProvider.ShulkerContentsProvider;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class ShopProvider extends InventoryProvider {
	protected final ShopService service = new ShopService();
	protected ShopGroup shopGroup;
	@Getter
	protected ShopProvider previousMenu;
	@Getter
	protected int page = 0;
	@Getter
	protected ShopHolder holder;

	public static class ShopHolder extends SmartInventoryHolder {
		public ShopHolder(InventoryProvider provider) {
			super(provider);
		}
	}

	@Override
	public void open(Player viewer) {
		try {
			open(viewer, page);
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, Shops.PREFIX, ex);
		}
	}

	public void open(Player viewer, int page) {
		this.page = page;
		this.shopGroup = ShopGroup.of(viewer);
		this.holder = new ShopHolder(this);
		super.open(viewer, page);
	}

	@Override
	public void init() {
		if (previousMenu == null)
			addCloseItem();
		else
			addBackItem(e -> previousMenu.open(viewer));

		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT).name("&e&lBalance")
			.lore("&f" + getBalanceFormatted(viewer, shopGroup)).build()));
	}

	protected boolean handleRightClick(Product product, ItemClickData clickData) {
		if (!(clickData.getEvent() instanceof InventoryClickEvent event))
			return false;

		if (event.getClick() != ClickType.RIGHT)
			return false;

		if (ItemUtils.getShulkerContents(product.getItem()).isEmpty())
			return false;

		new ShulkerContentsProvider(this, product).open(clickData.getPlayer());
		return true;
	}

	protected String getBalanceFormatted(Player viewer, ShopGroup shopGroup) {
		return new BankerService().getBalanceFormatted(viewer, shopGroup);
	}

}
