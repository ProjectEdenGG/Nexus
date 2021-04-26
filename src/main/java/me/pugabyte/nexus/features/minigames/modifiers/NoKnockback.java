package me.pugabyte.nexus.features.minigames.modifiers;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class NoKnockback implements MinigameModifier {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.getPlayer().getInventory().forEach(itemStack -> {
			ItemMeta meta = itemStack.getItemMeta();
			meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier("minigame_noknockback", 10, AttributeModifier.Operation.ADD_NUMBER));
		});
	}

	@Override
	public @NotNull String getName() {
		return "No Knockback";
	}
}
