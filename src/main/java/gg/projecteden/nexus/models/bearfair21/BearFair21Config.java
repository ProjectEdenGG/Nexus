package gg.projecteden.nexus.models.bearfair21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "bearfair21_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class BearFair21Config implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<BearFair21ConfigOption, Boolean> config = new ConcurrentHashMap<>();

	public boolean isEnabled(BearFair21ConfigOption option) {
		return config.getOrDefault(option, false);
	}

	public boolean isDisabled(BearFair21ConfigOption option) {
		return !isEnabled(option);
	}

	public void setEnabled(BearFair21ConfigOption option, boolean enabled) {
		config.put(option, enabled);
	}

	public enum BearFair21ConfigOption {
		WARP,
		EDIT,
		RIDES,
		QUESTS,
		GIVE_REWARDS,
		GIVE_DAILY_TOKENS,
		SKIP_WAITS,
	}

}
