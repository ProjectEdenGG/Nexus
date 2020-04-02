package me.pugabyte.bncore.features.particles.effects;

import lombok.Builder;
import lombok.Getter;

public class SphereEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public SphereEffect() {
		taskId = -1;
	}
}
