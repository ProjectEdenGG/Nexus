package me.pugabyte.bncore.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.shop.ShopService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.bncore.features.shops.ShopUtils.pretty;

public abstract class _ShopProvider extends MenuUtils implements InventoryProvider {
	protected ShopService service = new ShopService();
	@Getter
	protected _ShopProvider previousMenu;

	public void open(Player viewer) {
		open(viewer, 0);
	}

	abstract public void open(Player viewer, int page);

	@Override
	public void init(Player player, InventoryContents contents) {
		if (previousMenu == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> previousMenu.open(player));
		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT).name("&e&lBalance")
				.lore("&f$" + pretty(BNCore.getEcon().getBalance(player))).build()));
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

	protected void addPagination(Player player, InventoryContents contents, List<ClickableItem> items) {
		Pagination page = contents.pagination();
		page.setItemsPerPage(36);
		page.setItems(items.toArray(new ClickableItem[0]));
		page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

		if (!page.isFirst())
			contents.set(5, 0, ClickableItem.from(nameItem(new ItemStack(Material.ARROW, Math.max((page.getPage() + 1) - 1, 1)),
					"&fPrevious Page"), e -> open(player, page.previous().getPage())));
		if (!page.isLast())
			contents.set(5, 8, ClickableItem.from(nameItem(new ItemStack(Material.ARROW, (page.getPage() + 1) + 1),
					"&fNext Page"), e -> open(player, page.next().getPage())));
	}

}
