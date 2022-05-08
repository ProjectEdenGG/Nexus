package gg.projecteden.nexus.features.ambience.effects.particles.common;

import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Particle;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ParticleEffect {
	protected AmbienceUser user;
	protected ParticleEffectType effectType;
	protected Particle particle;
	protected long life;
	protected double chance;

	public void tick() {
		this.life--;

		if (RandomUtils.chanceOf(this.chance))
			this.play();
	}

	public boolean isAlive() {
		return this.life > 0;
	}

	public abstract void play();

}
