package gg.projecteden.nexus.models.ambience;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.ambience.effects.sounds.common.SoundPlayer;
import gg.projecteden.nexus.features.ambience.managers.common.AmbienceManagers;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "ambience_user", noClassnameStored = true)
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
		if (variables == null)
			variables = new Variables(getPlayer());

		return variables;
	}

	public SoundPlayer getSoundPlayer() {
		if (soundPlayer == null)
			soundPlayer = new SoundPlayer(uuid);

		return soundPlayer;
	}

	public Map<String, Integer> getCooldowns() {
		if (cooldowns == null)
			AmbienceManagers.SOUNDS.get().init(this);

		return cooldowns;
	}

	public boolean hasCooldown(String key) {
		return getCooldowns().containsKey(key);
	}

	public int updateCooldown(String key) {
		if (!hasCooldown(key))
			return 0;

		int value = cooldowns.get(key) - 1;
		cooldowns.put(key, value);
		return value;
	}

	public int getCooldown(String key) {
		if (!hasCooldown(key))
			return 0;

		return cooldowns.get(key);
	}

	public void setCooldown(String key, int value) {
		if (value < 0)
			value = 0;

		if (cooldowns == null)
			cooldowns = new HashMap<>();

		cooldowns.put(key, value);
	}
}
