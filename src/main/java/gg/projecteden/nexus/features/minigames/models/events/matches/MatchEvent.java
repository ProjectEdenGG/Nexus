package gg.projecteden.nexus.features.minigames.models.events.matches;

import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.events.MinigameEvent;
import lombok.Data;
import lombok.NonNull;

@Data
public abstract class MatchEvent extends MinigameEvent {
	@NonNull
	protected final Match match;

}
