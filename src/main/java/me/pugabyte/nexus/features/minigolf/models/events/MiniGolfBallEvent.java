package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;

public class MiniGolfBallEvent extends MiniGolfEvent {
	@Getter
	protected GolfBall golfBall;

	public MiniGolfBallEvent(final GolfBall golfBall) {
		this.golfBall = golfBall;
	}
}
