package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class MiniGolfBallSinkEvent extends MiniGolfBallMoveEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	private final String holeRegion;
	@Getter
	private int strokes;
	@Getter
	private int par;
	@Setter
	private String score = MiniGolfUtils.getScore(strokes, par);
	@Setter
	private String message = "Strokes: " + strokes + " (" + score + ")";

	public MiniGolfBallSinkEvent(GolfBall golfBall, String holeRegion, int strokes, int par) {
		super(golfBall);
		this.holeRegion = holeRegion;
		this.strokes = strokes;
		this.par = par;
	}

	public void sendScore() {
		if (message == null) return;

		MiniGolfUtils.sendActionBar(golfBall.getUser(), message);
	}
}
