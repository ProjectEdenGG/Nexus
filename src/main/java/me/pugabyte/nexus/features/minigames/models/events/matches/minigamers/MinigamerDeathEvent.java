package me.pugabyte.nexus.features.minigames.models.events.matches.minigamers;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

@Getter
public class MinigamerDeathEvent extends MinigamerEvent implements Cancellable {
	@Nullable
	private final Minigamer attacker;
	@Nullable
	private final Event originalEvent;
	@Setter
	private String deathMessage = "";

	public MinigamerDeathEvent(@NonNull Minigamer victim) {
		super(victim);
		attacker = null;
		originalEvent = null;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Minigamer attacker) {
		super(victim);
		this.attacker = attacker;
		originalEvent = null;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Event originalEvent) {
		super(victim);
		attacker = null;
		this.originalEvent = originalEvent;
	}

	public MinigamerDeathEvent(@NonNull Minigamer victim, @Nullable Minigamer attacker, @Nullable Event originalEvent) {
		super(victim);
		this.attacker = attacker;
		this.originalEvent = originalEvent;
	}

	public void broadcastDeathMessage() {
		if (deathMessage == null) return;
		if (deathMessage.isEmpty()) {
			boolean showTeam = minigamer.getMatch().getMechanic().showTeamOnDeath();
			String victimName = showTeam ? minigamer.getColoredName() : "&3" + minigamer.getNickname();
			if (attacker == null)
				deathMessage = victimName + " &3died";
			else
				deathMessage = victimName + " &3was killed by " + (showTeam ? attacker.getColoredName() : "&3" + attacker.getNickname());
		}
		getMatch().broadcast(deathMessage);
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
