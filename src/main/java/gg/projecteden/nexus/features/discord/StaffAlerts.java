package gg.projecteden.nexus.features.discord;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Unused

public class StaffAlerts implements Listener {
	@Getter
	static Map<Player, LocalDateTime> tracking = new HashMap<>();

	public StaffAlerts() {
		Nexus.registerListener(this);

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
	public void notAFK(NotAFKEvent event){
		readyToStalk(event.getPlayer().getPlayer(), "afk");
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
		if (Rank.of(player).isMod()) {
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
