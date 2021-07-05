package me.pugabyte.nexus.features.minigolf.models.events;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.features.minigolf.models.GolfBall;
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
