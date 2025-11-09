package gg.projecteden.nexus.models.wordle;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter.WordleLetterState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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
				var state = WordleLetterState.NO_POSITION;
				var solutionChar = solution2.get(i);
				var guessChar = guess2.get(i);
				if (!"_".equals(guessChar))
					if (solutionChar.equals(guessChar)) {
						state = WordleLetterState.CORRECT_POSITION;
						solution2.set(i, "_");
					}
				letters.add(new WordleLetter(state, guessChar));
			}

			for (int i = 0; i < 5; i++) {
				var guessChar = guess2.get(i);
				var letter = letters.get(i);
				if (letter.getState() == WordleLetterState.CORRECT_POSITION)
					continue;

				if (solution2.contains(guessChar) && !"_".equals(guessChar)) {
					letters.set(i, new WordleLetter(WordleLetterState.WRONG_POSITION, guessChar));
					solution2.set(solution2.indexOf(guessChar), "_");
				}
			}
			return letters;
		}
	}

	@Data
	@SuppressWarnings("deprecation")
	public static class WordleLetter {
		private WordleLetterState state;
		private String letter;
		private String emoji = "爅";

		public static final String LARGE_BOX_EMOJI = "爅";
		public static final String SMALL_BOX_EMOJI = "焯"; // For tooltips

		public WordleLetter(WordleLetterState state, String letter) {
			this.state = state;
			this.letter = letter;
		}

		public static String BEFORE = InventoryTexture.minus(13);
		public static String AFTER = InventoryTexture.minus(0);
		public static String BEFORE_I = InventoryTexture.minus(12);
		public static String AFTER_I = " " + InventoryTexture.minus(5);
		public static String BEFORE_SMALL = InventoryTexture.minus(5);
		public static String AFTER_SMALL = AFTER;
		public static String BEFORE_TRANSPARENT = BEFORE_I;
		public static String AFTER_TRANSPARENT = AFTER_I;

		@Override
		public String toString() {
			var letter = this.letter == null ? "" : this.letter.toUpperCase();
			var minusBefore = BEFORE;
			var minusAfter = AFTER;

			if (isNullOrEmpty(letter)) {
				minusBefore = "";
				minusAfter = "";
			}

			if ("I".equals(letter)) {
				minusBefore = BEFORE_I;
				minusAfter = AFTER_I;
			}

			if (" ".equals(letter)) {
				minusBefore = BEFORE_TRANSPARENT;
				minusAfter = AFTER_TRANSPARENT;
			}

			if (SMALL_BOX_EMOJI.equals(emoji)) {
				minusBefore = BEFORE_SMALL;
				minusAfter = AFTER;
			}

			return state.getColor() + emoji + minusBefore + ChatColor.WHITE + letter + minusAfter;
		}

		@Getter
		@RequiredArgsConstructor
		public enum WordleLetterState {
			FAILED(ChatColor.RED, null),
			UNUSED(ChatColor.GRAY, null),
			NO_POSITION(ChatColor.DARK_GRAY, WordleSound.NO_POSITION),
			WRONG_POSITION(ChatColor.GOLD, WordleSound.WRONG_POSITION),
			CORRECT_POSITION(ChatColor.DARK_GREEN, WordleSound.CORRECT_POSITION),
			;

			private final ChatColor color;
			private final WordleSound sound;

			public void playSound() {
				if (sound != null)
					sound.playSound();
			}
		}
	}

	public enum WordleSound {
		FAIL,
		SUCCESS,
		NO_POSITION,
		WRONG_POSITION,
		CORRECT_POSITION,
		;

		public void playSound() {}
	}
}
