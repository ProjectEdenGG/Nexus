package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NoKnockback implements MinigameModifier {
	private static final UUID DEFENSE = UUID.fromString("38628a37-5c1c-4fa7-a003-66ff7883b494");
	private static final UUID TOUGHNESS = UUID.fromString("20f0437a-8fb1-4cfe-8885-7833f1371999");
	private static final UUID KNOCKBACK_A = UUID.fromString("aa13deba-c837-46fc-94de-c122178a11c1");
	private static final UUID KNOCKBACK_B = UUID.fromString("aa13deba-c837-46fc-94de-c122178a11c2");

	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.getOnlinePlayer().getInventory().forEach(itemStack -> {
			if (itemStack == null) return;
			ItemMeta meta = itemStack.getItemMeta();
			EquipmentSlot slot = ItemUtils.getArmorEquipmentSlot(itemStack.getType());
			if (slot != null) {
				int defense = ItemUtils.getDefensePoints(itemStack.getType());
				int toughness = ItemUtils.getArmorToughness(itemStack.getType());
				if (defense > 0)
					meta.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(DEFENSE, "minigame_fix_armor", defense, AttributeModifier.Operation.ADD_NUMBER, slot));
				if (toughness > 0)
					meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(TOUGHNESS, "minigame_fix_armor_toughness", toughness, AttributeModifier.Operation.ADD_NUMBER, slot));

				meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK_A, "minigame_noknockback", 10, AttributeModifier.Operation.ADD_NUMBER, slot));
			} else {
				meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK_A, "minigame_noknockback", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
				meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK_B, "minigame_noknockback_b", 10, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
			}

			itemStack.setItemMeta(meta);
		});
	}

	@Override
	public @NotNull String getName() {
		return "No Knockback";
	}

	@Override
	public @NotNull String getDescription() {
		return "Removes knockback";
	}
}
