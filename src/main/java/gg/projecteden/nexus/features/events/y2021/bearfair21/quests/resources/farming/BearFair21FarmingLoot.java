package gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.farming;

import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21Merchants;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum BearFair21FarmingLoot {
	HAY_BALE(Material.HAY_BLOCK, 16, 1),
	BEETROOT(Material.BEETROOT, 32, 1),
	CARROT(Material.CARROT, 64, 1),
	POTATO(Material.POTATO, 64, 1),
	SUGAR_CANE(Material.SUGAR_CANE, 48, 1),
	PUMPKIN(Material.PUMPKIN, 12, 1),
	MELON(Material.MELON, 8, 1),
	COCOA_BEANS(Material.COCOA_BEANS, 6, 1),
	;

	Material material;
	int amount;
	int gold;

	public ItemStack getDrops(int amount) {
		return new ItemBuilder(this.material).amount(amount).build();
	}

	public static List<TradeBuilder> getTrades() {
		return new ArrayList<>() {{
			for (BearFair21FarmingLoot loot : values()) {
				add(new TradeBuilder()
						.result(BearFair21Merchants.goldNugget.clone().amount(loot.getGold()).build())
						.ingredient(loot.getDrops(loot.getAmount()))
				);
			}
		}};
	}
}
