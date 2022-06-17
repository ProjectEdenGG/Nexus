package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.nexus.features.legacy.Legacy;
import gg.projecteden.nexus.features.legacy.listeners.LegacyItems;
import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUserService;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Title("Legacy Item Transfer")
public class ItemTransferMenu implements TemporaryMenuListener {
	@Getter
	private final Player player;

	public ItemTransferMenu(Player player) {
		this.player = player;
		this.open();
	}

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		new LegacyItemTransferUserService().edit(player, user -> {
			// TODO Banned items list
			user.getItems(ReviewStatus.PENDING).addAll(LegacyItems.convert(contents));
			user.sendMessage(Legacy.PREFIX + "Successfully stored " + contents.stream().mapToInt(ItemStack::getAmount).sum() + " legacy items for staff review");
		});
	}

}
