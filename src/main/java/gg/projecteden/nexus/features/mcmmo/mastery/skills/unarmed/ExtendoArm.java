package gg.projecteden.nexus.features.mcmmo.mastery.skills.unarmed;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

public class ExtendoArm {

	public static final ItemStack ITEM = new ItemBuilder(ItemModelType.EXTENDO_ARM)
		.maxStackSize(1)
		.name("&eExtendo-Arm")
		.lore("&3Doubles your block reach while in your offhand")
		.attribute(Attribute.BLOCK_INTERACTION_RANGE, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "extendo_arm"), 1.0, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.OFFHAND))
		.build();

}
