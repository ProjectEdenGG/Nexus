package gg.projecteden.nexus.features.minigames.models.events.lobby;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.MinigameEvent;
import lombok.Data;

@Data
public abstract class MinigamerLobbyEvent extends MinigameEvent {
	private final Minigamer minigamer;

}
