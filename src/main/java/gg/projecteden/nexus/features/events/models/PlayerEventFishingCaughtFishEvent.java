package gg.projecteden.nexus.features.events.models;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerEventFishingCaughtFishEvent extends PlayerEvent {
	@Getter
	private static final HandlerList handlerList = new HandlerList();
	@Getter
	List<ItemStack> loot;

	public PlayerEventFishingCaughtFishEvent(@NotNull Player who, List<ItemStack> loot) {
		super(who);
		this.loot = loot;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
