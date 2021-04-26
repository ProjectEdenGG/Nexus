package me.pugabyte.nexus.features.minigames.modifiers;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ModernCombat implements MinigameModifier {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.getPlayer().getInventory().forEach(itemStack -> {
			if (itemStack.hasItemMeta()) {
				ItemMeta meta = itemStack.getItemMeta();
				meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
				itemStack.setItemMeta(meta);
			}
		});
	}

	@Override
	public @NotNull String getName() {
		return "Modern Combat";
	}
}