package me.pugabyte.nexus.features.ambience;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.ambience.ParticleEffects.AmbienceEffect;
import me.pugabyte.nexus.features.ambience.particleeffects.DustWind;
import me.pugabyte.nexus.features.ambience.particleeffects.FallingLeaves;
import me.pugabyte.nexus.features.ambience.particleeffects.Fireflies;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Data
@AllArgsConstructor
public class ParticleEffect {
	private final AmbienceEffect effect;
	private final Material particleMaterial;
	private final Material spawnMaterial;
	private final Material aboveMaterial;
	private final Material belowMaterial;
	private double chance;

	public void update(AmbienceUser user, Block block, double x, double y, double z) {
		switch (this.effect) {
			case FIREFLIES -> {
				if (Fireflies.conditionsMet(this, user, block, x, y, z)) {
					ParticleEffects.addInstance(user, new Fireflies(user, x, y, z, 5, this.chance));
				}
			}

			case DUST_WIND -> {
				if (DustWind.conditionsMet(this, user, block, x, y, z)) {
					ParticleEffects.addInstance(user, new DustWind(user, block.getType(), Ambience.getWIND_X(), Ambience.getWIND_Z(), x, y, z, chance));
				}
			}

			case FALLING_LEAVES -> {
				if (FallingLeaves.conditionsMet(this, user, block, x, y, z)) {
					ParticleEffects.addInstance(user, new FallingLeaves(user, block.getType(), x, y, z, chance));
				}
			}
		}
	}
}
