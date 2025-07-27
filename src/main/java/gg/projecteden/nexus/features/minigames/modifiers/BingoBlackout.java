package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.jetbrains.annotations.NotNull;

public class BingoBlackout implements MinigameModifier {

	@Override
	public @NotNull String getName() {
		return "Bingo Blackout";
	}

	@Override
	public @NotNull String getDescription() {
		return "Time limit removed, first to complete entire board wins";
	}

}
