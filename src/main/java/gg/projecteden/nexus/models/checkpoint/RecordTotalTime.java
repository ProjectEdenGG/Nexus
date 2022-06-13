package gg.projecteden.nexus.models.checkpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordTotalTime implements Comparable<RecordTotalTime> {
	private UUID playerId;
	private Duration time;

	@Override
	public int compareTo(@NotNull RecordTotalTime o) {
		return time.compareTo(o.time);
	}
}
