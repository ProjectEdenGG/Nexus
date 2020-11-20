package me.pugabyte.nexus.features.store.perks.stattrack.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class StatIncreaseEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private ItemStack item;
	private Stat stat;
	private int value;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
