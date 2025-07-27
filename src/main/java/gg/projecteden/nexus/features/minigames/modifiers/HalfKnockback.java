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

@SuppressWarnings("removal")
public class HalfKnockback implements MinigameModifier {
	private static final UUID KNOCKBACK = UUID.fromString("aa13deba-c837-46fc-94de-c122178a11c3");

	@Override
	public @NotNull String getName() {
		return "Half Knockback";
	}

	@Override
	public @NotNull String getDescription() {
		return "Reduces knockback by one half";
	}

	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.getOnlinePlayer().getInventory().forEach(itemStack -> {
			if (itemStack == null) return;
			ItemMeta meta = itemStack.getItemMeta();

			if (ItemUtils.getDefensePoints(itemStack.getType()) > 0) return;

			meta.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE, new AttributeModifier(KNOCKBACK, "minigame_halfknockback", 0.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
			itemStack.setItemMeta(meta);
		});
	}

}
