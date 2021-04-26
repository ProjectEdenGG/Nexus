package me.pugabyte.nexus.features.minigames.modifiers;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HalfKnockback implements MinigameModifier {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.getPlayer().getInventory().forEach(itemStack -> {
			if (itemStack == null) return;
			ItemMeta meta = itemStack.getItemMeta();
			meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "minigame_halfknockback", 0.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
			itemStack.setItemMeta(meta);
		});
	}

	@Override
	public @NotNull String getName() {
		return "Half Knockback";
	}
}
