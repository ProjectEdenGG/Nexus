package gg.projecteden.nexus.features.minigames.models.events.matches.minigamers;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEvent;
import lombok.Getter;
import lombok.NonNull;

public abstract class MinigamerMatchEvent extends MatchEvent {
	@Getter
	@NonNull
	protected final Minigamer minigamer;

	public MinigamerMatchEvent(Minigamer minigamer) {
		super(minigamer.getMatch());
		this.minigamer = minigamer;
	}

}
