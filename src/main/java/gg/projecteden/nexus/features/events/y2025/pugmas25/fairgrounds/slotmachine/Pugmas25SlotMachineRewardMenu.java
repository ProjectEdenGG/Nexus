package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine;

import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine.Pugmas25SlotMachineReward.Pugmas25SlotMachineRewardEnchant;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Rows(5)
@Title("Slot Machine Rewards")
public class Pugmas25SlotMachineRewardMenu extends InventoryProvider {
	ItemBuilder infoItem = new ItemBuilder(Material.BOOK).name("&eInfo").lore("&e2 &3of a kind -> &eHalf reward", "&e3 &3of a kind -> &eFull reward").itemFlags(ItemFlags.HIDE_ALL);
	ItemBuilder categoryPrize = new ItemBuilder(Material.DIAMOND).name("&bPrizes").itemFlags(ItemFlags.HIDE_ALL);
	ItemBuilder categoryTool = new ItemBuilder(Material.IRON_PICKAXE).name("&dTool Rewards").itemFlags(ItemFlags.HIDE_ALL);
	ItemBuilder categoryPenalty = new ItemBuilder(Material.TNT).name("&cPenalties").itemFlags(ItemFlags.HIDE_ALL);

	@Override
	public void init() {
		addCloseItem();
		contents.set(SlotPos.of(0, 8), ClickableItem.empty(infoItem));
		contents.set(SlotPos.of(0, 2), ClickableItem.empty(categoryPrize));
		contents.set(SlotPos.of(0, 4), ClickableItem.empty(categoryTool));
		contents.set(SlotPos.of(0, 6), ClickableItem.empty(categoryPenalty));

		for (Pugmas25SlotMachineReward reward : Pugmas25SlotMachineReward.values()) {
			ItemBuilder displayItem = reward.getDisplayItem().loreize(false).itemFlags(ItemFlags.HIDE_ALL);

			Pugmas25SlotMachineRewardEnchant rewardEnchant = Pugmas25SlotMachineRewardEnchant.of(reward);
			if (rewardEnchant != null) {
				List<String> lore = new ArrayList<>();
				for (String enchant : rewardEnchant.getEnchantStrings()) {
					lore.add("&3- &e" + StringUtils.camelCase(enchant));
				}
				displayItem.lore(lore);
			}

			contents.set(reward.getDisplaySlot(), ClickableItem.empty(displayItem));
		}
	}
}
