package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pugmas25SellCrate implements Listener {
	private static final String HEADER = "&0&l[&3Sell Crate&0&l]";
	private static final String HEADER_UNFORMATTED = "[Sell Crate]";
	private static final String INVENTORY_TITLE = "&3Sell Crate - ";
	private static final ItemBuilder COIN = new ItemBuilder(ItemModelType.GOLD_COINS_1).amount(1);

	public Pugmas25SellCrate() {
		Nexus.registerListener(this);
	}

	@Getter
	@AllArgsConstructor
	public enum Pugmas25SellCrateType {
		FISHING("&bFishing Items"),
		FARMING("&aFarming Items"),
		MINING("&7Mining Items"),
		;

		private final String line;

		public void openMenu(Player player) {
			Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&0" + StringUtils.stripColor(INVENTORY_TITLE + line)));
			player.openInventory(inv);
		}

		public @Nullable List<TradeBuilder> getTrades() {
			switch (this) {
				case FISHING -> {
					return new ArrayList<>() {{
						Pugmas25.get().getFishingLoot().forEach(loot -> {
							if (loot.getGold() == null)
								return;

							add(new TradeBuilder().ingredient(loot.getItem()).result(COIN.clone().amount(loot.getGold())));
						});
					}};
				}

				case FARMING -> {
					return new ArrayList<>() {{
						add(new TradeBuilder().ingredient(Material.HAY_BLOCK, 16).result(COIN.clone()));
						add(new TradeBuilder().ingredient(Material.CARROT, 64).result(COIN.clone()));
					}};
				}

				case MINING -> {
					return new ArrayList<>() {{
						add(new TradeBuilder().ingredient(Material.COAL, 8).result(COIN.clone()));
						add(new TradeBuilder().ingredient(Material.LAPIS_LAZULI, 8).result(COIN.clone()));
						add(new TradeBuilder().ingredient(Material.COPPER_INGOT, 4).result(COIN.clone()));
						add(new TradeBuilder().ingredient(Material.GOLD_INGOT, 1).result(COIN.clone().amount(4)));
						add(new TradeBuilder().ingredient(Material.IRON_INGOT, 1).result(COIN.clone().amount(8)));
						add(new TradeBuilder().ingredient(Material.DIAMOND, 1).result(COIN.clone().amount(24)));
						add(new TradeBuilder().ingredient(Material.EMERALD, 1).result(COIN.clone().amount(30)));
						add(new TradeBuilder().ingredient(Material.NETHERITE_INGOT, 1).result(COIN.clone().amount(50)));
					}};
				}
			}
			;

			return null;
		}

		public static Pugmas25SellCrateType of(Block block) {
			Sign sign = getSign(block);
			if (sign == null)
				return null;

			var lines = sign.getLines();
			String line1 = StringUtils.stripColor(lines[0]);
			String line2 = StringUtils.stripColor(lines[1]).split(" ")[0].toUpperCase();
			if (!HEADER_UNFORMATTED.equalsIgnoreCase(line1))
				return null;

			try {
				return valueOf(line2);
			} catch (Exception ignored) {
			}

			return null;
		}

		public static Pugmas25SellCrateType of(InventoryView inv) {
			String title = StringUtils.stripColor(inv.getTitle());
			if (!title.contains(StringUtils.stripColor(INVENTORY_TITLE)))
				return null;

			try {
				return valueOf(StringUtils.decolorize(title).toLowerCase().split(" - ")[1].toUpperCase());
			} catch (Exception ignored) {
			}

			return null;
		}

		public void applyToSign(Block block) {
			Sign sign = getSign(block);
			if (sign == null)
				return;

			sign.setLine(0, StringUtils.colorize(HEADER));
			sign.setLine(1, StringUtils.colorize(this.line));
			sign.setWaxed(true);
			sign.update();
		}

		private static @Nullable Sign getSign(Block block) {
			if (Nullables.isNullOrAir(block))
				return null;

			Material type = block.getType();
			Sign sign = null;
			if (MaterialTag.SIGNS.isTagged(type)) {
				sign = (Sign) block.getState();
			} else {
				for (Block relativeBlock : BlockUtils.getAdjacentBlocks(block))
					if (MaterialTag.SIGNS.isTagged(relativeBlock.getType()))
						sign = (Sign) relativeBlock.getState();
			}

			return sign;
		}
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
			PlayerUtils.send(player, "Found no trades, returning items");
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
			if (!foundTrade || leftovers) {
				PlayerUtils.giveItem(player, item);
				if (!foundTrade)
					PlayerUtils.send(player, "Found no trades, returning items");
				else
					PlayerUtils.send(player, "There were leftovers, returning items");
			}
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
