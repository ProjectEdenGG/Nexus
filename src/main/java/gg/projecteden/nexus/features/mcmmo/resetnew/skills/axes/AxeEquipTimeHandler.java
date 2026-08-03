package gg.projecteden.nexus.features.mcmmo.resetnew.skills.axes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.resetnew.ItemAttributeHandler;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

public class AxeEquipTimeHandler extends ItemAttributeHandler {

	public static final String PERMISSION = "nexus.axesequiptime";

	private static final AttributeModifier attributeModifier = new AttributeModifier(
		new NamespacedKey(Nexus.getInstance(), "axes_attackspeed"),
		0.5,
		AttributeModifier.Operation.ADD_NUMBER,
		EquipmentSlotGroup.MAINHAND
	);

	@Override
	public MaterialTag getApplicableItems() {
		return MaterialTag.AXES;
	}

	@Override
	public String getPermission() {
		return PERMISSION;
	}

	@Override
	public void apply(Player player) {
		AttributeInstance instance = player.getAttribute(Attribute.ATTACK_SPEED);
		if (instance == null) return;

		instance.removeModifier(attributeModifier.getKey());
		instance.addTransientModifier(attributeModifier);
	}

	@Override
	public void remove(Player player) {
		AttributeInstance instance = player.getAttribute(Attribute.ATTACK_SPEED);
		if (instance == null) return;
		instance.removeModifier(attributeModifier.getKey());
	}

}
