package me.pugabyte.nexus.features.minigames.models.modifiers;

import eden.interfaces.Named;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.framework.interfaces.HasDescription;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

public interface MinigameModifier extends Named, HasDescription, ComponentLike {
	default void afterLoadout(@NotNull Minigamer minigamer) {};
	default void onProjectileSpawn(@NotNull Projectile projectile) {};

	default @NotNull TextComponent asComponent() {
		return Component.text("Modifier: ", NamedTextColor.DARK_AQUA).append(Component.text(getName(), NamedTextColor.YELLOW));
	}
}
