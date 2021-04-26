package me.pugabyte.nexus.features.minigames.modifiers;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class MoonGravity extends BulletArrows {
	@Override
	public void afterLoadout(@NotNull Minigamer minigamer) {
		Player player = minigamer.getPlayer();
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 1, true, false, true));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1000000, 0, true, false, true));
	}

	@Override
	public @NotNull String getName() {
		return "Moon Gravity";
	}
}
