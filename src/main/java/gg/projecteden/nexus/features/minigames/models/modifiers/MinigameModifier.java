package gg.projecteden.nexus.features.minigames.models.modifiers;

import gg.projecteden.api.interfaces.Named;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.framework.interfaces.HasDescription;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public interface MinigameModifier extends Named, HasDescription, ComponentLike {

	default void afterLoadout(@NotNull Minigamer minigamer) {}

	default boolean appliesTo(@NotNull Mechanic mechanic) {
		return MinigameModifiers.of(this).appliesTo(mechanic);
	}

	default @NotNull TextComponent asComponent() {
		return Component.text("Modifier: ", NamedTextColor.DARK_AQUA).append(Component.text(getName(), NamedTextColor.YELLOW));
	}

}
