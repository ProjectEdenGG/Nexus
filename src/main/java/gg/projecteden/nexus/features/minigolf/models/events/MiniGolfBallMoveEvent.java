package gg.projecteden.nexus.features.minigolf.models.events;

import com.google.common.base.Preconditions;
import gg.projecteden.nexus.models.minigolf.GolfBall;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

@NoArgsConstructor
public class MiniGolfBallMoveEvent extends MiniGolfBallEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	private Location from;

	@Getter
	private Location to;

	public MiniGolfBallMoveEvent(final GolfBall golfBall) {
		super(golfBall);
		this.from = golfBall.getLastLocation();
		this.to = null;
		if (golfBall.isAlive())
			this.to = golfBall.getBallLocation();
	}

	public MiniGolfBallMoveEvent(final GolfBall golfBall, final Location from, final Location to) {
		super(golfBall);
		this.from = from;
		this.to = to;
	}

	public void setFrom(Location from) {
		validateLocation(from);
		this.from = from;
	}

	public void setTo(Location to) {
		validateLocation(to);
		this.to = to;
	}

	private void validateLocation(Location loc) {
		Preconditions.checkArgument(loc != null, "Cannot use null location!");
		Preconditions.checkArgument(loc.getWorld() != null, "Cannot use null location with null world!");
	}
}
