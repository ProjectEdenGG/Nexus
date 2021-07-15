package me.pugabyte.nexus.features.ambience.effects.particles.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Particle;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ParticleEffect {
	protected AmbienceUser user;
	protected ParticleEffectType effectType;
	protected Particle particle;
	protected int life;
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
