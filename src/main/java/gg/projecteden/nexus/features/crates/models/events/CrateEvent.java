package gg.projecteden.nexus.features.crates.models.events;

import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import lombok.Data;
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
	public CrateType crateType;
	private static final HandlerList handlers = new HandlerList();

	public CrateEvent(Player player, CrateLoot crateLoot, CrateType type) {
		this.player = player;
		this.crateLoot = crateLoot;
		this.crateType = type;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
