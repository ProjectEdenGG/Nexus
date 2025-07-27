package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Blindness implements MinigameModifier {

	@Override
	public @NotNull String getName() {
		return "Blindness";
	}

	@Override
	public @NotNull String getDescription() {
		return "Gives everyone blindness";
	}

	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS).infinite().ambient(true).icon(true));
	}

}
