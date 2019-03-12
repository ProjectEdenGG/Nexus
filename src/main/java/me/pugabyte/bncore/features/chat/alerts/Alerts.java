package me.pugabyte.bncore.features.chat.alerts;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.alerts.models.AlertsDatabase;
import me.pugabyte.bncore.features.chat.alerts.models.AlertsPlayer;
import me.pugabyte.bncore.features.chat.alerts.models.Highlight;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Alerts {
	private static Map<String, AlertsPlayer> players = new HashMap<>();

	public Alerts() {
		new AlertsListener();
		new AlertsCommand();

		AlertsDatabase.HighlightsReader highlightsReader = new AlertsDatabase.HighlightsReader();

		Map<String, List<Highlight>> highlights = (Map<String, List<Highlight>>) highlightsReader.read();

		for (Map.Entry<String, List<Highlight>> entry : highlights.entrySet()) {
			String uuid = entry.getKey();
			players.put(uuid, new AlertsPlayer(uuid, entry.getValue(), false));
		}

		BNCore.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), Alerts::write, 600L, 600L);
	}

	public static void write() {
		AlertsDatabase.HighlightsWriter highlightsWriter = new AlertsDatabase.HighlightsWriter();

		for (AlertsPlayer alertsPlayer : players.values()) {
			if (alertsPlayer.isDirty()) {
				highlightsWriter.write(alertsPlayer);
				alertsPlayer.setDirty(false);
			}
		}
	}

	public AlertsPlayer getPlayer(Player player) {
		return getPlayer(player.getUniqueId().toString());
	}

	public AlertsPlayer getPlayer(String uuid) {
		if (!players.containsKey(uuid)) {
			players.put(uuid, new AlertsPlayer(uuid));
		}
		return players.get(uuid);
	}

	void playSound(Player player) {
		if (!getPlayer(player).isMuted()) {
			player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
		}
	}

	void tryAlerts(String message, Player player) {
		if (message.toLowerCase().contains(player.getName().toLowerCase())) {
			playSound(player);
		} else {
			for (Highlight highlight : getPlayer(player).getHighlights()) {
				if (highlight.isPartialMatching()) {
					if (message.toLowerCase().contains(highlight.get().toLowerCase())) {
						playSound(player);
						break;
					}
				} else {
					String _message = message;
					if (highlight.get().replaceAll("[a-zA-Z0-9 ]+", "").length() == 0) {
						_message = message.replaceAll("[^a-zA-Z0-9 ]+", " ");
					}

					if ((" " + _message + " ").toLowerCase().contains(" " + highlight.get().toLowerCase() + " ")) {
						playSound(player);
						break;
					}
				}
			}
		}
	}

}
