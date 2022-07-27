package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Title("Players waiting for review")
public class ReviewableMenu extends InventoryProvider {
	private final LegacyItemTransferUserService service = new LegacyItemTransferUserService();

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();

		for (LegacyItemTransferUser user : service.getAll()) {
			final int count = user.getCount(ReviewStatus.PENDING);
			if (count == 0)
				continue;

			final ItemBuilder skull = new ItemBuilder(Material.PLAYER_HEAD)
				.name(user.getNerd().getColoredName())
				.skullOwner(user)
				.lore("&f" + count + " items pending review");

			items.add(ClickableItem.of(skull.build(), e -> new ItemReviewMenu(user).open(player)));
		}

		paginator().items(items).build();
	}

}
