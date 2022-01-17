package gg.projecteden.nexus.models.hallofhistory;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.LocalDateConverter;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.nerd.Rank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "hall_of_history", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class HallOfHistory implements PlayerOwnedObject {
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
		private boolean current;
		@NonNull
		private LocalDate promotionDate;
		private LocalDate resignationDate;
	}
}
