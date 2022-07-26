package gg.projecteden.nexus.features.survival;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Environments(Env.TEST)
public class MendingIntegrity extends Feature implements Listener {
	private static final String NBT_KEY = "MendingIntegrity";
	private static final double maxIntegrity = 100;

	public static void updateItem(ItemStack item) {
		// TODO: remove
		if (Nexus.getEnv() != Env.TEST)
			return;
		//

		ItemMeta meta = item.getItemMeta();
		if (!meta.hasEnchants())
			return;

		if (!meta.hasEnchant(Enchantment.MENDING))
			return;

		if (hasIntegrity(item))
			return;

		setIntegrity(item, maxIntegrity);
	}

	@EventHandler
	public void on(EnchantItemEvent event) {
		ItemStack item = event.getItem();
		if (event.getEnchantsToAdd().containsKey(Enchantment.MENDING)) {
			Dev.WAKKA.send("Enchant: set max integrity");
			setIntegrity(item, maxIntegrity);
		}
	}

	@EventHandler
	public void onPrepareItem(PrepareResultEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		ItemStack result = event.getResult();
		if (isNullOrAir(result))
			return;

		if (WorldGroup.of(player) != WorldGroup.SURVIVAL)
			return;

		Inventory inv = event.getInventory();
		if (!(inv instanceof AnvilInventory anvilInv))
			return;

		ItemStack firstItem = anvilInv.getFirstItem();
		ItemStack secondItem = anvilInv.getSecondItem();
		if (isNullOrAir(firstItem) || isNullOrAir(secondItem))
			return;

		if (secondItem.getType() == Material.ENCHANTED_BOOK) {
			if (!(secondItem.getItemMeta() instanceof EnchantmentStorageMeta enchantedBookMeta))
				return;

			if (!enchantedBookMeta.getStoredEnchants().containsKey(Enchantment.MENDING))
				return;

			setIntegrity(result, maxIntegrity);
			return;
		}

		if (hasIntegrity(firstItem) && hasIntegrity(secondItem)) {
			double integritySum = getIntegrity(firstItem) + getIntegrity(secondItem);

			setIntegrity(result, integritySum);
		}
	}

	@EventHandler
	public void on(PlayerItemMendEvent event) {
		if (event.isCancelled())
			return;

		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		ItemStack item = event.getItem();
		double percentage = getIntegrity(item);

		double failChance = (100 - percentage) / 5.0;
		if (RandomUtils.chanceOf(failChance) || percentage == 0) {
			event.setCancelled(true);
			return;
		}

		if (RandomUtils.chanceOf(80)) {
			return;
		}

		int repairAmount = event.getRepairAmount();
		updateIntegrity(item, repairAmount);
	}

	private static double getIntegrity(ItemStack item) {
		NBTItem nbtItem = new NBTItem(item);
		if (nbtItem.hasKey(NBT_KEY)) {
			return nbtItem.getDouble(NBT_KEY);
		}

		return maxIntegrity;
	}

	private static boolean hasIntegrity(ItemStack item) {
		return new NBTItem(item).hasKey(NBT_KEY);
	}

	private static void setIntegrity(ItemStack item, double integrity) {
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

	private static void updateIntegrity(ItemStack item, int repairAmount) {
		double integrity = getIntegrity(item);

		int maxDurability = item.getType().getMaxDurability();
		integrity = getNewIntegrity(integrity, repairAmount, maxDurability);

		setIntegrity(item, integrity);
	}

	private static double getNewIntegrity(double integrity, double repairAmount, double durability) {
		double removeAmount = (repairAmount / durability) * 100;

		integrity -= removeAmount;
		integrity = clamp(integrity);

		return integrity;
	}

	private static String getIntegrityLore(double integrity) {
		String message = "&fMending Integrity: ";
		if (integrity >= 75)
			message += "&a";
		else if (integrity >= 50)
			message += "&e";
		else if (integrity >= 25)
			message += "&6";
		else
			message += "&c";

		integrity = round(integrity);

		return StringUtils.colorize(message + integrity + "%");
	}

	private static double clamp(double integrity) {
		integrity = round(integrity);
		return MathUtils.clamp(integrity, 0, maxIntegrity);
	}

	private static double round(double integrity) {
		return round(integrity, 2);
	}

	private static double round(double integrity, int places) {
		return MathUtils.round(integrity, places);
	}
}
