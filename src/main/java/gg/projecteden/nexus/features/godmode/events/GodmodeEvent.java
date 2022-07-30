package gg.projecteden.nexus.features.godmode.events;

import gg.projecteden.nexus.models.godmode.Godmode;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@RequiredArgsConstructor
public class GodmodeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	@NonNull
	protected final Godmode user;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
