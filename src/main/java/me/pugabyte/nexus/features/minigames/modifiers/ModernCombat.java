package me.pugabyte.nexus.features.minigames.modifiers;

import com.google.common.collect.ImmutableListMultimap;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ModernCombat implements MinigameModifier {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.getPlayer().getInventory().forEach(itemStack -> {
			if (itemStack == null) return;
			if (!itemStack.hasItemMeta()) return;
			ItemMeta meta = itemStack.getItemMeta();
			if (meta.getAttributeModifiers() == null) return;
			ImmutableListMultimap.copyOf(meta.getAttributeModifiers()).forEach(meta::removeAttributeModifier);
			itemStack.setItemMeta(meta);
		});
	}

	@Override
	public @NotNull String getName() {
		return "Modern Combat";
	}

	@Override
	public @NotNull String getDescription() {
		return "Disables spam-clicking weapons";
	}
}