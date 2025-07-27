package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class JumpBoost implements MinigameModifier {

	@Override
	public @NotNull String getName() {
		return "Jump Boost";
	}

	@Override
	public @NotNull String getDescription() {
		return "Gives everyone jump boost";
	}

	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.JUMP_BOOST).infinite().amplifier(2).ambient(true).icon(true));
	}

}
