package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GearsEnchant extends CustomEnchant {

	private static final NamespacedKey ATTR_KEY = new NamespacedKey(Nexus.getInstance(), "GEARS_ENCHANT");

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public @NotNull EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR_FEET;
	}

	@SuppressWarnings("UnstableApiUsage")
	public static void updateAttributeModifier(ItemStack item) {
		if (!item.containsEnchantment(Enchant.GEARS))
			return;

		var meta = item.getItemMeta();
		var level = meta.getEnchantLevel(Enchant.GEARS);
		var amount = level * .02;
		var modifier = new AttributeModifier(ATTR_KEY, amount, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);

		var modifiers = meta.getAttributeModifiers(Attribute.MOVEMENT_SPEED);
		if (modifiers != null) {
			var match = modifiers.stream()
				.filter(existingModifier -> modifier.getOperation() == existingModifier.getOperation())
				.filter(existingModifier -> modifier.getAmount() == existingModifier.getAmount())
				.findAny();

			if (match.isPresent())
				return;
		}

		ItemUtils.explicitlySetDefaultAttributes(meta, item.getType());

		meta.removeAttributeModifier(Attribute.MOVEMENT_SPEED);
		meta.addAttributeModifier(Attribute.MOVEMENT_SPEED, modifier);

		item.setItemMeta(meta);
	}

}
