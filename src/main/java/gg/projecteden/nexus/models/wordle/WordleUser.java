package gg.projecteden.nexus.models.wordle;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.Extensions.isNullOrEmpty;

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
	private List<LocalDate> wonDates = new ArrayList<>();

	public WordleGame get(LocalDate date) {
		return games.computeIfAbsent(date, $ -> new WordleGame(date));
	}

	public void playedOn(LocalDate today) {
		if (!playedDates.contains(today))
			playedDates.add(today);
	}

	public void wonOn(LocalDate today) {
		if (!wonDates.contains(today))
			wonDates.add(today);
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

			return guesses.contains(getSolution());
		}

		private String getSolution() {
			return new WordleConfigService().get0().get(date).getSolution();
		}

		private boolean isMaxGuesses() {
			return guesses.size() == 6;
		}

		@SuppressWarnings("deprecation")
		public @NotNull List<WordleLetter> getColoredGuess(String guess) {
			var solution = getSolution();
			var solution2 = new ArrayList<>(List.of(solution.toLowerCase().split("")));
			var guess2 = new ArrayList<>(List.of(guess.toLowerCase().split("")));

			List<WordleLetter> letters = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				var color = ChatColor.DARK_GRAY;
				var solutionChar = solution2.get(i);
				var guessChar = guess2.get(i);
				if (solutionChar.equals(guessChar)) {
					color = ChatColor.DARK_GREEN;
					solution2.set(i, "_");
				}
				letters.add(new WordleLetter(color, guessChar));
			}

			for (int i = 0; i < 5; i++) {
				var guessChar = guess2.get(i);
				var letter = letters.get(i);
				if (letter.getColor() == ChatColor.DARK_GREEN)
					continue;

				if (solution2.contains(guessChar)) {
					letters.set(i, new WordleLetter(ChatColor.GOLD, guessChar));
					solution2.set(solution2.indexOf(guessChar), "_");
				}
			}
			return letters;
		}
	}

	@Data
	@SuppressWarnings("deprecation")
	public static class WordleLetter {
		private ChatColor color;
		private String letter;
		private String emoji = "爅";

		public static final String LARGE_BOX_EMOJI = "爅";
		public static final String SMALL_BOX_EMOJI = "焯"; // For tooltips

		public WordleLetter(ChatColor color, String letter) {
			this.color = color;
			this.letter = letter;
		}

		public static String BEFORE = InventoryTexture.minus(13);
		public static String AFTER = InventoryTexture.minus(0);
		public static String BEFORE_I = InventoryTexture.minus(12);
		public static String AFTER_I = " " + InventoryTexture.minus(5);

		@Override
		public String toString() {
			var letter = isNullOrEmpty(this.letter) ? "" : this.letter.toUpperCase();
			String minusBefore = isNullOrEmpty(letter) ? "" : "I".equals(letter) ? BEFORE_I : BEFORE;
			String minusAfter = isNullOrEmpty(letter) ? "" : "I".equals(letter) ? AFTER_I : AFTER;
			if (SMALL_BOX_EMOJI.equals(emoji))
				minusBefore = InventoryTexture.minus(5);
			return color + emoji + minusBefore + ChatColor.WHITE + letter + minusAfter;
		}
	}
}
