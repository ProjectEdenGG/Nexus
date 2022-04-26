package gg.projecteden.nexus.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Data
@NoArgsConstructor
public class PotionEffectBuilder implements Cloneable {
	private PotionEffectType type;
	private int amplifier = 1;
	private int duration;
	private boolean ambient = false;
	private boolean particles = false;
	private boolean icon = false;

	public PotionEffectBuilder(PotionEffectType type){
		this.type = type;
	}

	public PotionEffectBuilder(PotionEffect potionEffect) {
		this.type = potionEffect.getType();
		this.amplifier = potionEffect.getAmplifier();
		this.duration = potionEffect.getDuration();
		this.ambient = potionEffect.isAmbient();
		this.particles = potionEffect.hasParticles();
		this.icon = potionEffect.hasIcon();
	}

	public PotionEffectBuilder type(PotionEffectType type){
		this.type = type;
		return this;
	}

	public PotionEffectBuilder amplifier(int amplifier){
		this.amplifier = amplifier;
		return this;
	}

	public PotionEffectBuilder maxAmplifier(){
		this.amplifier = 255;
		return this;
	}

	public PotionEffectBuilder duration(long duration){
		return duration((int) duration);
	}

	public PotionEffectBuilder duration(int duration){
		this.duration = duration;
		return this;
	}

	public PotionEffectBuilder maxDuration(){
		this.duration = 9999999;
		return this;
	}

	public PotionEffectBuilder ambient(boolean ambient){
		this.ambient = ambient;
		return this;
	}

	public PotionEffectBuilder particles(boolean particles){
		this.particles = particles;
		return this;
	}

	public PotionEffectBuilder icon(boolean icon){
		this.icon = icon;
		return this;
	}

	public PotionEffectBuilder clone() {
		return new PotionEffectBuilder(type)
			.amplifier(amplifier)
			.duration(duration)
			.ambient(ambient)
			.particles(particles)
			.icon(icon);
	}

	public PotionEffect build(){
		return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
	}
}
