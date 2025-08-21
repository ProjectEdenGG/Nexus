package gg.projecteden.nexus.features.discord;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Unused

public class StaffAlerts implements Listener {
	private static final Map<UUID, LocalDateTime> TRACKING = new HashMap<>();

	public StaffAlerts() {
		Nexus.registerListener(this);

		Tasks.repeat(0, TickTime.SECOND.x(30), () -> {
			Set<UUID> trackedPlayers = TRACKING.keySet();
			for (UUID uuid : trackedPlayers) {
				LocalDateTime then = TRACKING.get(uuid);
				LocalDateTime now = LocalDateTime.now();
				if (then.isBefore(now.minusMinutes(10)))
					TRACKING.remove(uuid);
			}
		});
	}

	@EventHandler
	public void notAFK(NotAFKEvent event){
		readyToStalk(event.getUser().getOnlinePlayer(), "afk");
	}

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()) return;
		if (AFK.getActiveStaff() != 0) return;

		String playerName = event.getPlayer().getName();
		Discord.staffAlerts("Boop! New player (" + playerName + "). Anyone @here free?");
		TRACKING.putIfAbsent(event.getPlayer().getUniqueId(), LocalDateTime.now());
	}

	private void readyToStalk(Player player, String type) {
		if (Rank.of(player).isMod()) {
			boolean alert = false;
			Set<UUID> trackedPlayers = TRACKING.keySet();
			for (UUID uuid : trackedPlayers) {
				var tracked = Bukkit.getPlayer(uuid);
				if (tracked != null && tracked.isOnline())
					alert = true;
				TRACKING.remove(uuid);
			}

			if (alert) {
				String staffName = player.getName();
				if (type.equalsIgnoreCase("join"))
					Discord.staffAlerts(staffName + " logged in to watch the new kids. Thank you <3");
				else if (type.equalsIgnoreCase("afk"))
					Discord.staffAlerts(staffName + " came back from being afk to watch the new kids. Thank you <3");
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (TRACKING.containsKey(event.getPlayer().getUniqueId())) {
			String playerName = event.getPlayer().getName();
			Discord.staffAlerts(playerName + " logged back in.");
		}
		readyToStalk(event.getPlayer(), "join");

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (TRACKING.containsKey(event.getPlayer().getUniqueId())) {
			String playerName = event.getPlayer().getName();
			Discord.staffAlerts(playerName + " logged out.");
		}
	}
}
