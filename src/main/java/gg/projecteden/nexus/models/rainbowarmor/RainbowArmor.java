package gg.projecteden.nexus.models.rainbowarmor;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "rainbow_armor", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class RainbowArmor implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;
	private transient RainbowArmorTask task;

	public void stop() {
		if (task != null) {
			task.stop();
			task = null;
		}
	}

	public void start() {
		stop();

		task = RainbowArmorTask.builder()
			.inventory(getOnlinePlayer().getInventory())
			.cancelIf(this::isNotAllowed)
			.onCancel(() -> {
				if (enabled)
					task.removeColor();
			})
			.start();
	}

	public boolean isNotAllowed() {
		return !isOnline() || PlayerManager.get(getOnlinePlayer()).isPlaying();
	}

}
