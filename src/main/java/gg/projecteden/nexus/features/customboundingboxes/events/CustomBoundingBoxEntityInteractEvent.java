package gg.projecteden.nexus.features.customboundingboxes.events;

import gg.projecteden.nexus.models.customboundingbox.CustomBoundingBoxEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class CustomBoundingBoxEntityInteractEvent extends CustomBoundingBoxEntityEvent {
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	private final EquipmentSlot hand;
	private final PlayerEvent originalEvent;

	public CustomBoundingBoxEntityInteractEvent(@NotNull Player who, CustomBoundingBoxEntity entity, PlayerEvent originalEvent) {
		super(who, entity);
		this.originalEvent = originalEvent;

		if (originalEvent instanceof PlayerInteractEvent interactEvent)
			this.hand = interactEvent.getHand();
		else if (originalEvent instanceof PlayerInteractEntityEvent interactEntityEvent)
			this.hand = interactEntityEvent.getHand();
		else
			this.hand = null;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}

}
