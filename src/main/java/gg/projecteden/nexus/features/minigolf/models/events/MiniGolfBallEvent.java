package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import lombok.Getter;

public class MiniGolfBallEvent extends MiniGolfEvent {
	@Getter
	protected GolfBall golfBall;

	public MiniGolfBallEvent(final GolfBall golfBall) {
		this.golfBall = golfBall;
	}
}
