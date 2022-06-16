package gg.projecteden.nexus.models.checkpoint;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "checkpoint_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class CheckpointUser implements PlayerOwnedObject {
	private static final CheckpointService service = new CheckpointService();
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, RecordTotalTime> bestTotalTimes = new ConcurrentHashMap<>();
	private Map<CheckpointKey, CheckpointValue> bestCheckpointTimes = new ConcurrentHashMap<>(); // for sum of best

	public void recordTotalTime(String arena, Duration duration, Map<Integer, Duration> checkpointTimes, Instant achievedAt) {
		RecordTotalTime newRecord = new RecordTotalTime(uuid, duration, checkpointTimes, achievedAt);
		if (!bestTotalTimes.containsKey(arena) || bestTotalTimes.get(arena).compareTo(newRecord) > 0) {
			bestTotalTimes.put(arena, newRecord);
			service.save(this);
		}
		CheckpointService.recordBestTotalTime(arena, newRecord);
	}

	public void recordCheckpointTime(CheckpointKey key, CheckpointValue value) {
		if (!bestCheckpointTimes.containsKey(key) || bestCheckpointTimes.get(key).compareTo(value) > 0) {
			bestCheckpointTimes.put(key, value);
			service.save(this);
		}
		// service does not currently keep a cache of the best checkpoint times
	}

	public void recordCheckpointTime(String arena, int checkpoint, Duration duration, Instant achievedAt) {
		recordCheckpointTime(new CheckpointKey(arena, checkpoint), new CheckpointValue(uuid, duration, achievedAt));
	}

	public @Nullable RecordTotalTime getBestTotalTime(String arena) {
		return bestTotalTimes.get(arena);
	}

	public @Nullable CheckpointValue getBestCheckpointTime(CheckpointKey key) {
		return bestCheckpointTimes.get(key);
	}

	public @Nullable CheckpointValue getBestCheckpointTime(String arena, int checkpoint) {
		return getBestCheckpointTime(new CheckpointKey(arena, checkpoint));
	}

	// handy dandy boilerplate

	public void recordTotalTime(Arena arena, Duration duration, Map<Integer, Duration> checkpointTimes, Instant achievedAt) {
		recordTotalTime(arena.getName(), duration, checkpointTimes, achievedAt);
	}

	public void recordCheckpointTime(Arena arena, int checkpoint, Duration duration, Instant achievedAt) {
		recordCheckpointTime(arena.getName(), checkpoint, duration, achievedAt);
	}

	public @Nullable RecordTotalTime getBestTotalTime(Arena arena) {
		return getBestTotalTime(arena.getName());
	}

	public @Nullable CheckpointValue getBestCheckpointTime(Arena arena, int checkpoint) {
		return getBestCheckpointTime(arena.getName(), checkpoint);
	}

	public void recordTotalTime(Match match, Duration duration, Map<Integer, Duration> checkpointTimes, Instant achievedAt) {
		recordTotalTime(match.getArena(), duration, checkpointTimes, achievedAt);
	}

	public void recordCheckpointTime(Match match, int checkpoint, Duration duration, Instant achievedAt) {
		recordCheckpointTime(match.getArena(), checkpoint, duration, achievedAt);
	}

	public @Nullable RecordTotalTime getBestTotalTime(Match match) {
		return getBestTotalTime(match.getArena());
	}

	public @Nullable CheckpointValue getBestCheckpointTime(Match match, int checkpoint) {
		return getBestCheckpointTime(match.getArena(), checkpoint);
	}

	public void recordTotalTime(MatchData matchData, Duration duration, Map<Integer, Duration> checkpointTimes, Instant achievedAt) {
		recordTotalTime(matchData.getMatch(), duration, checkpointTimes, achievedAt);
	}

	public void recordCheckpointTime(MatchData matchData, int checkpoint, Duration duration, Instant achievedAt) {
		recordCheckpointTime(matchData.getMatch(), checkpoint, duration, achievedAt);
	}

	public @Nullable RecordTotalTime getBestTotalTime(MatchData matchData) {
		return getBestTotalTime(matchData.getMatch());
	}

	public @Nullable CheckpointValue getBestCheckpointTime(MatchData matchData, int checkpoint) {
		return getBestCheckpointTime(matchData.getMatch(), checkpoint);
	}
}
