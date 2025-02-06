package gg.projecteden.nexus.models.blockparty;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.LocalDateConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
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
@Entity(value = "blockparty_stats", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateConverter.class})
public class BlockPartyStatsUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<BlockPartyStats> stats = new ArrayList<>();

	public void add(BlockPartyStats stat) {
		stats.add(stat);
	}

	@Data
	public static class BlockPartyStats {
		@NonNull
		public final LocalDate time;
		public boolean win;
		public int playTimeInSeconds;
		public int roundsSurvived;
		public int powerUpsCollected;
		public int powerUpsUsed;

		public BlockPartyStats() {
			this.time = LocalDate.now();
		}
	}

}
