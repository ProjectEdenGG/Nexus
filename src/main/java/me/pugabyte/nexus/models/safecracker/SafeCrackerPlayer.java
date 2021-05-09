package me.pugabyte.nexus.models.safecracker;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
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
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("safe_cracker_player")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
@Disabled
public class SafeCrackerPlayer implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private Map<String, Game> games = new HashMap<>();

	public Game getActiveGame() {
		return games.get(new SafeCrackerEventService().getActiveEvent().getName());
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
	public static class Game {
		private int score;
		private LocalDateTime started;
		private Map<String, SafeCrackerPlayerNPC> npcs = new HashMap<>();

		public boolean isFinished() {
			return score != 0;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters(LocalDateTimeConverter.class)
	public static class SafeCrackerPlayerNPC {
		private int id;
		private String name;
		private LocalDateTime found;
		private String answer;
		private boolean correct;
	}


}
