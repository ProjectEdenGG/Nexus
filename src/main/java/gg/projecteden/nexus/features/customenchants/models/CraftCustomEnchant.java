package gg.projecteden.nexus.features.customenchants.models;

import net.kyori.adventure.text.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftCustomEnchant extends CraftEnchantment {
	private final CustomEnchant enchant;

	public CraftCustomEnchant(CustomEnchant customEnchant, Enchantment handle) {
		super(customEnchant.getKey(), handle);
		this.enchant = customEnchant;
	}

	@Override
	public int getMaxLevel() {
		return enchant.getMaxLevel();
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return enchant.canEnchantItem(item);
	}

	@Override
	public @NotNull String getName() {
		return enchant.getId().toUpperCase();
	}

	@Override
	public boolean conflictsWith(org.bukkit.enchantments.Enchantment other) {
		return enchant.conflictsWith(other);
	}

	@Override
	public Component displayName(int level) {
		return enchant.displayName(level);
	}

	@Override
	public @NotNull String translationKey() {
		return enchant.translationKey();
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

	@Override
	public int getMinModifiedCost(int level) {
		return enchant.getMinModifiedCost(level);
	}

	@Override
	public int getMaxModifiedCost(int level) {
		return enchant.getMaxModifiedCost(level);
	}


	@Override
	public float getDamageIncrease(int level, EntityCategory entityCategory) {
		return 0f;
	}

}
