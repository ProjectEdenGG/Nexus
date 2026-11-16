package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpringsEnchant extends CustomEnchant {

	private static final NamespacedKey ATTR_KEY = new NamespacedKey(Nexus.getInstance(), "SPRINGS_ENCHANT");

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return MaterialTag.ALL_BOOTS.getValues().stream().toList();
	}

	@SuppressWarnings("UnstableApiUsage")
	public static void updateAttributeModifier(ItemStack item) {
		if (!item.containsEnchantment(Enchant.SPRINGS))
			return;

		var meta = item.getItemMeta();
		var level = meta.getEnchantLevel(Enchant.SPRINGS);
		var amount = .2 + (level * .1);
		var modifier = new AttributeModifier(ATTR_KEY, amount, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);

		var modifiers = meta.getAttributeModifiers(Attribute.JUMP_STRENGTH);
		if (modifiers != null) {
			var match = modifiers.stream()
				.filter(existingModifier -> modifier.getOperation() == existingModifier.getOperation())
				.filter(existingModifier -> modifier.getAmount() == existingModifier.getAmount())
				.findAny();

			if (match.isPresent())
				return;
		}

		ItemUtils.explicitlySetDefaultAttributes(meta, item.getType());

		meta.removeAttributeModifier(Attribute.JUMP_STRENGTH);
		meta.addAttributeModifier(Attribute.JUMP_STRENGTH, modifier);

		item.setItemMeta(meta);
	}

}
