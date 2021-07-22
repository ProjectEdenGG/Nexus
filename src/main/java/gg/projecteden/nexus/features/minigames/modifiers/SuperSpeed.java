package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class SuperSpeed implements MinigameModifier {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, true, false, true));
	}

	@Override
	public @NotNull String getName() {
		return "Super Speed";
	}

	@Override
	public @NotNull String getDescription() {
		return "Gives everyone super speed";
	}
}
