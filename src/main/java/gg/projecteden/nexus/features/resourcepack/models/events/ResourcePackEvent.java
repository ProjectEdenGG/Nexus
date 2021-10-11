package gg.projecteden.nexus.features.resourcepack.models.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@NoArgsConstructor
public abstract class ResourcePackEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
