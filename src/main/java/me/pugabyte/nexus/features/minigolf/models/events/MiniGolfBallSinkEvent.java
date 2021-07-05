package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.event.Cancellable;

public class MiniGolfBallSinkEvent extends MiniGolfBallMoveEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	private int strokes;
	@Getter
	private int par;
	@Setter
	private String score = MiniGolfUtils.getScore(strokes, par);
	@Setter
	private String message = "Strokes: " + strokes + " (" + score + ")";

	public MiniGolfBallSinkEvent(GolfBall golfBall, int strokes, int par) {
		super(golfBall);
		this.strokes = strokes;
		this.par = par;
	}

	public void sendScore() {
		if (message == null) return;

		MiniGolfUtils.sendActionBar(golfBall.getUser(), message);
	}
}
