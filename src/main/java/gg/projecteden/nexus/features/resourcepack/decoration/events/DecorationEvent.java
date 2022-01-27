package gg.projecteden.nexus.features.resourcepack.decoration.events;

import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DecorationEvent extends Event implements Cancellable {
	@Getter
	@Setter
	protected boolean cancelled = false;

	@Getter
	protected Player player;
	@Getter
	protected Location origin;
	@Getter
	protected Decoration decoration;
	@Getter
	@Setter
	protected ItemStack item;


	public DecorationEvent(Player player, Location origin, Decoration decoration, ItemStack item) {
		this.player = player;
		this.origin = origin;
		this.decoration = decoration;
		this.item = item;
	}

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}
}
