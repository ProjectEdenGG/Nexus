package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
import org.bukkit.event.Cancellable;

public class MiniGolfBallSpawnEvent extends MiniGolfBallMoveEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	public MiniGolfBallSpawnEvent(GolfBall golfBall) {
		super(golfBall);
	}
}
