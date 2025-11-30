package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

public class MiniGolfBallSpawnEvent extends MiniGolfBallMoveEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;
	@Getter
	@Setter
	private Location location;

	public MiniGolfBallSpawnEvent(GolfBall golfBall, Location location) {
		super(golfBall);
		this.location = location;
	}

	public void spawnBall() {
		golfBall.spawn(location);
	}
}
