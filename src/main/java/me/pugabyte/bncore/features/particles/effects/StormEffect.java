package me.pugabyte.bncore.features.particles.effects;

import lombok.Builder;
import lombok.Getter;

public class StormEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public StormEffect() {
		taskId = -1;
	}
}
