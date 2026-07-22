package gg.projecteden.nexus.features.titan.serverbound;

import gg.projecteden.nexus.features.titan.models.Serverbound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scroll extends Serverbound {
	private boolean up;

	@Override
	public void onReceive(Player player) {
		new PlayerScrollMenuEvent(player, up).callEvent();
	}

	public static class PlayerScrollMenuEvent extends PlayerEvent {

		@Getter
		private final boolean up;

		public PlayerScrollMenuEvent(@NotNull Player player, boolean up) {
			super(player);
			this.up = up;
		}

		@Getter
		private static final HandlerList handlerList = new HandlerList();

		@Override
		public @NotNull HandlerList getHandlers() {
			return handlerList;
		}



	}

}
