package gg.projecteden.nexus.models.bossfight;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Data
@Entity(value = "boss_fight_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BossFightUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<BossFightUserSetting, Boolean> settings = new ConcurrentHashMap<>();


	public boolean getSetting(BossFightUserSetting setting) {
		return settings.getOrDefault(setting, setting.defaultValue);
	}

	public void setSetting(BossFightUserSetting setting, boolean value) {
		if (value == setting.defaultValue)
			settings.remove(setting);
		else
			settings.put(setting, value);
	}

	@Getter
	@AllArgsConstructor
	public enum BossFightUserSetting {
		BROADCASTS(
			true,
			"Hides your boss fight broadcasts from other players",
			null,
			value -> "&3Your own boss fight broadcasts are now " + (value ? "&ashown" : "&chidden") + " &3from other players"
		),
		;

		private final boolean defaultValue;
		private final String description;
		private final String descriptionExtra;
		private final Function<Boolean, String> message;
	}

}

