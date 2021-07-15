package me.pugabyte.nexus.models.ambience;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.ambience.effects.sounds.common.SoundEffect;
import me.pugabyte.nexus.features.ambience.effects.sounds.common.SoundPlayer;
import me.pugabyte.nexus.features.ambience.managers.SoundEffectManager;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManager;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManagers;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("ambience_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class AmbienceUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean debug;
	//
	private boolean sounds;
	private boolean particles;
	//
	private transient Variables variables;
	private transient SoundPlayer soundPlayer;
	private transient Map<String, Integer> cooldowns;

	@Override
	public void debug(String message) {
		if (debug)
			sendMessage(message);
	}

	public Variables getVariables() {
		if (variables == null) {
			variables = new Variables(getPlayer());
		}

		return variables;
	}

	public Map<String, Integer> getCooldowns() {
		if (cooldowns == null) {
			final AmbienceManager<SoundEffect> soundEffectManager = AmbienceManagers.SOUND_EFFECTS.get();
			SoundEffectManager manager = (SoundEffectManager) soundEffectManager;
			manager.init(this);
		}

		return cooldowns;
	}

	public int updateCooldown(String key) {
		if (!cooldowns.containsKey(key)) return 0;

		int value = cooldowns.get(key) - 1;
		cooldowns.put(key, value);
		return value;
	}

	public int getCooldown(String key) {
		if (!cooldowns.containsKey(key)) return 0;

		return cooldowns.get(key);
	}

	public void setCooldown(String key, int value) {
		if (value < 0)
			value = 0;

		cooldowns.put(key, value);
	}
}
