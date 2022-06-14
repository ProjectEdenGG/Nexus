package gg.projecteden.nexus.models.checkpoint;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
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
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, RecordTotalTime> bestTotalTimes = new ConcurrentHashMap<>();
	private Map<Pair<String, Integer>, Duration> bestCheckpointTimes = new ConcurrentHashMap<>(); // for sum of best

	public void recordTotalTime(String arena, Duration duration, Map<Integer, Duration> checkpointTimes) {
		RecordTotalTime newRecord = new RecordTotalTime(uuid, duration, checkpointTimes);
		if (!bestTotalTimes.containsKey(arena) || bestTotalTimes.get(arena).compareTo(newRecord) > 0) {
			bestTotalTimes.put(arena, newRecord);
		}
		CheckpointService.recordBestTotalTime(arena, newRecord);
	}

	public void recordCheckpointTime(String arena, int checkpoint, Duration duration) {
		Pair<String, Integer> key = new Pair<>(arena, checkpoint);
		if (!bestCheckpointTimes.containsKey(key) || bestCheckpointTimes.get(key).compareTo(duration) > 0) {
			bestCheckpointTimes.put(key, duration);
		}
		// service does not currently keep a cache of the best checkpoint times
	}

	public @Nullable RecordTotalTime getBestTotalTime(String arena) {
		return bestTotalTimes.get(arena);
	}

	public @Nullable Duration getBestCheckpointTime(String arena, int checkpoint) {
		Pair<String, Integer> key = new Pair<>(arena, checkpoint);
		return bestCheckpointTimes.get(key);
	}

	// handy dandy boilerplate

	public void recordTotalTime(Arena arena, Duration duration, Map<Integer, Duration> checkpointTimes) {
		recordTotalTime(arena.getName(), duration, checkpointTimes);
	}

	public void recordCheckpointTime(Arena arena, int checkpoint, Duration duration) {
		recordCheckpointTime(arena.getName(), checkpoint, duration);
	}

	public @Nullable RecordTotalTime getBestTotalTime(Arena arena) {
		return getBestTotalTime(arena.getName());
	}

	public @Nullable Duration getBestCheckpointTime(Arena arena, int checkpoint) {
		return getBestCheckpointTime(arena.getName(), checkpoint);
	}

	public void recordTotalTime(Match match, Duration duration, Map<Integer, Duration> checkpointTimes) {
		recordTotalTime(match.getArena(), duration, checkpointTimes);
	}

	public void recordCheckpointTime(Match match, int checkpoint, Duration duration) {
		recordCheckpointTime(match.getArena(), checkpoint, duration);
	}

	public @Nullable RecordTotalTime getBestTotalTime(Match match) {
		return getBestTotalTime(match.getArena());
	}

	public @Nullable Duration getBestCheckpointTime(Match match, int checkpoint) {
		return getBestCheckpointTime(match.getArena(), checkpoint);
	}

	public void recordTotalTime(MatchData matchData, Duration duration, Map<Integer, Duration> checkpointTimes) {
		recordTotalTime(matchData.getMatch(), duration, checkpointTimes);
	}

	public void recordCheckpointTime(MatchData matchData, int checkpoint, Duration duration) {
		recordCheckpointTime(matchData.getMatch(), checkpoint, duration);
	}

	public @Nullable RecordTotalTime getBestTotalTime(MatchData matchData) {
		return getBestTotalTime(matchData.getMatch());
	}

	public @Nullable Duration getBestCheckpointTime(MatchData matchData, int checkpoint) {
		return getBestCheckpointTime(matchData.getMatch(), checkpoint);
	}
}
