package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MiniGolfBallEvent extends MiniGolfEvent {
	@Getter
	protected GolfBall golfBall;

	public MiniGolfBallEvent(final GolfBall golfBall) {
		this.golfBall = golfBall;
	}
}
