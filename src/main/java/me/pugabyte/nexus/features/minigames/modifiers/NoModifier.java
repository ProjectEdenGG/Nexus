package me.pugabyte.nexus.features.minigames.modifiers;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.jetbrains.annotations.NotNull;

public class NoModifier implements MinigameModifier {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {}

	@Override
	public @NotNull String getName() {
		return "No Modifier";
	}

	@Override
	public @NotNull String getDescription() {
		return "Does nothing";
	}
}
