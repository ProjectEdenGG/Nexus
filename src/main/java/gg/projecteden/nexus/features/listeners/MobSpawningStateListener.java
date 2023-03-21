package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.features.godmode.events.GodmodeEvent;
import gg.projecteden.nexus.features.vanish.events.VanishToggleEvent;
import gg.projecteden.nexus.models.afk.events.AFKEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class MobSpawningStateListener implements Listener {

	public static void update(Nerd nerd) {
		nerd.updateSetAffectsSpawning();
		nerd.updateSetBypassInsomnia();
	}

	static {
		for (Player player : OnlinePlayers.getAll())
			update(Nerd.of(player));
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		update(Nerd.of(event.getPlayer()));
	}

	@EventHandler
	public void on(VanishToggleEvent event) {
		update(Nerd.of(event.getPlayer()));
	}

	@EventHandler
	public void on(PlayerGameModeChangeEvent event) {
		update(Nerd.of(event.getPlayer()));
	}

	@EventHandler
	public void on(GodmodeEvent event) {
		update(Nerd.of(event.getUser()));
	}

	@EventHandler
	public void on(AFKEvent event) {
		update(event.getUser().getNerd());
	}

}
