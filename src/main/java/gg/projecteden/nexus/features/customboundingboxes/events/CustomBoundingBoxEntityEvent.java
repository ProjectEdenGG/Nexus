package gg.projecteden.nexus.features.customboundingboxes.events;

import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public abstract class CustomBoundingBoxEntityEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlerList = new HandlerList();
	private final CustomBoundingBoxEntity entity;
	private boolean cancelled;

	public CustomBoundingBoxEntityEvent(@NotNull Player who, CustomBoundingBoxEntity entity) {
		super(who);
		this.entity = entity;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
