package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.models.minigolf.GolfBall;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;

public class MiniGolfBallBounceEvent extends MiniGolfBallMoveEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	Material material;

	public MiniGolfBallBounceEvent(GolfBall golfBall) {
		super(golfBall);
	}
}
