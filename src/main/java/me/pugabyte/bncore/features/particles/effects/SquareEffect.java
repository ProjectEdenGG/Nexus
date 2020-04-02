package me.pugabyte.bncore.features.particles.effects;

import lombok.Builder;
import lombok.Getter;

public class SquareEffect {
	@Getter
	private int taskId;

	@Builder(buildMethodName = "start")
	public SquareEffect() {
		taskId = -1;
	}
}
