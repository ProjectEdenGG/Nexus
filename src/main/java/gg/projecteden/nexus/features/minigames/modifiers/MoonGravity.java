package gg.projecteden.nexus.features.minigames.modifiers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class MoonGravity extends BulletArrows {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.JUMP_BOOST).infinite().ambient(true).icon(true));
		minigamer.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SLOW_FALLING).infinite().amplifier(0).ambient(true).icon(true));
	}

	@Override
	public @NotNull String getName() {
		return "Moon Gravity";
	}

	@Override
	public @NotNull String getDescription() {
		return "Players fall slower and projectiles never fall";
	}
}
