package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse.MiniGolfHole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Data
public class MiniGolfBallSinkEvent extends MiniGolfBallMoveEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	private final MiniGolfCourse course;
	private final MiniGolfHole hole;
	private final int strokes;
	private final String score;
	private final String message;

	public MiniGolfBallSinkEvent(GolfBall golfBall) {
		super(golfBall);
		this.course = golfBall.getCourse();
		this.hole = golfBall.getHole();
		this.strokes = golfBall.getStrokes();
		this.score = MiniGolfUtils.getScore(strokes, hole.getPar());
		this.message = "Strokes: " + strokes + " (" + score + ")";
	}

	public void sendScore() {
		if (message == null) return;

		MiniGolfUtils.sendActionBar(golfBall.getUser(), message);
	}
}
