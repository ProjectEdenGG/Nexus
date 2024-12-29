package gg.projecteden.nexus.features.survival;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import com.gmail.nossr50.events.skills.salvage.McMMOPlayerSalvageCheckEvent;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.survival.difficulty.Difficulty;
import gg.projecteden.nexus.features.survival.difficulty.ForDifficulty;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.StringUtils.Gradient;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// TODO: if item is put on the shop, should it always show integrity lore ?
@ForDifficulty(Difficulty.HARD)
public class MendingIntegrity extends Feature implements Listener {
	private static final String NBT_KEY = "MendingIntegrity";
	private static final double MAX_INTEGRITY = 100;

	public static void update(ItemStack item, @Nullable Player player) {
		if (player == null)
			return;

		if (!Difficulty.of(player).isApplicable(MendingIntegrity.class)) {
			if (!hasIntegrityLore(item))
				return;

			removeIntegrityLore(item);
			return;
		}

		ItemMeta meta = item.getItemMeta();
		if (!meta.hasEnchants())
			return;

		if (!meta.hasEnchant(Enchantment.MENDING))
			return;

		if (hasIntegrity(item)) {
			if (!hasIntegrityLore(item))
				setIntegrity(item, getIntegrity(item));

			return;
		}

		setMaxIntegrity(item);
	}

	@EventHandler
	public void on(McMMOPlayerSalvageCheckEvent event) {
		ItemStack item = event.getSalvageItem();
		ItemStack enchantedBook = event.getEnchantedBook();
		if (Nullables.isNullOrAir(enchantedBook))
			return;

		ItemMeta meta = enchantedBook.getItemMeta();
		EnchantmentStorageMeta enchantedBookMeta = (EnchantmentStorageMeta) meta;
		if (!enchantedBookMeta.getStoredEnchants().containsKey(Enchantment.MENDING))
			return;

		if (!Difficulty.of(event.getPlayer()).isApplicable(this))
			return;

		setIntegrity(enchantedBook, getIntegrity(item));
	}

	@EventHandler
	public void on(EnchantItemEvent event) {
		if (!Difficulty.of(event.getEnchanter()).isApplicable(this))
			return;

		if (event.getEnchantsToAdd().containsKey(Enchantment.MENDING)) {
			setMaxIntegrity(event.getItem());
		}
	}

	@EventHandler
	public void on(PrepareResultEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		if (!Survival.isInWorldGroup(player))
			return;

		if (!Difficulty.of(player).isApplicable(this))
			return;

		ItemStack result = event.getResult();
		if (Nullables.isNullOrAir(result))
			return;

		Inventory inv = event.getInventory();
		if (inv instanceof AnvilInventory anvilInv) {
			ItemStack firstItem = anvilInv.getFirstItem();
			ItemStack secondItem = anvilInv.getSecondItem();
			if (Nullables.isNullOrAir(firstItem) || Nullables.isNullOrAir(secondItem))
				return;

			if (hasIntegrity(firstItem) && hasIntegrity(secondItem)) {
				double integritySum = getIntegrity(firstItem) + getIntegrity(secondItem);

				setIntegrity(result, integritySum);
			} else {
				if (secondItem.getType() == Material.ENCHANTED_BOOK) {
					if (!(secondItem.getItemMeta() instanceof EnchantmentStorageMeta enchantedBookMeta))
						return;

					if (!enchantedBookMeta.getStoredEnchants().containsKey(Enchantment.MENDING))
						return;

					setIntegrity(result, getIntegrity(secondItem));
				}
			}
		} else if (inv instanceof GrindstoneInventory) {
			if (!result.getEnchantments().containsKey(Enchantment.MENDING) && hasIntegrity(result)) {
				removeIntegrity(result);
			}
		}
	}

	@EventHandler
	public void on(PlayerItemMendEvent event) {
		if (event.isCancelled())
			return;

		if (!Survival.isInWorldGroup(event.getPlayer()))
			return;

		if (!Difficulty.of(event.getPlayer()).isApplicable(this))
			return;

		ItemStack item = event.getItem();
		double percentage = getIntegrity(item);

		double failChance = (100 - percentage) / 5.0;
		if (RandomUtils.chanceOf(failChance) || percentage == 0) {
			event.setCancelled(true);
			return;
		}

		if (RandomUtils.chanceOf(80))
			return;

		int repairAmount = event.getRepairAmount();
		updateIntegrity(item, repairAmount);
	}

	private static boolean hasIntegrityLore(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasLore())
			return false;

		List<String> lore = meta.getLore();
		if (Nullables.isNullOrEmpty(lore))
			return false;

		return lore.stream().filter(line -> line.contains("Mending Integrity")).toList().size() > 0;
	}

	private static void removeIntegrityLore(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null || lore.isEmpty()) {
			lore = new ArrayList<>();
		}

		List<String> newLore = new ArrayList<>();

		for (String line : lore) {
			String strippedLine = StringUtils.stripColor(line);
			if (strippedLine.contains("Mending Integrity"))
				continue;

			newLore.add(line);
		}

		meta.setLore(newLore);
		item.setItemMeta(meta);
	}

	public static void removeIntegrity(ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		if (nbtItem.hasKey(NBT_KEY)) {
			nbtItem.removeKey(NBT_KEY);
			nbtItem.applyNBT(item);
		}

		removeIntegrityLore(item);
	}

	public static double getIntegrity(ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		if (nbtItem.hasKey(NBT_KEY)) {
			return nbtItem.getDouble(NBT_KEY);
		}

		return MAX_INTEGRITY;
	}

	public static boolean hasIntegrity(ItemStack item) {
		return new NBTItem(item).hasKey(NBT_KEY);
	}

	public static void setMaxIntegrity(ItemStack item) {
		setIntegrity(item, MAX_INTEGRITY);
	}

	public static void setIntegrity(ItemStack item, double integrity) {
		integrity = clamp(integrity);

		NBTItem nbtItem = new NBTItem(item);
		nbtItem.setDouble(NBT_KEY, integrity);
		nbtItem.applyNBT(item);

		// update lore

		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null || lore.isEmpty()) {
			lore = new ArrayList<>();
		}

		List<String> newLore = new ArrayList<>();

		newLore.add(getIntegrityLore(round(integrity, 1)));

		for (String line : lore) {
			String strippedLine = StringUtils.stripColor(line);
			if (strippedLine.contains("Mending Integrity"))
				continue;

			newLore.add(line);
		}

		meta.setLore(newLore);
		item.setItemMeta(meta);
	}

	public static void updateIntegrity(ItemStack item, int repairAmount) {
		double integrity = getIntegrity(item);

		int maxDurability = item.getType().getMaxDurability();
		integrity = getNewIntegrity(integrity, repairAmount, maxDurability);

		setIntegrity(item, integrity);
	}

	private static double getNewIntegrity(double integrity, double repairAmount, double maxDurability) {
		double removeAmount = (repairAmount / maxDurability) * 100;

		integrity -= removeAmount;
		integrity = clamp(integrity);

		return integrity;
	}

	static final String COLOR_CODES = Gradient.ofTypes(List.of(ColorType.RED, ColorType.ORANGE, ColorType.YELLOW, ColorType.LIGHT_GREEN)).apply("|".repeat(100));

	private static String getIntegrityLore(double integrity) {
		String message = "&fMending Integrity: ";

		int integrityInt = (int) integrity;
		integrity = round(integrity);

		String colorCode;
		try {
			colorCode = COLOR_CODES.substring(integrityInt * 15, ((integrityInt + 1) * 15)).replace("|", "");
		} catch (Exception ex) {
			if (integrity >= 75)
				colorCode = "&a";
			else if (integrity >= 50)
				colorCode = "&e";
			else if (integrity >= 25)
				colorCode = "&6";
			else
				colorCode = "&4";
		}

		return StringUtils.colorize(message + colorCode + integrity + "%");
	}

	private static double clamp(double integrity) {
		integrity = round(integrity);
		return MathUtils.clamp(integrity, 0, MAX_INTEGRITY);
	}

	private static double round(double integrity) {
		return round(integrity, 2);
	}

	private static double round(double integrity, int places) {
		return MathUtils.round(integrity, places);
	}

	@EventHandler
	public void on(EntityDeathEvent event) {
		if (!event.getEntity().getRemoveWhenFarAway())
			return;

		for (ItemStack drop : event.getDrops()) {
			if (Nullables.isNullOrAir(drop))
				continue;

			if (drop.getType() != Material.TRIDENT)
				continue;

			if (drop.getItemMeta().hasLore())
				continue;

			if (drop.getItemMeta() instanceof Damageable damageable) {
				damageable.setDamage(0);
				drop.setItemMeta(damageable);
			}
		}
	}

}
