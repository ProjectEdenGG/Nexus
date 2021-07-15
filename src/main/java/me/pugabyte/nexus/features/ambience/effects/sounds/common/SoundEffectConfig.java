package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.ambience.effects.common.AmbientEffectConfig;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoundEffectConfig implements AmbientEffectConfig<SoundEffectType> {
	@NotNull
	private SoundEffectType effectType;
	@NotNull
	private Sound sound;
	private int cooldownMin;
	private int cooldownMax;
	private String cooldownId;

	public SoundEffectConfig(@NotNull SoundEffectType effectType, @NotNull Sound sound, int cooldownMin, int cooldownMax) {
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

	public void init(AmbienceUser user) {
		setCooldown(user);
	}

	private void setCooldown(AmbienceUser user) {
		user.setCooldown(cooldownId, cooldownMin + RandomUtils.getRandom().nextInt(cooldownMax - cooldownMin + 1));
	}

	public void update(AmbienceUser user) {
		Player player = user.getPlayer();
		if (player == null)
			return;

		if (!effectType.conditionsMet(user, this))
			return;

		if (user.updateCooldown(cooldownId) <= 0) {
			user.getSoundPlayer().playSound(sound, player.getLocation());
			setCooldown(user);
		}
	}

}
