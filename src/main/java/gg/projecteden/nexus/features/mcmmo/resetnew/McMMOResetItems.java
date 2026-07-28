package gg.projecteden.nexus.features.mcmmo.resetnew;

import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.ItemStack;

public class McMMOResetItems {

	// Recipe registration at CustomRecipes#smithingRecipes
	public static final ItemStack ELYTRA_TEMPLATE = new ItemBuilder(Material.PAPER).maxStackSize(1)
		.model("survival/mcmmoreset/skills/acrobatics/elytra_template")
		.name("&eElytra Template")
		.build();

	public static final ItemStack SLIME_BOOTS = ArmorSkin.SLIME.apply(new ItemBuilder(Material.DIAMOND_BOOTS)
		.name("&aSlime Boots")
		.attribute(Attribute.BOUNCINESS, new AttributeModifier(NamespacedKey.minecraft("slime_boots_bounce"), 2, Operation.ADD_NUMBER))
		.attribute(Attribute.AIR_DRAG_MODIFIER, new AttributeModifier(NamespacedKey.minecraft("slime_boots_air_drag"), -1.5, Operation.MULTIPLY_SCALAR_1))
		.attribute(Attribute.FRICTION_MODIFIER, new AttributeModifier(NamespacedKey.minecraft("slime_boots_friction"), -1.5, Operation.MULTIPLY_SCALAR_1)))
		.build();

}
