package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Pending Items")
public class ItemStatusMenu extends InventoryProvider {
	private final LegacyItemTransferUser user;
	private final ReviewStatus status;

	public ItemStatusMenu(LegacyItemTransferUser user, ReviewStatus status) {
		this.user = user;
		this.status = status;
	}

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();

		final boolean inLegacy = PlayerUtils.isSelf(viewer, user) && WorldGroup.of(viewer) == WorldGroup.LEGACY;

		for (ItemStack item : user.getItems(status)) {
			if (inLegacy && status == ReviewStatus.PENDING)
				items.add(ClickableItem.of(new ItemBuilder(item).lore("Click to cancel"), e -> {
					user.getItems(ReviewStatus.PENDING).remove(item);
					PlayerUtils.giveItem(user, item);
					refresh();
				}));
			else
				items.add(ClickableItem.empty(item));
		}

		paginate(items);
	}

}
