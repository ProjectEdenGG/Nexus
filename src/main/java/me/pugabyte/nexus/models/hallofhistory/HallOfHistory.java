package me.pugabyte.nexus.models.hallofhistory;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Rank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity("hall_of_history")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class HallOfHistory extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<RankHistory> rankHistory = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters(LocalDateConverter.class)
	public static class RankHistory {
		@NonNull
		private Rank rank;
		@NonNull
		private boolean current;
		@NonNull
		private LocalDate promotionDate;
		private LocalDate resignationDate;
	}
}
