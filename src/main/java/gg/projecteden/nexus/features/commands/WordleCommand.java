package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.wordle.WordleConfig;
import gg.projecteden.nexus.models.wordle.WordleConfigService;
import gg.projecteden.nexus.models.wordle.WordleUser;
import gg.projecteden.nexus.models.wordle.WordleUser.WordleGame;
import gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter;
import gg.projecteden.nexus.models.wordle.WordleUserService;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.Extensions.isNullOrEmpty;
import static java.util.stream.Collectors.joining;

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
	void wordle(
		@Switch int before,
		@Switch int after,
		@Switch int beforeI,
		@Switch int afterI
	) {
		if (before != 0)
			WordleLetter.BEFORE = InventoryTexture.minus(before);
		if (after != 0)
			WordleLetter.AFTER = InventoryTexture.minus(after);
		if (beforeI != 0)
			WordleLetter.BEFORE_I = InventoryTexture.minus(beforeI);
		if (afterI != 0)
			WordleLetter.AFTER_I = " " + InventoryTexture.minus(afterI);

		var today = new GeoIPService().get(player()).getCurrentTime().toLocalDate();
		if (userService.get(player()).get(today).isComplete())
			new WordleResultsMenu(today).open(player());
		else
			new WordleMenu(today).open(player());
	}

	@RequiredArgsConstructor
	public static class WordleResultsMenu {
		private final LocalDate date;

		public void open(Player player) {
			var users = userService.getAll().stream()
				.filter(user -> user.get(date).isComplete())
				.sorted(Comparator.comparing(user -> user.get(date).getGuesses().size()))
				.toList();

			var dialog = new DialogBuilder()
				.title("Wordle #" + config.get(date).getDaysSinceLaunch() + " | " + date.format(formatter));

			for (WordleUser user : users) {
				var head = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(user);
				dialog.bodyItem(head.build(), " " + Nerd.of(user).getColoredName());
				for (String guess : user.get(date).getGuesses()) {
					var letters = user.get(date).getColoredGuess(guess).stream().peek(letter -> letter.setLetter(null));
					dialog.bodyText(letters.map(WordleUser.WordleLetter::toString).collect(joining(" ")));
				}
				dialog.bodyText("");
			}

			dialog.multiAction()
				.columns(1)
				.button("Back to puzzle", click -> new WordleMenu(date).open(player))
				.button("Go to archive", click -> new WordleArchiveMenu(YearMonth.from(date)).open(player))
				.open(player);
		}
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

			dialog.bodyText("");

			for (var guess : game.getGuesses()) {
				dialog.bodyText(game.getColoredGuess(guess)
					.stream()
					.map(WordleUser.WordleLetter::toString)
					.collect(joining(" ")));
			}

			if (game.isFailed()) {
				dialog.bodyText(Arrays.stream(solution.toUpperCase().split(""))
					.map(letter -> new WordleLetter(ChatColor.RED, letter).toString())
					.collect(joining(" ")));
			}

			dialog.bodyText("");

			for (String row : KEYBOARD){
				dialog.bodyText(getKeyboardColors(row, game, solution)
					.stream()
					.map(WordleUser.WordleLetter::toString)
					.collect(joining(" ")));
			}

			if (!game.isComplete())
				dialog.inputText("answer", errorMessage != null ? errorMessage : "");

			var builder = dialog.multiAction().columns(1);
			if (game.isComplete())
				builder
					.button("See server results", click -> new WordleResultsMenu(date).open(player))
					.button("Go to archive", click -> new WordleArchiveMenu(YearMonth.from(date)).open(player));
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

					if (input.equalsIgnoreCase(solution)) {
						var today = new GeoIPService().get(player).getCurrentTime().toLocalDate();
						if (today.equals(date))
							user.playedOn(today);
					}

					refresh(player);
				})
				.button("Go to archive", click -> new WordleArchiveMenu(YearMonth.from(date)).open(player));

			builder.open(player);
			configService.save(config);
			userService.save(user);
		}

		private static @NotNull List<WordleLetter> getKeyboardColors(String keysInRow, WordleGame game, String solution) {
			Map<String, ChatColor> keys = new HashMap<>();
			for (String letter : keysInRow.toLowerCase().split(""))
				keys.put(letter, ChatColor.GRAY);

			for (String guess : game.getGuesses()) {
				var solution2 = new ArrayList<>(List.of(solution.toLowerCase().split("")));
				var guess2 = new ArrayList<>(List.of(guess.toLowerCase().split("")));
				for (int i = 0; i < 5; i++) {
					var guessChar = guess2.get(i).toLowerCase();
					var solutionChar = solution2.get(i).toLowerCase();

					var color = keys.getOrDefault(guessChar, ChatColor.GRAY);
					if (color == ChatColor.DARK_GREEN)
						continue;

					if (guessChar.equals(solutionChar)) {
						color = ChatColor.DARK_GREEN;
						guess2.set(i, "_");
						solution2.set(i, "_");
					} else if (solution2.contains(guessChar))
						color = ChatColor.GOLD;
					else if (color != ChatColor.GOLD)
						color = ChatColor.DARK_GRAY;

					keys.put(guessChar, color);
				}
			}

			List<WordleLetter> letters = new ArrayList<>();
			for (String letter : keysInRow.toLowerCase().split(""))
				letters.add(new WordleLetter(keys.getOrDefault(letter, ChatColor.GRAY), letter));
			return letters;
		}

	}

	@RequiredArgsConstructor
	public static class WordleArchiveMenu {
		private YearMonth yearMonth = YearMonth.now();
		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

		public WordleArchiveMenu(YearMonth yearMonth) {
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
					color = ChatColor.DARK_GREEN;
				else if (user.get(current).isFailed())
					color = ChatColor.RED;
				else if (user.get(current).isStarted())
					color = ChatColor.GOLD;

				LocalDate date = current;
				ChatColor finalColor = color;
				dialog.button(
					color + String.valueOf(current.getDayOfMonth()),
					"#" + config.getDaysSinceLaunch(current),
					click -> {
						if (finalColor != ChatColor.DARK_GRAY) {
							if (user.get(date).isComplete())
								new WordleResultsMenu(date).open(player);
							else
								new WordleMenu(date).open(player);
						}
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
