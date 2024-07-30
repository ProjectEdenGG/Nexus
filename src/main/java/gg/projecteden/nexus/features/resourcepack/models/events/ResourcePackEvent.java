package gg.projecteden.nexus.features.resourcepack.models.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class ResourcePackEvent extends Event {
	@Getter
	private static final HandlerList handlerList = new HandlerList();

	public ResourcePackEvent() {
		super(true);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
