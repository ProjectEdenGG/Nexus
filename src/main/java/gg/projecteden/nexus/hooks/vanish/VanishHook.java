package gg.projecteden.nexus.hooks.vanish;

import gg.projecteden.nexus.hooks.IHook;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class VanishHook extends IHook<VanishHook> {

	public boolean isInvisible(Player player) {
		return false;
	}

	public void hidePlayer(Player player) {}

	public void showPlayer(Player player) {}

	@Data
	@RequiredArgsConstructor
	public static class VanishStateChangeEvent extends Event implements Cancellable {
		private static final HandlerList handlers = new HandlerList();
		private final UUID uuid;
		private final String name;
		private final boolean vanishing;
		private final String cause;
		private boolean isCancelled = false;

		public static HandlerList getHandlerList() {
			return handlers;
		}

		public HandlerList getHandlers() {
			return handlers;
		}
	}

}
