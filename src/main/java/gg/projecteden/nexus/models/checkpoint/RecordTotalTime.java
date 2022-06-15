package gg.projecteden.nexus.models.checkpoint;

import gg.projecteden.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordTotalTime implements Comparable<RecordTotalTime>, PlayerOwnedObject {

	// TODO: experiment with replay storage

	private UUID playerId;
	private Duration time;
	private Map<Integer, Duration> checkpointTimes; // map of checkpoint number to time it took the player to complete that checkpoint

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
