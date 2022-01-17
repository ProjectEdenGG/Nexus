package gg.projecteden.nexus.models.afk.events;

import gg.projecteden.nexus.models.afk.AFKUser;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@RequiredArgsConstructor
public class AFKEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	@NonNull
	protected final AFKUser user;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
