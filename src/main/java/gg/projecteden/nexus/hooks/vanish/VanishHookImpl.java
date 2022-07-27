package gg.projecteden.nexus.hooks.vanish;

import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class VanishHookImpl extends VanishHook {

	@Override
	public boolean isInvisible(Player player) {
		return VanishAPI.isInvisible(player);
	}

	@Override
	public void hidePlayer(Player player) {
		VanishAPI.hidePlayer(player);
	}

	@Override
	public void showPlayer(Player player) {
		VanishAPI.showPlayer(player);
	}

	@EventHandler
	public void on(PlayerVanishStateChangeEvent originalEvent) {
		final var hookEvent = new VanishStateChangeEvent(originalEvent.getUUID(), originalEvent.getName(), originalEvent.isVanishing(), originalEvent.getCause());
		if (!hookEvent.callEvent())
			originalEvent.setCancelled(true);
	}

}
