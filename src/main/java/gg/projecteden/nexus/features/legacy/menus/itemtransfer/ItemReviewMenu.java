package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.nexus.features.legacy.Legacy;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Review Items")
@RequiredArgsConstructor
public class ItemReviewMenu extends InventoryProvider {
	private final LegacyItemTransferUserService service = new LegacyItemTransferUserService();
	private final LegacyItemTransferUser user;

	@Override
	public void init() {
		addBackItem(e -> new ReviewableMenu().open(viewer));

		List<ClickableItem> items = new ArrayList<>();

		contents.set(0, 3, ClickableItem.of(Material.RED_CONCRETE, "&cDeny All Items", e -> ConfirmationMenu.builder()
			.onConfirm(e2 -> {
				int count = user.denyAll();
				service.save(user);
				viewer.closeInventory();
				PlayerUtils.send(viewer, Legacy.PREFIX + "Denied &e" + count + " &3items from " + user.getNerd().getColoredName());
			})
			.onCancel(e2 -> new ItemReviewMenu(user).open(viewer, contents.pagination().getPage()))
			.open(viewer)));

		contents.set(0, 5, ClickableItem.of(Material.LIME_CONCRETE, "&cAccept All Items", e -> ConfirmationMenu.builder()
			.onConfirm(e2 -> {
				int count = user.acceptAll();
				service.save(user);
				viewer.closeInventory();
				PlayerUtils.send(viewer, Legacy.PREFIX + "Accepted &e" + count + " &3items from " + user.getNerd().getColoredName());
			})
			.onCancel(e2 -> new ItemReviewMenu(user).open(viewer, contents.pagination().getPage()))
			.open(viewer)));

		for (ItemStack item : user.getItems(ReviewStatus.PENDING))
			items.add(ClickableItem.of(item, e ->
				new ItemReviewSubMenu(user, item, contents.pagination().getPage()).open(viewer)));

		paginate(items);
	}

}
