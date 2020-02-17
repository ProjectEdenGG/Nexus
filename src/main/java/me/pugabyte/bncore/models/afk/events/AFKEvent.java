package me.pugabyte.bncore.models.afk.events;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@RequiredArgsConstructor
public class AFKEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	@NonNull
	AFKPlayer player;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
