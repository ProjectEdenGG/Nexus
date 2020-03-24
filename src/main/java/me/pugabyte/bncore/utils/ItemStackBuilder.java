package me.pugabyte.bncore.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class ItemStackBuilder {
	private ItemStack itemStack;
	private ItemMeta itemMeta;
	private boolean doLoreize = true;

	public ItemStackBuilder(Material material) {
		this(new ItemStack(material));
	}

	public ItemStackBuilder(ItemStack itemStack) {
		this.itemStack = itemStack.clone();
		this.itemMeta = itemStack.getItemMeta();
	}

	public ItemStackBuilder amount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public ItemStackBuilder color(ColorType colorType) {
		return durability(colorType.getDurability().shortValue());
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
			if (doLoreize)
				colorized.addAll(Arrays.asList(StringUtils.loreize(colorize(line)).split("\\|\\|")));
			else
				colorized.addAll(Arrays.asList(colorize(line).split("\\|\\|")));
		itemMeta.setLore(colorized);
		return this;
	}

	public ItemStackBuilder loreize(boolean doLoreize) {
		this.doLoreize = doLoreize;
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
		enchant(Enchantment.ARROW_INFINITE);
		itemFlags(ItemFlag.HIDE_ENCHANTS);
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

	public ItemStackBuilder effectColor(Color color){
		PotionMeta potionMeta = (PotionMeta) itemMeta;
		potionMeta.setColor(color);
		itemMeta = potionMeta;
		return this;
	}

	public ItemStackBuilder itemFlags(ItemFlag... flags) {
		itemMeta.addItemFlags(flags);
		return this;
	}

	public ItemStackBuilder spawnEgg(EntityType entityType) {
		((SpawnEggMeta) itemMeta).setSpawnedType(entityType);
		return this;
	}

	public ItemStack build() {
		ItemStack clonedStack = itemStack.clone();
		clonedStack.setItemMeta(itemMeta.clone());
		return clonedStack;
	}

}
