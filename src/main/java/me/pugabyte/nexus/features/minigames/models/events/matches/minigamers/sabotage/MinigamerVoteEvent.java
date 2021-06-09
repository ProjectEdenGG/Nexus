package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage;

import lombok.Getter;
import me.pugabyte.nexus.features.menus.sabotage.VotingScreen;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class MinigamerVoteEvent extends MinigamerEvent implements Cancellable {
	private final @Nullable Minigamer target;
	private final @NotNull VotingScreen votingScreen;
	public MinigamerVoteEvent(Minigamer minigamer, @Nullable Minigamer target, @NotNull VotingScreen votingScreen) {
		super(minigamer);
		this.target = target;
		this.votingScreen = votingScreen;
	}

	protected boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
