package gg.projecteden.nexus.features.events.y2020.pugmas20.models;

import gg.projecteden.nexus.features.events.y2020.pugmas20.Pugmas20;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.LightTheTree;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.TheMines.OreType;
import gg.projecteden.nexus.models.pugmas20.Pugmas20User;
import gg.projecteden.nexus.models.pugmas20.Pugmas20UserService;
import gg.projecteden.nexus.utils.MerchantBuilder;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Merchants {
	public static void openMerchant(Player player, int id) {
		MerchantNPC merchantNPC = MerchantNPC.getFromId(id);
		if (merchantNPC == null)
			return;

		Pugmas20UserService service = new Pugmas20UserService();
		Pugmas20User user = service.get(player);

		List<TradeBuilder> trades = merchantNPC.getTrades(user);
		if (trades.isEmpty()) {
			user.sendMessage(Pugmas20.PREFIX + "No trades available");
			return;
		}

		new MerchantBuilder(StringUtils.camelCase(merchantNPC.name())).trades(trades).open(player);
	}

	public enum MerchantNPC {
		ORNAMENT_VENDOR(3083) {
			@Override
			public List<TradeBuilder> getTrades(Pugmas20User user) {
				return new ArrayList<>() {{
					for (Ornament ornament : Ornament.values()) {
						if (!user.canTradeOrnament(ornament))
							continue;

						add(new TradeBuilder()
								.result(ornament.getSkull())
								.ingredient(ornament.getTreeType().getLog(Ornament.logsPerOrnament))
								.maxUses(user.ornamentTradesLeft(ornament)));
					}
				}};
			}
		},
		BLACKSMITH(3109) {
			@Override
			public List<TradeBuilder> getTrades(Pugmas20User user) {
				return new ArrayList<>() {{
					add(new TradeBuilder()
							.result(LightTheTree.steel_ingot)
							.ingredient(OreType.COAL.getIngot(1))
							.ingredient(OreType.IRON.getIngot(8)));
				}};
			}
		},
		THEMINES_SELLCRATE(-1) {
			@Override
			public List<TradeBuilder> getTrades(Pugmas20User user) {
				Ornament.loadHeads();
				return new ArrayList<>() {{
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

		public abstract List<TradeBuilder> getTrades(Pugmas20User user);
	}
}
