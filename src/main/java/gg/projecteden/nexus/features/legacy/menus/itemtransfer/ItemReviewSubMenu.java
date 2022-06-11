package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.legacy.itemtransfer.ItemTransferUser;
import gg.projecteden.nexus.models.legacy.itemtransfer.ItemTransferUserService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Title("Review Item")
@RequiredArgsConstructor
public class ItemReviewSubMenu extends InventoryProvider {
	private final ItemTransferUserService service = new ItemTransferUserService();
	private final ItemTransferUser user;
	private final ItemStack item;
	private final int parentPage;

	@Override
	public void init() {
		addBackItem(e -> new ItemReviewMenu(user).open(player, parentPage));

		contents.set(1, 3, ClickableItem.of(Material.RED_CONCRETE, "&cDeny Item", e -> {
			user.deny(item);
			service.save(user);
			new ItemReviewMenu(user).open(player, parentPage);
		}));

		contents.set(1, 5, ClickableItem.of(Material.LIME_CONCRETE, "&cAccept Item", e -> {
			user.accept(item);
			service.save(user);
			new ItemReviewMenu(user).open(player, parentPage);
		}));
	}

}
