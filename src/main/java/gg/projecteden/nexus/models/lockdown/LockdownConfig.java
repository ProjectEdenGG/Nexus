package gg.projecteden.nexus.models.lockdown;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.*;
import org.bukkit.Location;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "lockdown_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class LockdownConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;
	private String reason;
	private LocalDateTime end;
	private final Set<UUID> bypass = new HashSet<>();

	public void end() {
		enabled = false;
		reason = null;
		end = null;
		bypass.clear();
	}

	public void broadcast(String message) {
		Chat.Broadcast.log().prefix("Justice").message(message).send();
	}
}
