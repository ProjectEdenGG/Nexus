package gg.projecteden.nexus.features.events.y2021.bearfair21.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21Quests;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21Merchants.BFMerchant;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.farming.BearFair21FarmingLoot;
import gg.projecteden.nexus.features.events.y2021.bearfair21.quests.resources.fishing.BearFair21FishingLoot;
import gg.projecteden.nexus.utils.MerchantBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BearFair21SellCrates implements Listener {
	public BearFair21SellCrates() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onClickSellCrate(PlayerInteractEvent event) {
		String[] lines = BearFair21Quests.getMenuBlockLines(event);
		if (lines == null)
			return;

		String crateType = getCrateType(lines);
		if (crateType == null)
			return;

		event.setCancelled(true);
		openSellCrate(event.getPlayer(), crateType);
	}

	private String getCrateType(String[] lines) {
		String line1 = lines[0];
		String line2 = lines[1];
		if ("[Sell Crate]".equals(StringUtils.stripColor(line1)))
			return line2;
		return null;
	}

	private void openSellCrate(Player player, String type) {
		Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&eSell Crate - " + type));
		player.openInventory(inv);
	}

	@EventHandler
	public void onSellCrateClose(InventoryCloseEvent event) {
		String title = StringUtils.stripColor(event.getView().getTitle());
		if (!title.contains(StringUtils.stripColor("Sell Crate - "))) return;

		String[] split = StringUtils.decolorize(title).toLowerCase().split(" - ");
		String crateType = split[1];
		List<MerchantBuilder.TradeBuilder> tradeBuilders = new ArrayList<>();

		Player player = (Player) event.getPlayer();
		if (crateType.contains("fishing"))
			tradeBuilders = BearFair21FishingLoot.getTrades();
		else if (crateType.contains("farming"))
			tradeBuilders = BearFair21FarmingLoot.getTrades();
		else if (crateType.contains("dyes"))
			tradeBuilders = BFMerchant.ARTIST.getTrades(player);
		else if (crateType.contains("woodcutting"))
			tradeBuilders = BFMerchant.LUMBERJACK.getTrades(player);

		// Give items back if no trades found
		if (tradeBuilders.isEmpty()) {
			PlayerUtils.giveItems((Player) event.getPlayer(), Arrays.asList(event.getInventory().getContents()));
			return;
		}

		List<ItemStack> profit = new ArrayList<>();
		for (ItemStack item : event.getInventory().getContents()) {
			if (Nullables.isNullOrAir(item))
				continue;

			boolean foundTrade = false;
			boolean leftovers = false;
			for (MerchantBuilder.TradeBuilder tradeBuilder : tradeBuilders) {
				ItemStack result = tradeBuilder.getResult();
				List<ItemStack> ingredients = tradeBuilder.getIngredients();
				if (ingredients.size() != 1) continue;

				ItemStack ingredient = ingredients.get(0);
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

		if (profit.size() == 0) return;

		for (ItemStack itemStack : profit)
			PlayerUtils.giveItem(player, itemStack);
	}
}
