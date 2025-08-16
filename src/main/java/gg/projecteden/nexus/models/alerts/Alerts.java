package gg.projecteden.nexus.models.alerts;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity(value = "alerts", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Alerts implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Highlight> highlights = new ArrayList<>();
	private List<PublicChannel> channels = new ArrayList<>();

	public boolean add(String highlight) {
		return add(highlight, true, false);
	}

	public boolean add(String highlight, boolean partialMatching, boolean negated) {
		if (!has(highlight)) {
			highlights.add(new Highlight(highlight, partialMatching, negated));
			sort();
			return true;
		}
		return false;
	}

	public boolean delete(Highlight highlight) {
		final boolean remove = highlights.remove(highlight);
		sort();
		return remove;
	}

	public boolean delete(String highlight) {
		if (has(highlight)) {
			delete(get(highlight).get());
			return true;
		}
		return false;
	}

	private void sort() {
		Collections.sort(highlights);
	}

	public boolean has(String highlight) {
		return get(highlight).isPresent();
	}

	public Optional<Highlight> get(String highlight) {
		return highlights.stream().filter(_highlight -> _highlight.getHighlight().equalsIgnoreCase(highlight)).findFirst();
	}

	public void clear() {
		highlights.clear();
	}

	public void playSound() {
		if (isOnline())
			Jingle.PING.play(getOnlinePlayer());
	}

	public void tryAlerts(ChatEvent event) {
		if (event.getChannel() instanceof PublicChannel publicChannel)
			if (channels.contains(publicChannel)) {
				playSound();
				return;
			}

		var message = event.getMessage();
		for (Highlight highlight : getHighlights())
			if (highlight.isNegated())
				message = highlight.removeNegate(message);

		for (Highlight highlight : getHighlights())
			if (highlight.test(message)) {
				playSound();
				break;
			}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Highlight implements Comparable<Highlight> {
		@NonNull
		private String highlight;
		private boolean partialMatching;
		private boolean negated;

		@Override
		public int compareTo(Highlight other) {
			return highlight.compareTo(other.getHighlight());
		}

		public boolean test(String message) {
			if (partialMatching) {
				return message.toLowerCase().contains(highlight.toLowerCase());
			} else {
				String _message = message;
				// Allow partial matching to work with special chars (ie quotes)
				if (highlight.replaceAll("[\\w ]+", "").length() == 0)
					_message = message.replaceAll("[^\\w ]+", " ");

				return (" " + _message + " ").toLowerCase().contains(" " + highlight.toLowerCase() + " ");
			}
		}

		public String removeNegate(String message) {
			if (partialMatching) {
				return message.toLowerCase().replaceAll(highlight.toLowerCase(), "");
			} else {
				String _message = message;
				// Allow partial matching to work with special chars (ie quotes)
				if (highlight.replaceAll("[\\w ]+", "").length() == 0)
					_message = message.replaceAll("[^\\w ]+", " ");
				_message = " " + _message + " ";

				return _message.toLowerCase().replaceAll(" " + highlight.toLowerCase() + " ", "");
			}
		}
	}

}
