package gg.projecteden.nexus.utils;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Data
public class PotionEffectEditor {
	@NonNull
	PotionEffect effect;

	private PotionEffectType type;
	private int duration;
	private int amplifier;
	private boolean ambient;
	private boolean particles;

	public PotionEffectEditor(PotionEffect effect) {
		this.effect = effect;
		type = effect.getType();
		duration = effect.getDuration();
		amplifier = effect.getAmplifier();
		ambient = effect.isAmbient();
		particles = effect.hasParticles();
	}

	public PotionEffect withType(PotionEffectType type) {
		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

	public PotionEffect withDuration(int duration) {
		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

	public PotionEffect withAmplifier(int amplifier) {
		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

	public PotionEffect withAmbient(boolean ambient) {
		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

	public PotionEffect withParticles(boolean particles) {
		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

}
