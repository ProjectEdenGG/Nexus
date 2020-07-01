package me.pugabyte.bncore.models.safecracker;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.converters.BooleanConverter;
import lombok.*;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

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
public class SafeCrackerEvent extends PlayerOwnedObject {

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
