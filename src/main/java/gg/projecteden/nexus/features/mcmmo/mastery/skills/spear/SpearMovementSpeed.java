package gg.projecteden.nexus.features.mcmmo.mastery.skills.spear;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mcmmo.mastery.models.ItemAttributeHandler;
import gg.projecteden.nexus.utils.MaterialTag;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;

public class SpearMovementSpeed extends ItemAttributeHandler {

	public static final String PERMISSION = "nexus.mcmmo.spears.movement_speed";
	private static final NamespacedKey KEY = new  NamespacedKey(Nexus.getInstance(), "spear_speed");
	private static final AttributeModifier MODIFIER = new AttributeModifier(KEY, .3, Operation.ADD_NUMBER);

	@Override
	public MaterialTag getApplicableItems() {
		return MaterialTag.SPEARS;
	}

	@Override
	public String getPermission() {
		return PERMISSION;
	}

	@Override
	public void apply(Player player) {
		for (AttributeModifier modifier : player.getAttribute(Attribute.MOVEMENT_SPEED).getModifiers()) {
			if (modifier.getKey().equals(KEY))
				return;
		}
		player.getAttribute(Attribute.MOVEMENT_SPEED).addModifier(MODIFIER);
	}

	@Override
	public void remove(Player player) {
		player.getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(KEY);
	}
}
