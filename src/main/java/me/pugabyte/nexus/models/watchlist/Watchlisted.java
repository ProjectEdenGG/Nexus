package me.pugabyte.nexus.models.watchlist;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.JsonBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.features.commands.staff.WatchlistCommand.PREFIX;
import static me.pugabyte.nexus.utils.StringUtils.shortDateFormat;

@Data
@Builder
@Entity("watchlisted")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class Watchlisted extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean active;
	private LocalDateTime watchlistedOn;
	private String reason;
	private List<Note> notes = new ArrayList<>();

	public JsonBuilder getMessage() {
		return new JsonBuilder(PREFIX + "&c" + getName() + " was watchlisted for &e" + reason + " &con " + shortDateFormat(watchlistedOn.toLocalDate()))
				.command("/watchlist info " + getName());
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Note {
		private UUID author;
		private LocalDateTime timestamp;
		private String note;

		public Note(UUID author, String note) {
			this.author = author;
			this.timestamp = LocalDateTime.now();
			this.note = note;
		}

	}

}
