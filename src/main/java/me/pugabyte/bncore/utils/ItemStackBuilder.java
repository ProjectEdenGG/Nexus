package me.pugabyte.bncore.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class ItemStackBuilder {
	private ItemStack itemStack;
	private ItemMeta itemMeta;

	public ItemStackBuilder(Material material) {
		itemStack = new ItemStack(material);
		itemMeta = itemStack.getItemMeta();
	}

	public ItemStackBuilder amount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public ItemStackBuilder durability(short durability) {
		itemStack.setDurability(durability);
		return this;
	}

	public ItemStackBuilder name(String displayName) {
		itemMeta.setDisplayName(colorize(displayName));
		return this;
	}

	public ItemStackBuilder lore(String... lore) {
		return lore(Arrays.asList(lore));
	}

	public ItemStackBuilder lore(List<String> lore) {
		List<String> colorized = new ArrayList<>();
		for (String line : lore)
			colorized.add(colorize(line));
		itemMeta.setLore(colorized);
		return this;
	}

	public ItemStackBuilder enchant(Enchantment enchantment) {
		return enchant(enchantment, 1);
	}

	public ItemStackBuilder enchant(Enchantment enchantment, int level) {
		return enchant(enchantment, level, true);
	}

	public ItemStackBuilder enchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
		if (itemStack.getType() == Material.ENCHANTED_BOOK) {
			EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemMeta;
			bookMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
			itemMeta = bookMeta;
		} else {
			itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
		}

		return this;
	}

	public ItemStackBuilder glow() {
		Utils.addGlowing(itemStack);
		return this;
	}

	public ItemStackBuilder effect(PotionEffectType potionEffectType) {
		return effect(potionEffectType, 1, 1);
	}

	public ItemStackBuilder effect(PotionEffectType potionEffectType, int seconds) {
		return effect(potionEffectType, seconds, 1);
	}

	public ItemStackBuilder effect(PotionEffectType potionEffectType, int seconds, int amplifier) {
		return effect(new PotionEffect(potionEffectType, seconds * 20, amplifier - 1));
	}

	public ItemStackBuilder effect(PotionEffect potionEffect) {
		PotionMeta potionMeta = (PotionMeta) itemMeta;
		potionMeta.addCustomEffect(potionEffect, true);
		itemMeta = potionMeta;
		return this;
	}

	public ItemStackBuilder itemFlags(ItemFlag... flags) {
		itemMeta.addItemFlags(flags);
		return this;
	}

	public ItemStack build() {
		ItemStack clonedStack = itemStack.clone();
		clonedStack.setItemMeta(itemMeta.clone());
		return clonedStack;
	}

}
