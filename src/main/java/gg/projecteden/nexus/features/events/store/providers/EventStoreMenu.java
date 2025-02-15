package gg.projecteden.nexus.features.events.store.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class EventStoreMenu extends InventoryProvider {

	abstract protected EventStoreMenu getPreviousMenu();

	@NotNull
	abstract protected List<ClickableItem> getItems(Player player);

	@Override
	public void init() {
		if (getPreviousMenu() == null)
			addCloseItem();
		else
			addBackItem(e -> getPreviousMenu().open(viewer));

		ItemStack tokens = new ItemBuilder(ItemModelType.EVENT_TOKEN).name("&e&lEvent Tokens").lore("&f" + getUser(viewer).getTokens()).build();
		contents.set(0, 8, ClickableItem.empty(tokens));

		paginate(getItems(viewer));
	}

	protected EventUser getUser(Player player) {
		return new EventUserService().get(player);
	}

	protected void charge(Player player, int price) {
		new EventUserService().edit(player, user -> user.charge(price));
	}

	protected void chargeAndAddPermissions(Player player, int price, String... permissions) {
		charge(player, price);
		PermissionChange.set().player(player).permissions(permissions).runAsync();
		open(player);
	}

	protected void lore(Player player, ItemBuilder item, int price) {
		item
				.lore("")
				.lore("&6Price: " + (getUser(player).hasTokens(price) ? "&e" : "&c") + price + " event tokens")
				.lore("")
				.lore("&7Click to preview")
				.lore("&7Shift click to buy");
	}

}
