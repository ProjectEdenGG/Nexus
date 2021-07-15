package me.pugabyte.nexus.features.ambience.effects.particles.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.ambience.effects.common.AmbientEffectConfig;
import me.pugabyte.nexus.features.ambience.effects.particles.DustWind;
import me.pugabyte.nexus.features.ambience.effects.particles.FallingLeaves;
import me.pugabyte.nexus.features.ambience.effects.particles.Fireflies;
import me.pugabyte.nexus.features.ambience.managers.ParticleEffectManager;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManagers;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Data
@AllArgsConstructor
public class ParticleEffectConfig implements AmbientEffectConfig<ParticleEffectType> {
	private final ParticleEffectType effectType;
	private final Material particleMaterial;
	private final Material spawnMaterial;
	private final Material aboveMaterial;
	private final Material belowMaterial;
	private double chance;

	public void update(AmbienceUser user, Block block) {
		if (!this.effectType.conditionsMet(user, this, block))
			return;

		// TODO Abstract?
		final ParticleEffectManager manager = AmbienceManagers.PARTICLES.get();
		switch (this.effectType) {
			case FIREFLIES -> manager.addInstance(user, new Fireflies(user, block, chance));
			case DUST_WIND -> manager.addInstance(user, new DustWind(user, block, chance));
			case FALLING_LEAVES -> manager.addInstance(user, new FallingLeaves(user, block, chance));
		}
	}

}
