package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.Merchants.MerchantNPC;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Ores.OreType;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.LocationUtils.CardinalDirection;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.MerchantBuilder;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.addTokenMax;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class TheMines implements Listener {

	static {
		addTokenMax("themines_" + OreType.COAL.name(), 4);
		addTokenMax("themines_" + OreType.IRON_NUGGET.name(), 4);
		addTokenMax("themines_" + OreType.LUMINITE_NUGGET.name(), 4);
		addTokenMax("themines_" + OreType.MITHRIL.name(), 4);
		addTokenMax("themines_" + OreType.ADAMANTITE.name(), 4);
		addTokenMax("themines_" + OreType.NECRITE.name(), 4);
		addTokenMax("themines_" + OreType.LIGHT_ANIMICA.name(), 4);
	}

	@EventHandler
	public void onClickSellCrate(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!Pugmas20.isAtPugmas(player)) return;

		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block)) return;

		Material type = block.getType();
		String crateType = null;
		if (MaterialTag.SIGNS.isTagged(type)) {
			crateType = getCrateType(block);
		} else {
			for (CardinalDirection direction : CardinalDirection.values()) {
				Block relative = block.getRelative(direction.toBlockFace());
				if (MaterialTag.SIGNS.isTagged(relative.getType())) {
					crateType = getCrateType(relative);
					if (crateType != null)
						break;
				}
			}
		}

		if (crateType == null) return;

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);
		if (QuestStage.COMPLETE.equals(user.getMinesStage())) return;

		event.setCancelled(true);

		Inventory inv = Bukkit.createInventory(null, 27, colorize("&eSell Crate - " + crateType));
		player.openInventory(inv);
	}

	private String getCrateType(Block block) {
		Sign sign = (Sign) block.getState();
		String line1 = sign.getLine(0);
		String line2 = sign.getLine(1);
		if (stripColor(line1).equals("[Sell Crate]") && stripColor(line2).contains("Ingots"))
			return line2;
		return null;
	}

	@EventHandler
	public void onSellCrateClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		String title = stripColor(event.getView().getTitle());
		if (!title.contains(stripColor("Sell Crate - Ingots"))) return;

		List<MerchantBuilder.TradeBuilder> tradeBuilders = MerchantNPC.THEMINES_SELLCRATE.getTrades();

		if (tradeBuilders == null || tradeBuilders.size() == 0) {
			player.getInventory().addItem(event.getInventory().getContents());
			return;
		}

		int profit = 0;
		OreType key;
		for (ItemStack item : event.getInventory().getContents()) {
			if (ItemUtils.isNullOrAir(item)) {
				continue;
			}

			boolean foundTrade = false;
			boolean leftovers = false;
			for (MerchantBuilder.TradeBuilder tradeBuilder : tradeBuilders) {
				ItemStack result = tradeBuilder.getResult();
				List<ItemStack> ingredients = tradeBuilder.getIngredients();
				if (ingredients.size() != 1) continue;
				ItemStack ingredient = ingredients.get(0);
				if (ItemUtils.isNullOrAir(ingredient)) continue;
				if (ItemUtils.isNullOrAir(result)) continue;

				key = OreType.ofIngot(ingredient.getType());
				if (key == null) continue;

				Material type = item.getType();
				if (type.equals(ingredient.getType())) {
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

								int testAmt = profit + result.getAmount();
								int excess = Pugmas20.checkDailyTokens(player, "themines_" + key.name(), testAmt);
								Utils.send(player, "Excess: " + excess);
								if (excess <= 0) {
									itemAmount -= ingredientAmount;
									profit += result.getAmount();
									Utils.send(player, "selling " + type + " | profit: " + profit);
								} else {
									Utils.send(player, "hit max for " + type);
								}

								item.setAmount(itemAmount);
							}
						}
					}

					// TODO: this aint workin
					Pugmas20.giveDailyTokens(player, "themines_" + key.name(), profit);
					Utils.send(player, "giving " + profit + " tokens as " + "themines_" + key.name());
					profit = 0;
				}
			}

			if (!foundTrade || leftovers || item.getAmount() > 0)
				player.getInventory().addItem(item);
		}
	}
}
