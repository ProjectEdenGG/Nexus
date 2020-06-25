package me.pugabyte.bncore.models.safecracker;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.*;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("safe_cracker_player")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class SafeCrackerPlayer extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private Map<String, Game> games = new HashMap<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
	public static class Game {
		private int score;
		private LocalDateTime started;
		private Map<String, SafeCrackerPlayerNPC> npcs = new HashMap<>();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters(LocalDateTimeConverter.class)
	public static class SafeCrackerPlayerNPC {
		private LocalDateTime found;
		private String answer;
		private boolean correct;
	}


}
