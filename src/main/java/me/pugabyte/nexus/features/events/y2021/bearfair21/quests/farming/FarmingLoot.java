package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.farming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.Merchants;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MerchantBuilder.TradeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum FarmingLoot {
	WHEAT(Material.WHEAT, 1, 1),
	HAYBALE(Material.HAY_BLOCK, 1, 1),
	BEETROOT(Material.BEETROOT, 1, 1),
	CARROT(Material.CARROT, 1, 1),
	POTATO(Material.POTATO, 1, 1),
	SUGAR_CANE(Material.SUGAR_CANE, 1, 1),
	PUMPKIN(Material.PUMPKIN, 1, 1),
	MELON(Material.MELON, 1, 1),
	MELON_SLICE(Material.MELON_SLICE, 1, 1),
	;


	Material material;
	int amount;
	int gold;

	public ItemStack getDrops(int amount) {
		return new ItemBuilder(this.material).amount(amount).build();
	}

	public static List<TradeBuilder> getTrades() {
		return new ArrayList<>() {{
			for (FarmingLoot loot : values()) {
				add(new TradeBuilder()
						.result(loot.getDrops(loot.getAmount()))
						.ingredient(Merchants.goldNugget.clone().amount(loot.getGold()).build())
				);
			}
		}};
	}
}
