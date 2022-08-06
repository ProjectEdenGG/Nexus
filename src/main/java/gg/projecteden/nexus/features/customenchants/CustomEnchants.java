package gg.projecteden.nexus.features.customenchants;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import gg.projecteden.nexus.features.survival.MendingIntegrity;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.customenchants.CustomEnchantsRegistration.register;
import static gg.projecteden.nexus.features.customenchants.CustomEnchantsRegistration.unregister;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class CustomEnchants extends Feature implements Listener {
	private static final Map<Class<? extends CustomEnchant>, CustomEnchant> enchants = new HashMap<>();

	public static CustomEnchant get(Class<? extends CustomEnchant> clazz) {
		return enchants.computeIfAbsent(clazz, $ -> CustomEnchantsRegistration.register(clazz));
	}

	public static Collection<CustomEnchant> getEnchants() {
		return enchants.values();
	}

	static Map<Class<? extends CustomEnchant>, CustomEnchant> getEnchantsMap() {
		return enchants;
	}

	@Override
	public void onStart() {
		register();
		OldCEConverter.load();
	}

	@Override
	public void onStop() {
		unregister();
	}

	@NotNull
	public static NamespacedKey getKey(Class<? extends CustomEnchant> enchant) {
		return CustomEnchantsRegistration.getKey(enchant.getSimpleName().replace("Enchant", "").toLowerCase());
	}

	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		ItemStack result = event.getItem();
		if (isNullOrAir(result))
			return;

		ItemStack updated = update(result, player);

		result.setItemMeta(updated.getItemMeta());
	}

	@EventHandler
	public void onItemCraft(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		ItemStack result = event.getInventory().getResult();
		if (isNullOrAir(result))
			return;

		ItemStack updated = update(result, player);

		event.getInventory().setResult(updated);
	}

	// Includes Anvil, Grindstone, and Smithing Table
	@EventHandler
	public void onPrepareResult(PrepareResultEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		ItemStack result = event.getResult();
		if (isNullOrAir(result))
			return;

		ItemStack updated = update(result, player);

		event.setResult(updated);
	}

	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player))
			return;

		ItemStack result = event.getResult();
		final ItemStack firstItem = event.getInventory().getFirstItem();
		final ItemStack secondItem = event.getInventory().getSecondItem();

		if (isNullOrAir(firstItem) || isNullOrAir(secondItem))
			return;

		Map<CustomEnchant, Integer> levels = getEnchantsToCombine(firstItem, secondItem);

		if (levels.isEmpty())
			return;

		if (isNullOrAir(result))
			result = getResult(firstItem, secondItem);

		applyCombinedLevels(result, levels);

		update(result, player);

		setResult(event.getInventory(), result);
	}

	private Map<CustomEnchant, Integer> getEnchantsToCombine(ItemStack firstItem, ItemStack secondItem) {
		Map<CustomEnchant, Integer> levels = new HashMap<>();

		for (CustomEnchant enchant : CustomEnchants.getEnchants()) {
			int firstLevel = enchant.getLevel(firstItem);
			int secondLevel = enchant.getLevel(secondItem);

			int finalLevel = Math.max(firstLevel, secondLevel);
			if (finalLevel == 0)
				continue;

			if (firstLevel == secondLevel)
				++finalLevel;

			levels.put(enchant, finalLevel);
		}

		return levels;
	}

	@NotNull
	private ItemStack getResult(ItemStack firstItem, ItemStack secondItem) {
		ItemStack result;

		if (firstItem.getType() == Material.ENCHANTED_BOOK && secondItem.getType() == Material.ENCHANTED_BOOK)
			result = new ItemStack(Material.ENCHANTED_BOOK);
		else
			if (firstItem.getType() == Material.ENCHANTED_BOOK)
				result = secondItem.clone();
			else
				result = firstItem.clone();

		return result;
	}

	private void applyCombinedLevels(ItemStack finalResult, Map<CustomEnchant, Integer> levels) {
		levels.forEach((enchant, level) -> {
			ItemMeta meta = finalResult.getItemMeta();

			if (meta instanceof EnchantmentStorageMeta storageMeta)
				storageMeta.addStoredEnchant(enchant, level, false);
			else
				meta.addEnchant(enchant, level, false);

			finalResult.setItemMeta(meta);
		});
	}

	private void setResult(AnvilInventory inventory, ItemStack result) {
		Tasks.wait(1, () -> {
			final Repairable repairable = (Repairable) result.getItemMeta();
			repairable.setRepairCost(repairable.getRepairCost() + 1); // TODO Fix + 1
			inventory.setRepairCost(repairable.getRepairCost());
			inventory.setResult(result);
		});
	}

	public static ItemStack update(ItemStack item, @Nullable Player player) {
		ItemMeta meta = item.getItemMeta();

		List<String> lore = new ArrayList<>();
		lore.addAll(getExistingLore(item));
		lore.addAll(0, getEnchantLore(item));

		meta.setLore(lore);
		item.setItemMeta(meta);
		MendingIntegrity.update(item, player);
		return item;
	}

	private static List<String> getExistingLore(ItemStack item) {
		final ItemMeta meta = item.getItemMeta();
		final List<String> lore = new ArrayList<>();

		if (!isNullOrEmpty(meta.getLore()))
			lines: for (String line : meta.getLore()) {
				if (!isNullOrEmpty(line))
					for (CustomEnchant enchant : CustomEnchants.getEnchants())
						if (stripColor(line).matches("(?i)^" + enchant.getName() + ".*"))
							continue lines;

				lore.add(line);
			}

		return lore;
	}

	private static List<String> getEnchantLore(ItemStack item) {
		return new ArrayList<>() {{
			for (CustomEnchant enchant : CustomEnchants.getEnchants()) {
				final int level = enchant.getLevel(item);
				if (level > 0)
					add(0, colorize("&7" + enchant.getDisplayName(level)));
			}
		}};
	}

}
