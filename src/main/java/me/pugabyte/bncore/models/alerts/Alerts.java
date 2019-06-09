package me.pugabyte.bncore.models.alerts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Alerts {
	@NonNull
	private String uuid;
	private List<Highlight> highlights;
	@Transient
	private boolean muted;

	public Alerts(String uuid, List<Highlight> highlights) {
		this.uuid = uuid;
		this.highlights = highlights;
		this.muted = false;
	}

	public boolean add(String highlight) {
		return add(highlight, true);
	}

	public boolean add(String highlight, boolean partialMatching) {
		if (!has(highlight)) {
			highlights.add(new Highlight(uuid, highlight, partialMatching));
			sort();
			return true;
		}
		return false;
	}

	public boolean delete(String highlight) {
		if (has(highlight)) {
			highlights.remove(get(highlight).get());
			sort();
			return true;
		}
		return false;
	}

	private void sort() {
		Collections.sort(highlights);
	}

	public boolean has(String highlight) {
		if (highlights == null) {
			highlights = new ArrayList<>();
		}

		return get(highlight).isPresent();
	}

	public Optional<Highlight> get(String highlight) {
		return highlights.stream().filter(_highlight -> _highlight.getHighlight().equalsIgnoreCase(highlight)).findFirst();
	}

	public void clear() {
		highlights = new ArrayList<>();
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public void tryAlerts(String message) {
		Player player = Bukkit.getPlayer(UUID.fromString(uuid));

		if (message.toLowerCase().contains(player.getName().toLowerCase())) {
			playSound();
			return;
		}

		for (Alerts.Highlight highlight : getHighlights()) {
			if (highlight.isPartialMatching()) {
				if (message.toLowerCase().contains(highlight.getHighlight().toLowerCase())) {
					playSound();
					break;
				}
			} else {
				String _message = message;
				if (highlight.getHighlight().replaceAll("[a-zA-Z0-9 ]+", "").length() == 0) {
					_message = message.replaceAll("[^a-zA-Z0-9 ]+", " ");
				}

				if ((" " + _message + " ").toLowerCase().contains(" " + highlight.getHighlight().toLowerCase() + " ")) {
					playSound();
					break;
				}
			}
		}
	}

	public void playSound() {
		if (!isMuted()) {
			Player player = Bukkit.getPlayer(UUID.fromString(uuid));
			player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Table(name = "alerts")
	public static class Highlight implements Comparable<Highlight> {
		public String uuid;
		public String highlight;
		public boolean partialMatching;

		@Override
		public int compareTo(Highlight other) {
			return highlight.compareTo(other.getHighlight());
		}

	}

}



