package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage;

import lombok.Getter;
import me.pugabyte.nexus.features.menus.sabotage.VotingScreen;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class MinigamerVoteEvent extends MinigamerEvent {
	private final @Nullable Minigamer target;
	private final @NotNull VotingScreen votingScreen;
	public MinigamerVoteEvent(Minigamer minigamer, @Nullable Minigamer target, @NotNull VotingScreen votingScreen) {
		super(minigamer);
		this.target = target;
		this.votingScreen = votingScreen;
	}
}
