package me.pugabyte.nexus.features.events.y2020.pugmas20.models;

import lombok.Getter;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.LightTheTree;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.TheMines.OreType;
import me.pugabyte.nexus.utils.MerchantBuilder;
import me.pugabyte.nexus.utils.MerchantBuilder.TradeBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Merchants {
	public static void openMerchant(Player player, int id) {
		MerchantNPC merchantNPC = MerchantNPC.getFromId(id);
		if (merchantNPC == null)
			return;

		new MerchantBuilder(StringUtils.camelCase(merchantNPC.name())).trades(merchantNPC.getTrades()).open(player);
	}

	public enum MerchantNPC {
		ORNAMENT_VENDOR(3083) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					for (Ornament ornament : Ornament.values()) {
						ItemStack result = ornament.getSkull();
						ItemStack ingredient = ornament.getTreeType().getLog(32);

						add(new TradeBuilder()
								.result(result)
								.ingredient(ingredient));
					}
				}};
			}
		},
		BLACKSMITH(3109) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(LightTheTree.steel_ingot)
							.ingredient(OreType.COAL.getIngot(1))
							.ingredient(OreType.IRON.getIngot(8)));
				}};
			}
		},
		THEMINES_SELLCRATE(-1) {
			@Override
			public List<TradeBuilder> getTrades() {
				return new ArrayList<TradeBuilder>() {{
					add(new TradeBuilder()
							.result(Pugmas20.questItem(Material.GOLD_INGOT))
							.ingredient(OreType.LIGHT_ANIMICA.getIngot(15)));
					add(new TradeBuilder()
							.result(Pugmas20.questItem(Material.GOLD_INGOT))
							.ingredient(OreType.NECRITE.getIngot(15)));
					add(new TradeBuilder()
							.result(Pugmas20.questItem(Material.GOLD_INGOT))
							.ingredient(OreType.ADAMANTITE.getIngot(20)));
					add(new TradeBuilder()
							.result(Pugmas20.questItem(Material.GOLD_INGOT))
							.ingredient(OreType.MITHRIL.getIngot(20)));
					add(new TradeBuilder()
							.result(Pugmas20.questItem(Material.GOLD_INGOT))
							.ingredient(OreType.IRON.getIngot(30)));
					add(new TradeBuilder()
							.result(Pugmas20.questItem(Material.GOLD_INGOT))
							.ingredient(OreType.LUMINITE.getIngot(15)));
					add(new TradeBuilder()
							.result(Pugmas20.questItem(Material.GOLD_INGOT))
							.ingredient(OreType.COAL.getIngot(20)));
				}};
			}
		};

		@Getter
		private final int npcId;

		MerchantNPC(int npcId) {
			this.npcId = npcId;
		}

		public static MerchantNPC getFromId(int id) {
			for (MerchantNPC merchant : values()) {
				if (merchant.getNpcId() == id)
					return merchant;
			}

			return null;
		}

		public abstract List<TradeBuilder> getTrades();
	}
}
