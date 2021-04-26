package me.pugabyte.nexus.features.minigames.modifiers;

import me.pugabyte.nexus.features.minigames.models.modifiers.MinigameModifier;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

public class BulletArrows implements MinigameModifier {
	@Override
	public void onProjectileSpawn(@NotNull Projectile projectile) {
		projectile.setGravity(false);
	}

	@Override
	public @NotNull String getName() {
		return "Bullet Arrows";
	}
}
