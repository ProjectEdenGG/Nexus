package me.pugabyte.nexus.features.ambience.effects.birds.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.features.ambience.effects.common.AmbientEffectConfig;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class BirdEffectConfig implements AmbientEffectConfig<BirdEffectType> {
	@NotNull
	private final BirdEffectType effectType;
	private final int cooldownMin;
	private final int cooldownMax;
	private final String cooldownId;

	public BirdEffectConfig(@NotNull BirdEffectType effectType, int cooldownMin, int cooldownMax) {
		this.effectType = effectType;
		this.cooldownMin = cooldownMin;
		this.cooldownMax = cooldownMax;
		this.cooldownId = effectType.name().toLowerCase();

		if (cooldownMin < 0)
			throw new IllegalArgumentException("cooldown minimum cannot be negative");
		if (cooldownMax < 0)
			throw new IllegalArgumentException("cooldown maximum cannot be negative");
		if (cooldownMax < cooldownMin)
			throw new IllegalArgumentException("cooldown minimum cannot be larger than cooldown maximum");
	}

	public void init(AmbienceUser user) {
		setCooldown(user);
	}

	public void update(AmbienceUser user) {
		Player player = user.getPlayer();
		if (player == null)
			return;

		if (!effectType.conditionsMet(user, this))
			return;

		if (user.updateCooldown(cooldownId) <= 0) {
//			user.getSoundPlayer().playSound(sound, player.getLocation());
			setCooldown(user);
		}
	}

	private void setCooldown(AmbienceUser user) {
		user.setCooldown(cooldownId, cooldownMin + RandomUtils.getRandom().nextInt(cooldownMax - cooldownMin + 1));
	}


}
