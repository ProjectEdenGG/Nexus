package me.pugabyte.bncore.models.alerts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.utils.Jingles;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Alerts {
	private String uuid;
	private List<Highlight> highlights;
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
		Player player = (Player) Utils.getPlayer(uuid);

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
		if (!isMuted())
			Jingles.ping(Utils.getPlayer(uuid).getPlayer());
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Table(name = "alerts")
	public static class Highlight implements Comparable<Highlight> {
		@Id
		@NonNull
		private String uuid;
		@NonNull
		private String highlight;
		@NonNull
		private boolean partialMatching;

		@Override
		public int compareTo(Highlight other) {
			return highlight.compareTo(other.getHighlight());
		}

	}

}
