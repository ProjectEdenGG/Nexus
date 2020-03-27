package me.pugabyte.bncore.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class ItemBuilder {
	private ItemStack itemStack;
	private ItemMeta itemMeta;
	private List<String> lore = new ArrayList<>();
	private boolean doLoreize = true;

	public ItemBuilder(Material material) {
		this(new ItemStack(material));
	}

	public ItemBuilder(ItemStack itemStack) {
		this.itemStack = itemStack.clone();
		this.itemMeta = itemStack.getItemMeta();
	}

	public ItemBuilder amount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public ItemBuilder color(ColorType colorType) {
		return durability(colorType.getDurability().shortValue());
	}

	public ItemBuilder durability(int durability) {
		return durability(Integer.valueOf(durability).shortValue());
	}

	public ItemBuilder durability(short durability) {
		itemStack.setDurability(durability);
		return this;
	}

	public ItemBuilder name(String displayName) {
		itemMeta.setDisplayName(colorize(displayName));
		return this;
	}

	public ItemBuilder lore(String... lore) {
		return lore(Arrays.asList(lore));
	}

	public ItemBuilder lore(List<String> lore) {
		this.lore.addAll(lore);
		return this;
	}

	public ItemBuilder loreize(boolean doLoreize) {
		this.doLoreize = doLoreize;
		return this;
	}

	public ItemBuilder enchant(Enchantment enchantment) {
		return enchant(enchantment, 1);
	}

	public ItemBuilder enchant(Enchantment enchantment, int level) {
		return enchant(enchantment, level, true);
	}

	public ItemBuilder enchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
		if (itemStack.getType() == Material.ENCHANTED_BOOK) {
			EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemMeta;
			bookMeta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
			itemMeta = bookMeta;
		} else {
			itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
		}

		return this;
	}

	public ItemBuilder glow() {
		enchant(Enchantment.ARROW_INFINITE);
		itemFlags(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	public ItemBuilder effect(PotionEffectType potionEffectType) {
		return effect(potionEffectType, 1, 1);
	}

	public ItemBuilder effect(PotionEffectType potionEffectType, int seconds) {
		return effect(potionEffectType, seconds, 1);
	}

	public ItemBuilder effect(PotionEffectType potionEffectType, int seconds, int amplifier) {
		return effect(new PotionEffect(potionEffectType, seconds * 20, amplifier - 1));
	}

	public ItemBuilder effect(PotionEffect potionEffect) {
		PotionMeta potionMeta = (PotionMeta) itemMeta;
		potionMeta.addCustomEffect(potionEffect, true);
		itemMeta = potionMeta;
		return this;
	}

	public ItemBuilder effectColor(Color color) {
		PotionMeta potionMeta = (PotionMeta) itemMeta;
		potionMeta.setColor(color);
		itemMeta = potionMeta;
		return this;
	}

	public ItemBuilder power(int power) {
		FireworkMeta fireworkMeta = (FireworkMeta) itemMeta;
		fireworkMeta.setPower(power);
		itemMeta = fireworkMeta;
		return this;
	}

	public ItemBuilder itemFlags(ItemFlag... flags) {
		itemMeta.addItemFlags(flags);
		return this;
	}

	public ItemBuilder spawnEgg(EntityType entityType) {
		((SpawnEggMeta) itemMeta).setSpawnedType(entityType);
		return this;
	}

	public ItemStack build() {
		ItemStack clonedStack = itemStack.clone();
		buildLore();
		clonedStack.setItemMeta(itemMeta.clone());
		return clonedStack;
	}

	public void buildLore() {
		List<String> colorized = new ArrayList<>();
		for (String line : lore)
			if (doLoreize)
				colorized.addAll(Arrays.asList(StringUtils.loreize(colorize(line)).split("\\|\\|")));
			else
				colorized.addAll(Arrays.asList(colorize(line).split("\\|\\|")));
		itemMeta.setLore(colorized);
	}

	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta itemMeta = item.getItemMeta();
		if (name == null)
			itemMeta.setDisplayName(null);
		else
			itemMeta.setDisplayName(colorize("&f" + name));
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack addItemFlags(ItemStack item, ItemFlag... flags) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addItemFlags(flags);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack addLore(ItemStack item, String... lore) {
		return addLore(item, Arrays.asList(lore));
	}

	public static ItemStack addLore(ItemStack item, List<String> lore) {
		lore = lore.stream().map(StringUtils::colorize).collect(Collectors.toList());
		ItemMeta itemMeta = item.getItemMeta();
		List<String> existing = itemMeta.getLore();
		if (existing == null) existing = new ArrayList<>();
		existing.addAll(lore);
		itemMeta.setLore(existing);
		item.setItemMeta(itemMeta);
		return item;
	}

}
