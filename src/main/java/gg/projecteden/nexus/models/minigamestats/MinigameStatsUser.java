package gg.projecteden.nexus.models.minigamestats;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "minigamer_stats", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class MinigameStatsUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<MatchStatRecord> statistics = new ArrayList<>();

	public void addRecord(MatchStatRecord record) {
		statistics.add(record);
	}

	public static class MatchStatRecord {
		MechanicType mechanic;
		LocalDateTime date;
		Map<MinigameStatistic, Integer> stats;

		public MatchStatRecord(MechanicType mechanic, Map<MinigameStatistic, Integer> stats) {
			if (mechanic == null)
				throw new InvalidInputException("MechanicType cannot be null");

			this.mechanic = mechanic;
			this.date = LocalDateTime.now();
			this.stats = stats;
		}
	}

}
