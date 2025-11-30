package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25SellCrateType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pugmas25SellCrate implements Listener {
	public static final String HEADER = "&0&l[&3Sell Crate&0&l]";
	public static final String HEADER_UNFORMATTED = "[Sell Crate]";
	public static final String INVENTORY_TITLE = "&3Sell Crate - ";
	public static final ItemBuilder COIN = new ItemBuilder(ItemModelType.GOLD_COINS_1).amount(1);

	public Pugmas25SellCrate() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onClickSellCrate(PlayerInteractEvent event) {
		if (!Pugmas25.get().isAtEvent(event))
			return;

		Pugmas25SellCrateType crateType = Pugmas25SellCrateType.of(event.getClickedBlock());
		if (crateType == null)
			return;

		event.setCancelled(true);
		crateType.openMenu(event.getPlayer());
	}

	@EventHandler
	public void onSellCrateClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (!Pugmas25.get().isAtEvent(player))
			return;

		Pugmas25SellCrateType type = Pugmas25SellCrateType.of(event.getView());
		if (type == null)
			return;

		List<TradeBuilder> tradeBuilders = type.getTrades();
		// Give items back if no trades found
		if (Nullables.isNullOrEmpty(tradeBuilders)) {
			PlayerUtils.giveItems((Player) event.getPlayer(), Arrays.asList(event.getInventory().getContents()));
			return;
		}

		List<ItemStack> profit = new ArrayList<>();
		for (ItemStack item : event.getInventory().getContents()) {
			if (Nullables.isNullOrAir(item))
				continue;

			boolean foundTrade = false;
			boolean leftovers = false;
			for (TradeBuilder tradeBuilder : tradeBuilders) {
				ItemStack result = tradeBuilder.getResult();
				List<ItemStack> ingredients = tradeBuilder.getIngredients();
				if (ingredients.size() != 1) continue;

				ItemStack ingredient = ingredients.getFirst();
				if (Nullables.isNullOrAir(ingredient)) continue;
				if (Nullables.isNullOrAir(result)) continue;

				if (item.getType().equals(ingredient.getType())) {
					if (item.getAmount() >= ingredient.getAmount()) {
						double loops = Math.ceil((item.getAmount() + 0D) / ingredient.getAmount());
						for (double i = 0; i < loops; i++) {
							int itemAmount = item.getAmount();
							int ingredientAmount = ingredient.getAmount();
							if (itemAmount < ingredientAmount) {
								leftovers = true;
								break;
							}

							item.setAmount(ingredientAmount);
							if (item.equals(ingredient)) {
								foundTrade = true;
								itemAmount -= ingredientAmount;
								item.setAmount(itemAmount);
								profit.add(result);
							}
						}
					}
				}
			}

			// If trade was not found for itemstack, give item back
			// If there were leftovers, give the edited item back
			if (!foundTrade || leftovers)
				PlayerUtils.giveItem(player, item);
		}

		if (!profit.isEmpty()) {
			int coins = 0;
			for (ItemStack itemStack : profit)
				coins += itemStack.getAmount();

			Currency.COIN_POUCH.deposit(player, Price.of(coins));
			PlayerUtils.send(player, StringUtils.getPrefix("Sell Crate") + "&3Deposited &e" + coins + " coins &3to Coin Pouch");
		}
	}
}
