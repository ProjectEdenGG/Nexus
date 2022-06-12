package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Pending Items")
public class ItemPendingMenu extends InventoryProvider {
	private final LegacyItemTransferUser user;

	public ItemPendingMenu(Player player) {
		this.user = new LegacyItemTransferUserService().get(player);
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();

		final boolean inLegacy = WorldGroup.of(player) == WorldGroup.LEGACY;

		for (ItemStack item : user.getItems(ReviewStatus.PENDING)) {
			if (inLegacy)
				items.add(ClickableItem.of(new ItemBuilder(item).lore("Click to cancel"), e -> {
					user.getItems(ReviewStatus.PENDING).remove(item);
					PlayerUtils.giveItem(user, item);
					refresh();
				}));
			else
				items.add(ClickableItem.empty(item));
		}

		paginator().items(items).build();
	}

}
