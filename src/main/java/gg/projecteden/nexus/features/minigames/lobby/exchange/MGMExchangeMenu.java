package gg.projecteden.nexus.features.minigames.lobby.exchange;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;

public class MGMExchangeMenu extends InventoryProvider {

	private static final int VP_PRICE = 3;
	private static final int ECO_PRICE = 1000;

	private static final VoterService VOTER_SERVICE = new VoterService();
	private static final BankerService BANKER_SERVICE = new BankerService();
	private static final PerkOwnerService MGM_SERVICE = new PerkOwnerService();

	@Override
	protected int getRows(Integer page) {
		return 4;
	}

	@Override
	public String getTitle() {
		return "MGM Token Exchange";
	}

	@Override
	public void init() {
		addCloseItem();

		double eco = BANKER_SERVICE.getBalance(viewer, Shop.ShopGroup.SURVIVAL);
		int vp = VOTER_SERVICE.get(viewer).getPoints();

		// Balances
		contents.set(8, ClickableItem.empty(
			new ItemBuilder(Material.BOOK)
				.name("&eBalances")
				.lore(
					"&3Survival: &f" + BANKER_SERVICE.getBalanceFormatted(viewer, Shop.ShopGroup.SURVIVAL),
					"&3Vote Points: &f" + vp,
					"&3MGM Tokens: &f" + MGM_SERVICE.get(viewer).getTokens()
				)
				.build()
		));

		int[] amounts = {0, 1, 5, 10};

		// ECO
		for (int i = 1; i < 4; i++) {
			CustomMaterial material = switch(i) {
				case 1 -> CustomMaterial.GOLD_COINS_2;
				case 2 -> CustomMaterial.GOLD_COINS_6;
				case 3 -> CustomMaterial.GOLD_COINS_9;
				default -> CustomMaterial.NULL;
			};

			int finalI = i;
			contents.set(9 + (2 * i),
				ClickableItem.of(
					new ItemBuilder(material)
						.name("&e" + amounts[i] + " MGM Tokens")
						.amount(amounts[i])
						.lore(
							"&f" + StringUtils.prettyMoney(amounts[i] * ECO_PRICE),
							eco >= amounts[i] * ECO_PRICE ? "&7&oClick to Purchase" : "&cCannot afford"
						)
						.build(),
					e -> {
						if (eco < amounts[finalI] * ECO_PRICE)
							return;

						BANKER_SERVICE.withdraw(viewer, -amounts[finalI] * ECO_PRICE, Shop.ShopGroup.SURVIVAL, Transaction.TransactionCause.MGM_TOKEN_EXCHANGE);
						MGM_SERVICE.edit(viewer, user -> user.giveTokens(amounts[finalI]));

						refresh();
					}
				)
			);
		}

		// VP
		for (int i = 1; i < 4; i++) {
			CustomMaterial material = switch(i) {
				case 1 -> CustomMaterial.SILVER_COINS_9;
				case 2 -> CustomMaterial.SILVER_COINS_4;
				case 3 -> CustomMaterial.SILVER_COINS_7;
				default -> CustomMaterial.NULL;
			};

			int finalI = i;
			contents.set(18 + (2 * i),
				ClickableItem.of(
					new ItemBuilder(material)
						.name("&e" + amounts[i] + " MGM Tokens")
						.amount(amounts[i])
						.lore(
							"&f" + amounts[i] * VP_PRICE + " Vote Points",
							vp >= amounts[i] * VP_PRICE ? "&7&oClick to Purchase" : "&cCannot afford"
						)
						.build(),
					e -> {
						if (vp < amounts[finalI] * VP_PRICE)
							return;

						VOTER_SERVICE.edit(viewer, user -> user.takePoints(amounts[finalI] * VP_PRICE));
						MGM_SERVICE.edit(viewer, user -> user.giveTokens(amounts[finalI]));

						refresh();
					}
				)
			);
		}
	}

}
