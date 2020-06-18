package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Merchants;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.MerchantBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.decolorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class SellCrates implements Listener {
	public SellCrates() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onClickSellCrate(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		Location loc = player.getLocation();
		if (!WGUtils.getRegionsAt(loc).contains(BearFair20.getProtectedRegion()))
			return;

		Block block = event.getClickedBlock();
		if (Utils.isNullOrAir(block)) return;

		Material type = block.getType();
		String crateType = "null";
		if (MaterialTag.SIGNS.isTagged(type)) {
			crateType = getCrateType(block);
		} else {
			Block north = block.getRelative(BlockFace.NORTH);
			Block east = block.getRelative(BlockFace.EAST);
			Block south = block.getRelative(BlockFace.SOUTH);
			Block west = block.getRelative(BlockFace.WEST);
			List<Block> relatives = Arrays.asList(north, east, south, west);
			for (Block relativeBlock : relatives) {
				if (MaterialTag.SIGNS.isTagged(relativeBlock.getType())) {
					crateType = getCrateType(relativeBlock);
					if (!crateType.equals("null"))
						break;
				}
			}
		}

		if (crateType.equals("null")) {
			return;
		}

		event.setCancelled(true);
		openSellCrate(event.getPlayer(), crateType);
	}

	private String getCrateType(Block block) {
		Sign sign = (Sign) block.getState();
		String line1 = sign.getLine(0);
		String line2 = sign.getLine(1);
		if (stripColor(line1).equals("[Sell Crate]") && stripColor(line2).contains("Items"))
			return line2;
		return "null";
	}

	private void openSellCrate(Player player, String type) {
		Inventory inv = Bukkit.createInventory(null, 27, colorize("&eSell Crate - " + type));
		player.openInventory(inv);
	}

	@EventHandler
	public void onSellCrateClose(InventoryCloseEvent event) {
		String title = stripColor(event.getView().getTitle());
		if (!title.contains(stripColor("Sell Crate - "))) return;

		String[] split = decolorize(title).toLowerCase().split(" - ");
		String crateType = split[1];
		List<MerchantBuilder.TradeBuilder> tradeBuilders = new ArrayList<>();

		if (crateType.contains("fishing")) {
			tradeBuilders = Merchants.BFMerchant.FISHERMAN.getTrades();
		} else if (crateType.contains("farming")) {
			tradeBuilders = Merchants.BFMerchant.BOTANIST.getTrades();
		}

		// Give items back if no trades found
		if (tradeBuilders == null || tradeBuilders.size() == 0) {
			event.getPlayer().getInventory().addItem(event.getInventory().getContents());
			return;
		}

		List<ItemStack> profit = new ArrayList<>();
		for (ItemStack item : event.getInventory().getContents()) {
			if (Utils.isNullOrAir(item)) {
				continue;
			}

			boolean foundTrade = false;
			boolean leftovers = false;
			for (MerchantBuilder.TradeBuilder tradeBuilder : tradeBuilders) {
				ItemStack result = tradeBuilder.getResult();
				List<ItemStack> ingredients = tradeBuilder.getIngredients();
				if (ingredients.size() != 1) continue;
				ItemStack ingredient = ingredients.get(0);
				if (Utils.isNullOrAir(ingredient)) continue;
				if (Utils.isNullOrAir(result)) continue;

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
				event.getPlayer().getInventory().addItem(item);
		}

		if (profit.size() == 0) return;

		for (ItemStack itemStack : profit) {
			event.getPlayer().getInventory().addItem(itemStack);
		}
	}
}
