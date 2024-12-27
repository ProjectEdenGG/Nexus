package gg.projecteden.nexus.features.customenchants.models;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class NMSCustomEnchant extends Enchantment {
	private final CustomEnchant enchant;

	public NMSCustomEnchant(CustomEnchant enchant) {
		super(Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE, new EquipmentSlot[]{});
		this.enchant = enchant;
	}

	@Override
	public @NotNull Map<EquipmentSlot, ItemStack> getSlotItems(@NotNull LivingEntity entity) {
		return new HashMap<>();
	}

	@Override
	public int getMinLevel() {
		return 1;
	}

	@Override
	public int getMaxLevel() {
		return enchant.getMaxLevel();
	}

	@Override
	protected boolean checkCompatibility(@NotNull Enchantment other) {
		return enchant.conflictsWith(CraftEnchantment.minecraftToBukkit(other));
	}

	@Override
	public boolean canEnchant(@NotNull ItemStack stack) {
		return enchant.canEnchantItem(CraftItemStack.asCraftMirror(stack));
	}

	@Override
	public void doPostAttack(@NotNull LivingEntity user, @NotNull Entity target, int level) {
		// Do nothing
	}

	@Override
	public void doPostHurt(@NotNull LivingEntity user, @NotNull Entity attacker, int level) {
		// Do nothing
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@Override
	public boolean isCurse() {
		return false;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

}
