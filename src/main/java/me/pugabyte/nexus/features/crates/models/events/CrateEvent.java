package me.pugabyte.nexus.features.crates.models.events;

import lombok.Data;
import me.pugabyte.nexus.features.crates.models.CrateLoot;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Base Event class for customization of Crates.
 */
@Data
public abstract class CrateEvent extends Event {

	public Player player;
	public CrateLoot crateLoot;
	private static final HandlerList handlers = new HandlerList();

	public CrateEvent(Player player, CrateLoot crateLoot) {
		this.player = player;
		this.crateLoot = crateLoot;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
