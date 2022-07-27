package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUserService;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Receive Items")
public class ItemReceiveMenu implements TemporaryMenuListener {
	@Getter
	private final Player player;
	private final LegacyItemTransferUserService service = new LegacyItemTransferUserService();

	public ItemReceiveMenu(Player player) {
		this.player = player;

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			throw new InvalidInputException("You must be in the survival world to receive your items");

		final LegacyItemTransferUser user = service.get(player);
		// TODO Receive denied items back in legacy world?
		final ReviewStatus status = ReviewStatus.ACCEPTED;

		if (user.getItems(status).isEmpty())
			throw new InvalidInputException("No " + status.name().toLowerCase() + " items available, " +
				(user.getItems(ReviewStatus.PENDING).isEmpty() ? "add them with '/legacy items transfer' in the legacy worlds" :
					"please wait for the staff team to review your items"));

		final List<ItemStack> contents = new ArrayList<>(user.getItems(status).subList(0, Math.min(user.getItems(status).size(), 6 * 9)));

		user.getItems(status).removeAll(contents);
		service.save(user);

		open(contents);

		service.save(user);
	}

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		service.edit(player, user -> user.getItems(ReviewStatus.ACCEPTED).addAll(contents));
	}

}
