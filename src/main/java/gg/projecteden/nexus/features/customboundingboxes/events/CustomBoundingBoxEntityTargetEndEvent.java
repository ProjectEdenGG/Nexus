package gg.projecteden.nexus.features.customboundingboxes.events;

import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class CustomBoundingBoxEntityTargetEndEvent extends CustomBoundingBoxEntityEvent {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	public CustomBoundingBoxEntityTargetEndEvent(@NotNull Player who, CustomBoundingBoxEntity entity) {
		super(who, entity);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
