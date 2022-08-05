package gg.projecteden.nexus.models.afk.events;

import gg.projecteden.nexus.models.afk.AFKUser;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
public class AFKEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	@NonNull
	protected final AFKUser user;

	public AFKEvent(@NotNull AFKUser user) {
		super(true);
		this.user = user;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
