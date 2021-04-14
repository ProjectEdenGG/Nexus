package me.pugabyte.nexus.features.listeners;

import dev.morphia.query.Sort;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;

public class NerdListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(event.getPlayer());

		nerd.setLastJoin(LocalDateTime.now());
		nerd.getPastNames().add(event.getPlayer().getName());

		if (nerd.getLastQuit() != null && nerd.getLastQuit().isBefore(nerd.getLastJoin()))
			nerd.setLastQuit(LocalDateTime.now().minusMinutes(1));

		service.save(nerd);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(event.getPlayer());

		nerd.setLastQuit(LocalDateTime.now());
		nerd.getPastNames().add(event.getPlayer().getName());

		service.save(nerd);
	}

	static {
		Tasks.repeatAsync(0, Time.MINUTE, () -> {
			for (Nerd recentJoin : new NerdService().<Nerd>getAllSortedByLimit(200, Sort.descending("lastJoin")))
				if (!recentJoin.isOnline() && recentJoin.getNerd().getLastQuit() != null && recentJoin.getLastQuit().isBefore(recentJoin.getLastJoin()))
					recentJoin.setLastQuit(LocalDateTime.now());
		});
	}

}
