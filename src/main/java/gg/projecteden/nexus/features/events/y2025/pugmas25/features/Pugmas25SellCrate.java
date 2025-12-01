package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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

		if (CommonQuestItem.COIN_POUCH.isNotInInventoryOf(event.getPlayer())) {
			PlayerUtils.send(event.getPlayer(), Pugmas25.PREFIX + "&cYou need your Coin Pouch to open the sell crate");
			return;
		}

		Player player = event.getPlayer();
		Pugmas25User user = new Pugmas25UserService().get(player);
		if (crateType == Pugmas25SellCrateType.FARMING && !user.isReceivedFarmerInstructions()) {
			PlayerUtils.send(player, Pugmas25.PREFIX + "&cThe Farmer has not given you permission to use this yet");
			return;
		}

		new SellCrateMenu(player, crateType);
	}

	@Getter
	@AllArgsConstructor
	public enum Pugmas25SellCrateType {
		FISHING("&bFishing Items"),
		FARMING("&aFarming Items"),
		MINING("&7Mining Items"),
		;

		private final String line;

		public String getInventoryTitle() {
			return StringUtils.colorize("&0" + StringUtils.stripColor(INVENTORY_TITLE + line));
		}

		public @Nullable List<TradeBuilder> getTrades() {
			switch (this) {
				case FISHING -> {
					return new ArrayList<>() {{
						Pugmas25.get().getFishingLoot().forEach(loot -> {
							if (loot.getGold() == null)
								return;

							add(new TradeBuilder().ingredient(loot.getItem()).result(COIN.clone().amount(loot.getGold() * 4)));
						});
					}};
				}

				case FARMING -> {
					return new ArrayList<>() {{
						add(new TradeBuilder().ingredient(Material.HAY_BLOCK, 8).result(COIN.clone().amount(6)));
						add(new TradeBuilder().ingredient(Material.CARROT, 64).result(COIN.clone().amount(6)));
					}};
				}

				case MINING -> {
					return new ArrayList<>() {{
						add(new TradeBuilder().ingredient(Material.COAL, 64).result(COIN.clone()));
						add(new TradeBuilder().ingredient(Material.LAPIS_LAZULI, 64).result(COIN.clone()));
						add(new TradeBuilder().ingredient(Material.COPPER_INGOT, 32).result(COIN.clone()));
						add(new TradeBuilder().ingredient(Material.GOLD_INGOT, 32).result(COIN.clone().amount(4)));
						add(new TradeBuilder().ingredient(Material.IRON_INGOT, 32).result(COIN.clone().amount(8)));
						add(new TradeBuilder().ingredient(Material.DIAMOND, 12).result(COIN.clone().amount(20)));
						add(new TradeBuilder().ingredient(Material.EMERALD, 4).result(COIN.clone().amount(30)));
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

	@Data
	public static class SellCrateMenu implements TemporaryMenuListener {
		private final Pugmas25SellCrateType crateType;
		private final Player player;

		public SellCrateMenu(Player player, Pugmas25SellCrateType crateType) {
			this.player = player;
			this.crateType = crateType;

			open(3);
		}

		@Override
		public String getTitle() {
			return crateType.getInventoryTitle();
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			if (!Pugmas25.get().isAtEvent(player))
				return;

			List<TradeBuilder> tradeBuilders = crateType.getTrades();

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

					if (!item.getType().equals(ingredient.getType()))
						continue;
					if (item.getAmount() < ingredient.getAmount())
						continue;

					double loops = Math.ceil((item.getAmount() + 0D) / ingredient.getAmount());
					for (double i = 0; i < loops; i++) {
						int itemAmount = item.getAmount();
						int ingredientAmount = ingredient.getAmount();
						if (itemAmount < ingredientAmount) {
							leftovers = true;
							break;
						}

						ItemModelType ingredientType = ItemModelType.of(ingredient);
						ItemModelType itemType = ItemModelType.of(item);

						boolean equals;
						if (ingredientType == null || itemType == null)
							equals = ItemUtils.isFuzzyMatch(item, ingredient);
						else
							equals = ingredientType == itemType;

						if (equals) {
							foundTrade = true;
							itemAmount -= ingredientAmount;
							item.setAmount(itemAmount);
							profit.add(result);
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
}
