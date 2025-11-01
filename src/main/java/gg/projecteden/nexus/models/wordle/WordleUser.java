package gg.projecteden.nexus.models.wordle;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Entity(value = "wordle_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class WordleUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<LocalDate, WordleGame> games = new ConcurrentHashMap<>();
	private List<LocalDate> playedDates = new ArrayList<>();

	public WordleGame get(LocalDate date) {
		return games.computeIfAbsent(date, $ -> new WordleGame(date));
	}

	public void playedOn(LocalDate today) {
		if (!playedDates.contains(today))
			playedDates.add(today);
	}

	@Data
	@NoArgsConstructor
	public static class WordleGame {
		private LocalDate date;
		private List<String> guesses = new ArrayList<>();

		public WordleGame(LocalDate date) {
			this.date = date;
		}

		public boolean isComplete() {
			return isMaxGuesses() || isSolved();
		}

		public boolean isSuccess() {
			return isStarted() && isSolved();
		}

		public boolean isFailed() {
			return isStarted() && isMaxGuesses() && !isSolved();
		}

		public boolean isStarted() {
			return !guesses.isEmpty();
		}

		private boolean isSolved() {
			if (!isStarted())
				return false;

			String solution = new WordleConfigService().get0().get(date).getSolution();
			return guesses.contains(solution);
		}

		private boolean isMaxGuesses() {
			return guesses.size() == 6;
		}
	}

}
