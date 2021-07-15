package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.ambience.effects.sounds.BirdSound;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.entity.Player;

import java.util.Random;

@Data
@AllArgsConstructor
public class SoundEffectConfig {
	private static final Random RANDOM = new Random();
	//
	private SoundEffectType effectType;
	private Sound sound = null;
	private BirdSound birdSound = null;
	private int cooldownMin;
	private int cooldownMax;
	private String cooldownId;

	public SoundEffectConfig(SoundEffectType effectType, Sound sound, int cooldownMin, int cooldownMax) {
		this.effectType = effectType;
		this.sound = sound;
		this.cooldownMin = cooldownMin;
		this.cooldownMax = cooldownMax;
		this.cooldownId = effectType.name().toLowerCase();

		if (cooldownMin < 0) throw new IllegalArgumentException("cooldown minimum cannot be negative");
		if (cooldownMax < 0) throw new IllegalArgumentException("cooldown maximum cannot be negative");
		if (cooldownMax < cooldownMin)
			throw new IllegalArgumentException("cooldown min cannot be larger than cooldown max");
	}

	public SoundEffectConfig(SoundEffectType effectType, BirdSound birdSound, int cooldownMin, int cooldownMax) {
		this.effectType = effectType;
		this.birdSound = birdSound;
		this.cooldownMin = cooldownMin;
		this.cooldownMax = cooldownMax;
		this.cooldownId = effectType.name().toLowerCase();

		if (cooldownMin < 0) throw new IllegalArgumentException("cooldown minimum cannot be negative");
		if (cooldownMax < 0) throw new IllegalArgumentException("cooldown maximum cannot be negative");
		if (cooldownMax < cooldownMin)
			throw new IllegalArgumentException("cooldown min cannot be larger than cooldown max");
	}

	public void init(AmbienceUser user) {
		setCooldown(user);
	}

	public void update(AmbienceUser user) {
		Player player = user.getPlayer();
		if (player == null)
			return;

		if (!effectType.conditionsMet(this, user))
			return;

		if (user.updateCooldown(cooldownId) <= 0) {
			if (sound != null)
				user.getSoundPlayer().playSound(sound, player.getLocation());
			else if (birdSound != null)
				birdSound.play(player, player.getLocation());

			setCooldown(user);
		}
	}

	private void setCooldown(AmbienceUser user) {
		user.setCooldown(cooldownId, cooldownMin + RANDOM.nextInt(cooldownMax - cooldownMin + 1));
	}

}
