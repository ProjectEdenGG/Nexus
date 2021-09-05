package gg.projecteden.nexus.models.striplogs;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "strip_logs", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class StripLogs implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Behavior behavior = Behavior.DEFAULT;

	public enum Behavior {
		PREVENT,
		REQUIRE_SHIFT,
		DEFAULT
	}
}
