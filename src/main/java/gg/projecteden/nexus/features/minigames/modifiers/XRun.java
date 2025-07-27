package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class XRun implements MinigameModifier {

	@Override
	public @NotNull String getName() {
		return "X-Run";
	}

	@Override
	public @NotNull String getDescription() {
		return "Gives everyone X-Run-like speed";
	}

	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SPEED).infinite().amplifier(15).ambient(true).icon(true));
	}

}
