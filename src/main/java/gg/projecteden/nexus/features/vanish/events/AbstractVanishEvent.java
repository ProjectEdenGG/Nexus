package gg.projecteden.nexus.features.vanish.events;

import gg.projecteden.nexus.models.vanish.VanishUser;
import gg.projecteden.nexus.models.vanish.VanishUserService;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractVanishEvent extends PlayerEvent {
	public AbstractVanishEvent(@NotNull Player who) {
		super(who);
	}

	public VanishUser getUser() {
		return new VanishUserService().get(player);
	}

	@Getter
	private static final HandlerList handlerList = new HandlerList();

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}
