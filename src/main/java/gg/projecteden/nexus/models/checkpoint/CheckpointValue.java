package gg.projecteden.nexus.models.checkpoint;

import gg.projecteden.api.mongodb.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckpointValue implements PlayerOwnedObject, Comparable<CheckpointValue> {
	private UUID playerId;
	private Duration time;
	private Instant achievedAt;

	@Override
	public int compareTo(@NotNull CheckpointValue o) {
		return time.compareTo(o.time);
	}

	@Override
	public @NotNull UUID getUuid() {
		return playerId;
	}
}
