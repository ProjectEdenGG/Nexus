package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

public abstract class _ShopProvider extends MenuUtils implements InventoryProvider {
	protected ShopService service = new ShopService();
	@Getter
	protected _ShopProvider previousMenu;
	@Getter
	protected int page = 0;

	public void open(Player viewer) {
		open(viewer, page);
	}

	abstract public void open(Player viewer, int page);

	public void open(Player viewer, int page, _ShopProvider provider, String title) {
		this.page = page;
		SmartInventory.builder()
				.provider(provider)
				.title(colorize(title))
				.size(6, 9)
				.build()
				.open(viewer, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (previousMenu == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> previousMenu.open(player));
		contents.set(0, 8, ClickableItem.empty(new ItemBuilder(Material.GOLD_INGOT).name("&e&lBalance")
				.lore("&f" + prettyMoney(Nexus.getEcon().getBalance(player))).build()));
	}

	@Override
	public void update(Player player, InventoryContents contents) {}

}
