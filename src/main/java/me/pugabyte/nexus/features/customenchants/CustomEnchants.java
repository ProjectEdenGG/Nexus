package me.pugabyte.nexus.features.customenchants;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import eden.utils.Utils;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eden.utils.StringUtils.isNullOrEmpty;
import static me.pugabyte.nexus.features.customenchants.CustomEnchantsRegistration.register;
import static me.pugabyte.nexus.features.customenchants.CustomEnchantsRegistration.unregister;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class CustomEnchants extends Feature implements Listener {
	private static final Map<Class<? extends CustomEnchant>, CustomEnchant> enchants = new HashMap<>();

	public static CustomEnchant get(Class<? extends CustomEnchant> clazz) {
		return enchants.get(clazz);
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
		if (!(event.getView().getPlayer() instanceof Player))
			return;

		ItemStack result = event.getItem();
		if (isNullOrAir(result))
			return;

		ItemStack updated = update(result);

		result.setItemMeta(updated.getItemMeta());
		Tasks.sync(() -> result.setItemMeta(updated.getItemMeta()));
	}

	@EventHandler
	public void onItemCraft(PrepareItemCraftEvent event) {
		if (!(event.getView().getPlayer() instanceof Player))
			return;

		ItemStack result = event.getInventory().getResult();
		if (isNullOrAir(result))
			return;

		ItemStack updated = update(result);

		event.getInventory().setResult(updated);
		Tasks.sync(() -> event.getInventory().setResult(updated));
	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		ItemStack result = event.getItem();
		if (isNullOrAir(result))
			return;

		ItemStack updated = update(result);

		event.getItem().setItemMeta(updated.getItemMeta());
	}

	// Includes Anvil, Grindstone, and Smithing Table
	@EventHandler
	public void onPrepareResult(PrepareResultEvent event) {
		if (!(event.getView().getPlayer() instanceof Player))
			return;

		ItemStack result = event.getResult();
		if (isNullOrAir(result))
			return;

		ItemStack updated = update(result);

		event.setResult(updated);
		Tasks.sync(() -> event.setResult(updated));
	}

	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent event) {
		if (!(event.getView().getPlayer() instanceof Player))
			return;

		final ItemStack result = event.getResult();
		final ItemStack firstItem = event.getInventory().getFirstItem();
		final ItemStack secondItem = event.getInventory().getSecondItem();

		if (isNullOrAir(result) || isNullOrAir(firstItem) || isNullOrAir(secondItem))
			return;

		for (CustomEnchant enchant : CustomEnchants.getEnchants()) {
			int firstLevel = 0;
			int secondLevel = 0;

			if (firstItem.getItemMeta().hasEnchant(enchant))
				firstLevel = firstItem.getEnchantmentLevel(enchant);

			if (secondItem.getItemMeta().hasEnchant(enchant))
				secondLevel = secondItem.getEnchantmentLevel(enchant);

			int finalLevel = Math.max(firstLevel, secondLevel);
			if (firstLevel == secondLevel)
				++finalLevel;

			result.addUnsafeEnchantment(enchant, finalLevel);
		}
	}

	public static ItemStack update(ItemStack itemStack) {
		ItemMeta meta = itemStack.getItemMeta();

		List<String> lore = new ArrayList<>();
		lore.addAll(getExistingLore(meta));
		lore.addAll(0, getEnchantLore(meta));

		meta.setLore(lore);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	private static List<String> getExistingLore(ItemMeta meta) {
		List<String> lore = new ArrayList<>();

		if (!Utils.isNullOrEmpty(meta.getLore()))
			lines: for (String line : meta.getLore()) {
				if (!isNullOrEmpty(line))
					for (CustomEnchant enchant : CustomEnchants.getEnchants())
						if (stripColor(line).matches("(?i)^" + enchant.getName() + " .*"))
							continue lines;

				lore.add(line);
			}

		return lore;
	}

	private static List<String> getEnchantLore(ItemMeta meta) {
		return new ArrayList<>() {{
			for (CustomEnchant enchant : CustomEnchants.getEnchants())
				if (meta.hasEnchant(enchant))
					add(0, colorize("&7" + enchant.getDisplayName(meta.getEnchantLevel(enchant))));
		}};
	}

}
