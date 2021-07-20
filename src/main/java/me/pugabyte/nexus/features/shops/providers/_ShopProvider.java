package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.ItemClickData;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import lombok.Getter;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.shops.Shops;
import me.pugabyte.nexus.features.shops.providers.BrowseProductsProvider.ShulkerContentsProvider;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import static me.pugabyte.nexus.utils.ItemUtils.getShulkerContents;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

public abstract class _ShopProvider extends MenuUtils implements InventoryProvider {
	protected final ShopService service = new ShopService();
	protected ShopGroup shopGroup;
	@Getter
	protected _ShopProvider previousMenu;
	@Getter
	protected int page = 0;

	protected int rows = 6;
	protected int columns = 9;

	public void open(Player viewer) {
		try {
			open(viewer, page);
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, Shops.PREFIX, ex);
		}
	}

	public void open(Player viewer, Pagination pagination) {
		open(viewer, pagination.getPage());
	}

	abstract public void open(Player player, int page);

	public void open(Player viewer, int page, _ShopProvider provider, String title) {
		this.page = page;
		this.shopGroup = ShopGroup.of(viewer);
		try {
			SmartInventory.builder()
					.provider(provider)
					.title(colorize(title))
					.size(rows, columns)
					.build()
					.open(viewer, page);
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, Shops.PREFIX, ex);
		}
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (previousMenu == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> previousMenu.open(player));

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
