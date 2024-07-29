package gg.projecteden.nexus.features.customboundingboxes.events;

import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class CustomBoundingBoxEntityTargetTickEvent extends CustomBoundingBoxEntityEvent {
	private static final HandlerList handlerList = new HandlerList();
	private boolean cancelled;

	public CustomBoundingBoxEntityTargetTickEvent(@NotNull Player who, CustomBoundingBoxEntity entity) {
		super(who, entity);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
