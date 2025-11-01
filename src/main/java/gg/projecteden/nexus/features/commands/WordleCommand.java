package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.wordle.WordleConfig;
import gg.projecteden.nexus.models.wordle.WordleConfigService;
import gg.projecteden.nexus.models.wordle.WordleUser.WordleGame;
import gg.projecteden.nexus.models.wordle.WordleUserService;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import kotlin.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Extensions.isNullOrEmpty;

@SuppressWarnings("deprecation")
@Environments({Env.TEST, Env.UPDATE})
public class WordleCommand extends CustomCommand {
	private static final List<String> KEYBOARD = List.of("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM");
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private static final WordleConfigService configService = new WordleConfigService();
	private static final WordleConfig config = configService.get0();
	private static final WordleUserService userService = new WordleUserService();

	public WordleCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Async
	void wordle() {
		var today = new GeoIPService().get(player()).getCurrentTime().toLocalDate();
		userService.edit(player(), user -> user.playedOn(today));
		new WordleMenu(today).open(player());
	}

	@RequiredArgsConstructor
	public static class WordleMenu {
		private final LocalDate date;
		private String errorMessage;

		public void refresh(Player player) {
			userService.save(userService.get(player));
			open(player);
		}

		public void open(Player player) {
			var user = userService.get(player);
			var gameConfig = config.get(date);
			var game = user.get(date);
			var solution = gameConfig.getSolution().toUpperCase();

			var dialog = new DialogBuilder()
				.title("Wordle #" + gameConfig.getDaysSinceLaunch() + " | " + date.format(formatter));

			for (var guess : game.getGuesses())
				dialog.bodyText(String.join(" ", getColoredGuesses(guess, solution)));

			if (game.isFailed())
				dialog.bodyText("&c" + String.join(" ", solution.toUpperCase().split("")));

			dialog.bodyText("");

			for (String row : KEYBOARD)
				dialog.bodyText(String.join(" ", getKeyboardColors(row, game, solution)));

			if (!game.isComplete())
				dialog.inputText("answer", errorMessage != null ? errorMessage : "");

			var builder = dialog.multiAction().columns(1);
			if (game.isComplete())
				builder.button("Go to archive", click -> new WorldArchiveMenu(YearMonth.from(date)).open(player));
			else
				builder.button("Submit", click -> {
					var input = click.getText("answer");
					if (isNullOrEmpty(input))
						errorMessage = "&cGuess a word";
					else if (!config.getAllowedWords().contains(input.toLowerCase()))
						errorMessage = "&cWord not in dictionary";
					else if (game.getGuesses().contains(input.toLowerCase()))
						errorMessage = "&cYou already guessed that word";
					else {
						errorMessage = null;
						game.getGuesses().add(input.toLowerCase());
					}

					refresh(player);
				})
				.button("Go to archive", click -> new WorldArchiveMenu(YearMonth.from(date)).open(player));

			builder.open(player);
			configService.save(config);
			userService.save(user);
		}

		private static @NotNull List<String> getKeyboardColors(String keysInRow, WordleGame game, String solution) {
			Map<String, ChatColor> keys = new HashMap<>();
			for (String letter : keysInRow.toLowerCase().split(""))
				keys.put(letter, ChatColor.WHITE);

			for (String guess : game.getGuesses()) {
				var solution2 = new ArrayList<>(List.of(solution.toLowerCase().split("")));
				var guess2 = new ArrayList<>(List.of(guess.toLowerCase().split("")));
				for (int i = 0; i < 5; i++) {
					var guessChar = guess2.get(i).toLowerCase();
					var solutionChar = solution2.get(i).toLowerCase();

					var color = keys.getOrDefault(guessChar, ChatColor.WHITE);
					if (color == ChatColor.GREEN)
						continue;

					if (guessChar.equals(solutionChar)) {
						color = ChatColor.GREEN;
						guess2.set(i, "_");
						solution2.set(i, "_");
					} else if (solution2.contains(guessChar))
						color = ChatColor.YELLOW;
					else if (color != ChatColor.YELLOW)
						color = ChatColor.DARK_GRAY;

					keys.put(guessChar, color);
				}
			}

			List<String> coloredKeys = new ArrayList<>();
			for (String letter : keysInRow.toLowerCase().split(""))
				coloredKeys.add(keys.get(letter).toString() + letter.toUpperCase());
			return coloredKeys;
		}

		private static @NotNull List<String> getColoredGuesses(String guess, String solution) {
			var solution2 = new ArrayList<>(List.of(solution.toLowerCase().split("")));
			var guess2 = new ArrayList<>(List.of(guess.toLowerCase().split("")));

			List<Pair<String, ChatColor>> letters = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				var color = ChatColor.DARK_GRAY;
				var solutionChar = solution2.get(i);
				var guessChar = guess2.get(i);
				if (solutionChar.equals(guessChar)) {
					color = ChatColor.GREEN;
					solution2.set(i, "_");
				}
				letters.add(new Pair<>(guessChar, color));
			}

			for (int i = 0; i < 5; i++) {
				var guessChar = guess2.get(i);
				var color = letters.get(i);
				if (color.getSecond() == ChatColor.GREEN)
					continue;

				if (solution2.contains(guessChar)) {
					letters.set(i, new Pair<>(guessChar, ChatColor.YELLOW));
					solution2.set(solution2.indexOf(guessChar), "_");
				}
			}

			List<String> guessColored = new ArrayList<>();
			for (Pair<String, ChatColor> letter : letters)
				guessColored.add(letter.getSecond() + letter.getFirst().toUpperCase());
			return guessColored;
		}

	}

	@RequiredArgsConstructor
	public static class WorldArchiveMenu {
		private YearMonth yearMonth = YearMonth.now();
		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

		public WorldArchiveMenu(YearMonth yearMonth) {
			this.yearMonth = yearMonth;
		}

		public void open(Player player) {
			var user = userService.get(player);
			var geoip = new GeoIPService().get(player);
			var dialog = new DialogBuilder()
				.title(yearMonth.format(FORMATTER))
				.multiAction()
				.columns(7)
				.defaultButtonWidth(30);

			List<DayOfWeek> week = geoip.getWeek();
			for (DayOfWeek dayOfWeek : week)
				dialog.button(String.valueOf(dayOfWeek.name().charAt(0)));

			var initial = yearMonth.atDay(1);
			while (initial.getDayOfWeek() != week.getFirst())
				initial = initial.minusDays(1);

			var last = yearMonth.atEndOfMonth();
			while (last.getDayOfWeek() != week.getLast())
				last = last.plusDays(1);

			last = last.plusDays(1);

			var today = new GeoIPService().get(player).getCurrentTime().toLocalDate();
			var current = initial;
			while (current.isBefore(last)) {
				var color = ChatColor.WHITE;
				if (current.isBefore(WordleConfig.EPOCH))
					color = ChatColor.DARK_GRAY;
				else if (current.isAfter(today))
					color = ChatColor.DARK_GRAY;
				else if (current.getMonth() != yearMonth.getMonth())
					color = ChatColor.DARK_GRAY;
				else if (user.get(current).isSuccess())
					color = ChatColor.GREEN;
				else if (user.get(current).isFailed())
					color = ChatColor.RED;
				else if (user.get(current).isStarted())
					color = ChatColor.YELLOW;

				LocalDate date = current;
				ChatColor finalColor = color;
				dialog.button(
					color + String.valueOf(current.getDayOfMonth()),
					"#" + config.getDaysSinceLaunch(current),
					click -> {
						if (finalColor != ChatColor.DARK_GRAY)
							new WordleMenu(date).open(player);
				});
				current = current.plusDays(1);
			}

			YearMonth previous = yearMonth.minusMonths(1);
			dialog.button("< " + FORMATTER.format(previous), 107,click -> {
				yearMonth = previous;
				open(player);
			});

			YearMonth next = yearMonth.plusMonths(1);
			dialog.button(FORMATTER.format(next) + " >", 107, click -> {
				yearMonth = next;
				open(player);
			});

			dialog.open(player);
		}

	}
}
