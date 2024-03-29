package gg.projecteden.nexus.features.customenchants.models;

import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.customenchants.EnchantUtils;
import gg.projecteden.nexus.utils.Nullables;
import io.papermc.paper.enchantments.EnchantmentRarity;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.toRoman;

@NoArgsConstructor
public abstract class CustomEnchant extends Enchantment implements Translatable {

	@Override
	public @NotNull Key key() {
		return super.key();
	}

	@Override
	public @NotNull NamespacedKey getKey() {
		return NamespacedKey.minecraft(getId());
	}

	@Override
	public @NotNull String translationKey() {
		// custom enchants obviously can't be translated so this is a basic response
		// (actually they could be using our resource pack but it probably wouldn't be worth the effort)
		return getKey().asString();
	}

	public String getId() {
		return CustomEnchants.getId(getClass());
	}

	public NamespacedKey nmsKey() {
		return NamespacedKey.minecraft(getId());
	}

	@Override
	public @NotNull String getName() {
		return getKey().getKey();
	}

	@Override
	public @NotNull Component displayName(int level) {
		return Component.text(getDisplayName(level));
	}

	@NotNull
	public String getDisplayName(int level) {
		return camelCase(getName()) + (level > 1 ? " " + toRoman(level) : "");
	}

	public int getLevel(ItemStack item) {
		return EnchantUtils.getLevel(this, item);
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public int getMaxLevel() {
		return 255;
	}

	@Override
	public int getMinModifiedCost(int level) {
		return 0;
	}

	@Override
	public int getMaxModifiedCost(int level) {
		return 0;
	}

	@Override
	public boolean conflictsWith(@NotNull Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean canEnchantItem(@NotNull ItemStack itemStack) {
		return getItemTarget().includes(itemStack);
	}

	@Override
	public @NotNull EnchantmentRarity getRarity() {
		return EnchantmentRarity.VERY_RARE;
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ALL;
	}

	@Override
	public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
		return 0;
	}

	@Override
	public @NotNull Set<EquipmentSlot> getActiveSlots() {
		return Set.of(EquipmentSlot.values());
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
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@NotNull
	public static List<ItemStack> getItems(PlayerInventory inventory) {
		List<ItemStack> items = new ArrayList<>() {{
			addAll(Arrays.asList(inventory.getArmorContents()));
			add(inventory.getItemInMainHand());
			add(inventory.getItemInOffHand());
		}};

		items.removeIf(Nullables::isNullOrAir);
		return items;
	}

}
