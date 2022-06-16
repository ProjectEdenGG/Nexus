package gg.projecteden.nexus.models.checkpoint;

import gg.projecteden.mongodb.interfaces.PlayerOwnedObject;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class RecordTotalTime implements Comparable<RecordTotalTime>, PlayerOwnedObject {

	// TODO: experiment with replay storage

	private UUID playerId;
	private Duration time;
	private Map<Integer, Duration> checkpointTimes; // map of checkpoint number to time it took the player to complete that checkpoint
	private @Nullable Instant achievedAt;

	public RecordTotalTime() {
	}

	public RecordTotalTime(UUID playerId, Duration time, Map<Integer, Duration> checkpointTimes, Instant achievedAt) {
		this.playerId = playerId;
		this.time = time;
		this.checkpointTimes = new ConcurrentHashMap<>(checkpointTimes);
		this.achievedAt = achievedAt;
	}

	@Override
	public int compareTo(@NotNull RecordTotalTime o) {
		return time.compareTo(o.time);
	}

	public Map<Integer, Duration> getCheckpointTimesAsSum() {
		// sort map
		Map<Integer, Duration> map = new HashMap<>();
		List<Entry<Integer, Duration>> entries = new ArrayList<>(checkpointTimes.entrySet());
		entries.sort(Entry.comparingByKey());
		entries.add(entries.remove(0)); // move the end checkpoint (`-1`) to the end
		// compute sums
		Duration sum = Duration.ZERO;
		for (Map.Entry<Integer, Duration> entry : entries) {
			sum = sum.plus(entry.getValue());
			map.put(entry.getKey(), sum);
		}
		return map;
	}

	@Override
	public @NotNull UUID getUuid() {
		return playerId;
	}
}
