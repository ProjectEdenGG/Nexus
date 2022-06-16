package gg.projecteden.nexus.models.checkpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckpointKey {
	private String arenaName;
	private int checkpointNumber;
}
