package gg.projecteden.nexus.features.events.store.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class EventStoreMenu extends MenuUtils implements InventoryProvider {

	abstract protected EventStoreMenu getPreviousMenu();

	abstract protected String getTitle();

	protected int getRows() {
		return 6;
	}

	@NotNull
	abstract protected List<ClickableItem> getItems(Player player);

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.title(getTitle())
				.size(getRows(), 9)
				.provider(this)
				.build()
				.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		if (getPreviousMenu() == null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> getPreviousMenu().open(player));

		ItemStack tokens = new ItemBuilder(Material.BOOK).name("Tokens").build();
		contents.set(0, 8, ClickableItem.empty(tokens));

		paginator(player, contents, getItems(player));
	}

	protected void charge(Player player, int price) {
		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		if (!user.hasTokens(price))
			throw new InvalidInputException("You do not have enough tokens to purchase that");

		user.takeTokens(price);
		service.save(user);
	}

	protected void chargeAndAddPermissions(Player player, int price, String... permissions) {
		charge(player, price);
		PermissionChange.set().player(player).permissions(permissions).runAsync();
		open(player);
	}

	protected void lore(Player player, ItemBuilder item, int price) {
		EventUserService service = new EventUserService();
		EventUser user = service.get(player);

		item
				.lore("")
				.lore("&6Price: " + (user.hasTokens(price) ? "&e" : "&c") + price + " event tokens")
				.lore("")
				.lore("&7Click to preview")
				.lore("&7Shift click to buy");
	}

}
