package gg.projecteden.nexus.features.minigolf.models.events;

import gg.projecteden.nexus.features.minigolf.models.GolfBall;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

public class MiniGolfBallDeathEvent extends MiniGolfBallEvent implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	private final DeathCause deathCause;

	public MiniGolfBallDeathEvent(GolfBall golfBall, DeathCause deathCause) {
		super(golfBall);
		this.deathCause = deathCause;
	}

	public enum DeathCause {
		OUT_OF_BOUNDS,
		RECALLED,
		DEATH_BLOCK,
		DEATH_ENTITY
	}
}
