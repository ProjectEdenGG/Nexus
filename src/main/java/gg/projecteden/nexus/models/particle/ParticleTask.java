package gg.projecteden.nexus.models.particle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticleTask {
	private ParticleType particleType;
	private int taskId;

}
