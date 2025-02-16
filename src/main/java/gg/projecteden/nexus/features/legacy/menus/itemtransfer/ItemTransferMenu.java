package gg.projecteden.nexus.features.legacy.menus.itemtransfer;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.legacy.Legacy;
import gg.projecteden.nexus.features.legacy.listeners.LegacyItems;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUser.ReviewStatus;
import gg.projecteden.nexus.models.legacy.itemtransfer.LegacyItemTransferUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Title("Legacy Item Transfer")
public class ItemTransferMenu implements TemporaryMenuListener {
	@Getter
	private final Player player;

	public ItemTransferMenu(Player player) {
		this.player = player;
		this.open();
	}

	private static final MaterialTag NOT_ALLOWED = new MaterialTag(
		MaterialTag.TOOLS,
		MaterialTag.ARMOR,
		MaterialTag.WEAPONS,
		MaterialTag.ARROWS,
		MaterialTag.POTIONS,
		MaterialTag.MENU_BLOCKS,
		MaterialTag.ALL_MINERALS,
		MaterialTag.ALL_WOOD,
		MaterialTag.ITEMS_MUSIC_DISCS
	).append(
		Material.GOLDEN_APPLE,
		Material.ENCHANTED_GOLDEN_APPLE
	);

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		final List<ItemStack> shulkerBoxes = contents.stream()
			.filter(MaterialTag.SHULKER_BOXES::isTagged)
			.toList();

		for (ItemStack shulkerBox : shulkerBoxes) {
			contents.addAll(new ItemBuilder(shulkerBox).nonAirShulkerBoxContents());
			contents.remove(shulkerBox);
		}

		Map<CrateType, Integer> newCrateKeys = new HashMap<>();

		for (ItemStack content : new ArrayList<>(contents)) {
			final CrateType crateType = CrateType.fromKey(content);
			if (crateType != null) {
				newCrateKeys.put(crateType, newCrateKeys.getOrDefault(crateType, 0) + 1);
				contents.remove(content);
				continue;
			}

			if (!NOT_ALLOWED.isTagged(content.getType()))
				continue;
			else if (Model.of(content) != null)
				continue;

			contents.remove(content);
			PlayerUtils.giveItem(player, content);
		}

		new LegacyItemTransferUserService().edit(player, user -> {
			final int sum = contents.stream().mapToInt(ItemStack::getAmount).sum();
			if (sum > 0) {
				user.getItems(ReviewStatus.PENDING).addAll(LegacyItems.convert(player.getLocation(), contents));
				user.sendMessage(Legacy.PREFIX + "Successfully stored " + sum + " legacy items for staff review");
			}

			if (!newCrateKeys.isEmpty()) {
				newCrateKeys.forEach((crateType, amount) -> {
					user.getCrateKeys().put(crateType, user.getCrateKeys().getOrDefault(crateType, 0) + amount);
					user.sendMessage(Legacy.PREFIX + "Successfully stored " + amount + " " + StringUtils.camelCase(crateType) + " Crate keys for conversion");
				});
			}
		});
	}

}
