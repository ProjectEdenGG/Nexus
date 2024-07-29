package gg.projecteden.nexus.features.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class PlayerEventFishingBiteEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlerList = new HandlerList();
	@Setter
	private boolean cancelled;
	List<ItemStack> loot;

	public PlayerEventFishingBiteEvent(@NotNull Player who, List<ItemStack> loot) {
		super(who);
		this.loot = loot;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
