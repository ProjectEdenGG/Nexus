package me.pugabyte.bncore.features.discord;

import lombok.Getter;
import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StaffAlerts implements Listener {
	@Getter
	static Map<Player, LocalDateTime> tracking = new HashMap<>();

	public StaffAlerts() {
		Tasks.repeat(0, Time.SECOND.x(30), () -> {
			Set<Player> trackedPlayers = tracking.keySet();
			for (Player tracked : trackedPlayers) {
				LocalDateTime then = tracking.get(tracked);
				LocalDateTime now = LocalDateTime.now();
				if (then.isBefore(now.minusMinutes(10)))
					tracking.remove(tracked);
			}
		});
	}

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()) return;
		if (AFK.getActiveStaff() != 0) return;

		String playerName = event.getPlayer().getName();
		Discord.staffAlerts("Boop! New player (" + playerName + "). Anyone @here free?");
		tracking.putIfAbsent(event.getPlayer(), LocalDateTime.now());
	}

	private void readyToStalk(Player player, String type) {
		if (player.hasPermission("group.moderator")) {
			boolean alert = false;
			Set<Player> trackedPlayers = tracking.keySet();
			for (Player tracked : trackedPlayers) {
				if (tracked.isOnline())
					alert = true;
				tracking.remove(tracked);
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
		if (tracking.containsKey(event.getPlayer())) {
			String playerName = event.getPlayer().getName();
			Discord.staffAlerts(playerName + " logged back in.");
		}
		readyToStalk(event.getPlayer(), "join");

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (tracking.containsKey(event.getPlayer())) {
			String playerName = event.getPlayer().getName();
			Discord.staffAlerts(playerName + " logged out.");
		}
	}
}
