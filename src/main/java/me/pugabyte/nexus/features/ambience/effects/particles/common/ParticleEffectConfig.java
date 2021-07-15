package me.pugabyte.nexus.features.ambience.effects.particles.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.ambience.effects.particles.DustWind;
import me.pugabyte.nexus.features.ambience.effects.particles.FallingLeaves;
import me.pugabyte.nexus.features.ambience.effects.particles.Fireflies;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManager;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManagers;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Material;
import org.bukkit.block.Block;

@Data
@AllArgsConstructor
public class ParticleEffectConfig {
	private final ParticleEffectType effect;
	private final Material particleMaterial;
	private final Material spawnMaterial;
	private final Material aboveMaterial;
	private final Material belowMaterial;
	private double chance;

	public void update(AmbienceUser user, Block block, int x, int y, int z) {
		if (!this.effect.conditionsMet(this, user, block, x, y, z))
			return;

		// TODO Abstract?
		final AmbienceManager<ParticleEffect> manager = AmbienceManagers.PARTICLE_EFFECTS.get();
		switch (this.effect) {
			case FIREFLIES -> manager.addInstance(user, new Fireflies(user, x, y, z, chance));
			case DUST_WIND -> manager.addInstance(user, new DustWind(user, block.getType(), x, y, z, chance));
			case FALLING_LEAVES -> manager.addInstance(user, new FallingLeaves(user, block.getType(), x, y, z, chance));
		}
	}
}
