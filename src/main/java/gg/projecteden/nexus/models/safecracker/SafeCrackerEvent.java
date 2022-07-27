package gg.projecteden.nexus.models.safecracker;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.converters.BooleanConverter;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "safe_cracker_event", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
@Disabled
public class SafeCrackerEvent implements PlayerOwnedObject {

	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private Map<String, SafeCrackerGame> games = new ConcurrentHashMap<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({LocalDateTimeConverter.class, BooleanConverter.class})
	public static class SafeCrackerGame {
		private String name;
		private boolean active;
		private LocalDateTime created;
		private String riddle;
		private String answer;
		@Embedded
		private Map<String, SafeCrackerNPC> npcs = new ConcurrentHashMap<>();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SafeCrackerNPC {
		private int id;
		private String name;
		private String question;
		private List<String> answers;
		private String riddle;
	}

}
