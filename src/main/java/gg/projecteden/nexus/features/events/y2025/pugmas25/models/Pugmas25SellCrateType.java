package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25SellCrate;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.MerchantBuilder.TradeBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Pugmas25SellCrateType {
	FISHING("&bFishing Items"),
	FARMING("&aFarming Items"),
	MINING("&7Mining Items"),
	;

	private final String line;

	public void openMenu(Player player) {
		Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&0" + StringUtils.stripColor(Pugmas25SellCrate.INVENTORY_TITLE + line)));
		player.openInventory(inv);
	}

	public @Nullable List<TradeBuilder> getTrades() {
		switch (this) {
			case FISHING -> {
				return new ArrayList<>() {{
					Pugmas25.get().getFishingLoot().forEach(loot -> {
						if (loot.getGold() == null)
							return;

						add(new TradeBuilder().ingredient(loot.getItem()).result(Pugmas25SellCrate.COIN.clone().amount(loot.getGold() * 4)));
					});
				}};
			}

			case FARMING -> {
				return new ArrayList<>() {{
					add(new TradeBuilder().ingredient(Material.HAY_BLOCK, 8).result(Pugmas25SellCrate.COIN.clone().amount(6)));
					add(new TradeBuilder().ingredient(Material.CARROT, 64).result(Pugmas25SellCrate.COIN.clone().amount(6)));
				}};
			}

			case MINING -> {
				return new ArrayList<>() {{
					add(new TradeBuilder().ingredient(Material.COAL, 64).result(Pugmas25SellCrate.COIN.clone()));
					add(new TradeBuilder().ingredient(Material.LAPIS_LAZULI, 32).result(Pugmas25SellCrate.COIN.clone()));
					add(new TradeBuilder().ingredient(Material.COPPER_INGOT, 16).result(Pugmas25SellCrate.COIN.clone()));
					add(new TradeBuilder().ingredient(Material.GOLD_INGOT, 16).result(Pugmas25SellCrate.COIN.clone().amount(4)));
					add(new TradeBuilder().ingredient(Material.IRON_INGOT, 16).result(Pugmas25SellCrate.COIN.clone().amount(8)));
					add(new TradeBuilder().ingredient(Material.DIAMOND, 8).result(Pugmas25SellCrate.COIN.clone().amount(20)));
					add(new TradeBuilder().ingredient(Material.EMERALD, 4).result(Pugmas25SellCrate.COIN.clone().amount(30)));
					add(new TradeBuilder().ingredient(Material.NETHERITE_INGOT, 1).result(Pugmas25SellCrate.COIN.clone().amount(50)));
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
		if (!Pugmas25SellCrate.HEADER_UNFORMATTED.equalsIgnoreCase(line1))
			return null;

		try {
			return valueOf(line2);
		} catch (Exception ignored) {
		}

		return null;
	}

	public static Pugmas25SellCrateType of(InventoryView inv) {
		String title = StringUtils.stripColor(inv.getTitle());
		if (!title.contains(StringUtils.stripColor(Pugmas25SellCrate.INVENTORY_TITLE)))
			return null;

		String type = title.split(" - ")[1].split(" ")[0].toUpperCase();
		try {
			return valueOf(type);
		} catch (Exception ignored) {
		}

		return null;
	}

	public void applyToSign(Block block) {
		Sign sign = getSign(block);
		if (sign == null)
			return;

		sign.setLine(0, StringUtils.colorize(Pugmas25SellCrate.HEADER));
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
