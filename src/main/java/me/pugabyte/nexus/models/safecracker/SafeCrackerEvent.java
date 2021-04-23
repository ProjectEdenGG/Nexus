package me.pugabyte.nexus.models.safecracker;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.converters.BooleanConverter;
import eden.annotations.Disabled;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("safe_cracker_event")
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
	private Map<String, SafeCrackerGame> games = new HashMap<>();

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
		private Map<String, SafeCrackerNPC> npcs = new HashMap<>();
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
