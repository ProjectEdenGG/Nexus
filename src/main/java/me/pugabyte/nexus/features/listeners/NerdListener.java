package me.pugabyte.nexus.features.listeners;

import dev.morphia.query.Sort;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.utils.Name;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;

public class NerdListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(event.getUniqueId());

		nerd.setLastJoin(LocalDateTime.now());
		nerd.setName(event.getName());
		nerd.getPastNames().add(event.getName());

		if (nerd.getLastQuit() != null && nerd.getLastQuit().isBefore(nerd.getLastJoin()))
			nerd.setLastQuit(LocalDateTime.now().minusMinutes(1));

		service.save(nerd);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(event.getPlayer());

		nerd.setLoginLocation(event.getPlayer().getLocation());

		service.save(nerd);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(event.getPlayer());

		nerd.setLastQuit(LocalDateTime.now());
		nerd.getPastNames().add(Name.of(event.getPlayer()));

		service.save(nerd);
	}

	static {
		Tasks.repeatAsync(0, Time.MINUTE, () -> {
			for (Nerd recentJoin : new NerdService().getAllSortedByLimit(200, Sort.descending("lastJoin")))
				if (!recentJoin.isOnline() && recentJoin.getNerd().getLastQuit() != null && recentJoin.getLastQuit().isBefore(recentJoin.getLastJoin()))
					recentJoin.setLastQuit(LocalDateTime.now());
		});
	}

}
