package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.wordle.WordleConfig;
import gg.projecteden.nexus.models.wordle.WordleConfigService;
import gg.projecteden.nexus.models.wordle.WordleUser;
import gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter;
import gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter.WordleLetterState;
import gg.projecteden.nexus.models.wordle.WordleUser.WordleSound;
import gg.projecteden.nexus.models.wordle.WordleUserService;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.MessageType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter.WordleLetterState.CORRECT_POSITION;
import static gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter.WordleLetterState.FAILED;
import static gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter.WordleLetterState.NO_POSITION;
import static gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter.WordleLetterState.UNUSED;
import static gg.projecteden.nexus.models.wordle.WordleUser.WordleLetter.WordleLetterState.WRONG_POSITION;
import static gg.projecteden.nexus.utils.Extensions.isNullOrEmpty;
import static java.util.stream.Collectors.joining;

/* TODO
	Prevent reload if dialog is open (good luck)

	My stats
		Total completed
		Average guesses needed
		Win rate
		Current streak
		Best streak

	Leaderboard
		Most completed
		Lowest average guesses needed
		Best win rate
		Best current streak
		Best all time streak
 */

@NoArgsConstructor
@SuppressWarnings("deprecation")
public class WordleCommand extends CustomCommand implements Listener {
	private static final List<String> KEYBOARD = List.of("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM");
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private static final WordleConfigService configService = new WordleConfigService();
	private static final WordleConfig config = configService.get0();
	private static final WordleUserService userService = new WordleUserService();

	private static int ANIMATION_DELAY = 6;

	public WordleCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[date]")
	@Async
	void wordle(LocalDate date) {
		var user = userService.get(player());
		var userZonedLocalDate = user.getZonedLocalDate();

		if (date == null)
			date = userZonedLocalDate;

		if (date.isAfter(userZonedLocalDate.plusDays(1)))
			error("Wordle #" + config.getDaysSinceLaunch(date) + " is not yet available");

		if (date.isBefore(WordleConfig.EPOCH))
			error("Wordle was created on " + WordleConfig.EPOCH.format(formatter) + " and cannot be played before that date");

		if (user.get(date).isComplete())
			new WordleResultsMenu(date).open(player());
		else
			new WordleMenu(date).open(player());
	}

	@Path("streak [user]")
	void streak(@Arg("self") WordleUser user) {
		int streak = user.getStreak();
		send(PREFIX + streak + StringUtils.plural(" day", streak));
	}

	@Path("average [user]")
	void average(@Arg("self") WordleUser user) {
		send(PREFIX + "Average guesses: " + StringUtils.getDf().format(user.getAverage()));
	}

	@Path("top streak [page]")
	void top_streak(@Arg("1") int page) {
		var users = userService.getAll().stream()
			.filter(user -> user.getStreak() > 1)
			.sorted(Comparator.comparing(WordleUser::getStreak).reversed())
			.toList();

		send(PREFIX + "Highest streaks");
		new Paginator<WordleUser>()
			.values(users)
			.formatter((user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getStreak() + " days"))
			.command("/wordle top streak")
			.page(page)
			.send();
	}

	@Path("top average [page]")
	void top_average(@Arg("1") int page) {
		var users = userService.getAll().stream()
			.filter(user -> user.getAverage() > 0)
			.sorted(Comparator.comparing(WordleUser::getAverage))
			.toList();

		send(PREFIX + "Best average number of guesses");
		new Paginator<WordleUser>()
			.values(users)
			.formatter((user, index) -> json(index + " &e" + user.getNickname() + " &7- " + StringUtils.getDf().format(user.getAverage()) + " guesses"))
			.command("/wordle top average")
			.page(page)
			.send();
	}

	@Path("results [date]")
	void results(LocalDate date) {
		var user = userService.get(player());
		var userZonedLocalDate = user.getZonedLocalDate();

		if (date == null)
			date = userZonedLocalDate;

		if (date.isAfter(userZonedLocalDate.plusDays(1)))
			error("Wordle #" + config.getDaysSinceLaunch(date) + " is not yet available");

		new WordleResultsMenu(date).open(player());
	}

	@Path("archive [yearMonth]")
	void archive(YearMonth yearMonth) {
		var user = userService.get(player());

		if (yearMonth == null)
			yearMonth = YearMonth.from(user.getZonedLocalDate());

		new WordleArchiveMenu(yearMonth).open(player());
	}

	@Path("setPlayedOnReleaseDay <date> [player] [state]")
	@Permission(Group.ADMIN)
	void setPlayedOnReleaseDay(LocalDate date, @Arg("self") WordleUser user, Boolean state) {
		if (state == null)
			state = !user.get(date).isPlayedOnReleaseDay();

		user.get(date).setPlayedOnReleaseDay(state);
		send(PREFIX + "Set played on release day to " + state + " for " + user.getNickname() + "'s puzzle #" + config.get(date).getDaysSinceLaunch());
	}

	@Path("setSolvedOnReleaseDay <date> [player] [state]")
	@Permission(Group.ADMIN)
	void setSolvedOnReleaseDay(LocalDate date, @Arg("self") WordleUser user, Boolean state) {
		if (state == null)
			state = !user.get(date).isSolvedOnReleaseDay();

		user.get(date).setSolvedOnReleaseDay(state);
		send(PREFIX + "Set solved on release day to " + state + " for " + user.getNickname() + "'s puzzle #" + config.get(date).getDaysSinceLaunch());
	}

	@Confirm
	@Path("delete <player> <date>")
	@Permission(Group.SENIOR_STAFF)
	void delete(WordleUser user, LocalDate date) {
		user.getGames().remove(date);
		userService.save(user);
		send(PREFIX + "Deleted " + user.getNickname() + "'s data for puzzle #" + config.get(date).getDaysSinceLaunch() + " (" + date.format(formatter) + ")");
	}

	@Path("config")
	@Permission(Group.ADMIN)
	void config(
		@Switch Integer before,
		@Switch Integer after,
		@Switch Integer beforeI,
		@Switch Integer afterI,
		@Switch Integer beforeSmall,
		@Switch Integer afterSmall,
		@Switch Integer beforeTransparent,
		@Switch Integer afterTransparent,
		@Switch Integer animationDelay,
		@Switch WordleSound wordleSound,
		@Switch String sound,
		@Switch float pitch
	) {
		if (before != null) {
			send("Updating WordleLetter.BEFORE to " + before);
			WordleLetter.BEFORE = InventoryTexture.minus(before);
		}
		if (after != null) {
			send("Updating WordleLetter.AFTER to " + after);
			WordleLetter.AFTER = InventoryTexture.minus(after);
		}
		if (beforeI != null) {
			send("Updating BEFORE_I to " + beforeI);
			WordleLetter.BEFORE_I = InventoryTexture.minus(beforeI);
		}
		if (afterI != null) {
			send("Updating AFTER_I to " + afterI);
			WordleLetter.AFTER_I = " " + InventoryTexture.minus(afterI);
		}
		if (beforeSmall != null) {
			send("Updating BEFORE_SMALL to " + beforeSmall);
			WordleLetter.BEFORE_SMALL = InventoryTexture.minus(beforeSmall);
		}
		if (afterSmall != null) {
			send("Updating AFTER_SMALL to " + afterSmall);
			WordleLetter.AFTER_SMALL = InventoryTexture.minus(afterSmall);
		}
		if (beforeTransparent != null) {
			send("Updating BEFORE_TRANSPARENT to " + beforeTransparent);
			WordleLetter.BEFORE_TRANSPARENT = InventoryTexture.minus(beforeTransparent);
		}
		if (afterTransparent != null) {
			send("Updating AFTER_TRANSPARENT to " + afterTransparent);
			WordleLetter.AFTER_TRANSPARENT = InventoryTexture.minus(afterTransparent);
		}
		if (animationDelay != null) {
			send("Updating ANIMATION_DELAY to " + animationDelay);
			ANIMATION_DELAY = animationDelay;
		}

		if (wordleSound != null && sound != null && pitch != 0)
			wordleSound.setConsumer(player -> player.playSound(player.getLocation(), sound, 1, pitch));
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
					dialog.bodyText(letters.map(WordleLetter::toString).collect(joining(" ")));
				}
				dialog.bodyText("");
			}

			dialog.multiAction()
				.columns(1)
				.button("Go to puzzle", click -> new WordleMenu(date).open(player))
				.button("Go to archive", click -> new WordleArchiveMenu(YearMonth.from(date)).open(player))
				.open(player);
		}
	}

	@RequiredArgsConstructor
	public static class WordleMenu {
		private final LocalDate date;
		private WordleUser user;
		private String errorMessage;
		private String input = "";

		public void open(Player player) {
			open(player, -1);
		}

		public void open(Player player, int animationStep) {
			if (user == null)
				user = userService.get(player);

			var gameConfig = config.get(date);
			var game = user.getOrCreate(date);
			var solution = gameConfig.getSolution().toUpperCase();

			var dialog = new DialogBuilder()
				.title("Wordle #" + gameConfig.getDaysSinceLaunch() + " | " + date.format(formatter));

			dialog.bodyText("");

			List<String> guessesForAnimation = new ArrayList<>(game.getGuesses());
			if (animationStep != -1 && animationStep <= 4) {
				var last = guessesForAnimation.removeLast();
				last = last.substring(0, animationStep) + " ".repeat(last.length() - animationStep);
				guessesForAnimation.add(last);
				Tasks.wait(ANIMATION_DELAY, () -> open(player, animationStep + 1));
			}

			while (guessesForAnimation.size() < 6)
				guessesForAnimation.add("     ");

			for (var guess : guessesForAnimation) {
				dialog.bodyText(game.getColoredGuess(guess).stream()
					.map(WordleLetter::toString)
					.collect(joining(" ")));
			}

			if (animationStep >= 0) {
				var guess = game.getColoredGuess(guessesForAnimation.getLast());
				var letters = guess.stream().filter(letter -> isNotNullOrEmpty(letter.getLetter())).toList();
				if (!letters.isEmpty()) {
					var lastLetter = letters.getLast();

					if (animationStep == 5) {
						if (game.isSuccess()) {
							WordleSound.SUCCESS.playSound(player);
						} else if (game.isFailed()) {
							WordleSound.FAIL.playSound(player);
						} else {
							lastLetter.getState().playSound(player);
						}
					} else {
						lastLetter.getState().playSound(player);
					}
				}
			}

			if (game.isFailed()) {
				if (animationStep == -1 || animationStep == 5) {
					dialog.bodyText(Arrays.stream(solution.toUpperCase().split(""))
						.map(letter -> new WordleLetter(FAILED, letter).toString())
						.collect(joining(" ")));
				}
			}

			dialog.bodyText("");

			for (String row : KEYBOARD){
				dialog.bodyText(getKeyboardColors(row, guessesForAnimation, solution)
					.stream()
					.map(WordleLetter::toString)
					.collect(joining(" ")));
			}

			var guesses = game.getGuesses();
			if (!game.isComplete())
				dialog.inputText("answer", errorMessage != null ? errorMessage : guesses.isEmpty() ? "Guess a 5 letter word" : "", input);

			var builder = dialog.multiAction().columns(1);
			if (game.isComplete())
				builder
					.button("See server results", click -> new WordleResultsMenu(date).open(player))
					.button("Go to archive", click -> new WordleArchiveMenu(YearMonth.from(date)).open(player));
			else
				builder.button("Submit", click -> {
					input = click.getText("answer").toLowerCase();
					if (isNullOrEmpty(input))
						errorMessage = "&cGuess a word";
					else if (input.length() != 5)
						errorMessage = "&cWord must be 5 letters long";
					else if (!config.getAllowedWords().contains(input))
						errorMessage = "&cWord not in dictionary";
					else if (guesses.contains(input))
						errorMessage = "&cYou already guessed that word";
					else {
						errorMessage = null;
						guesses.add(input);
					}

					var today = new GeoIPService().get(player).getCurrentTime().toLocalDate();
					if (today.isEqual(date) || today.isBefore(date)) {
						game.setPlayedOnReleaseDay(true);
						if (input.equalsIgnoreCase(solution)) {
							game.setSolvedOnReleaseDay(true);

							String formattedText = "&e" + user.getNickname() + " &3solved puzzle #" + gameConfig.getDaysSinceLaunch() + " in &e" + guesses.size() + " guesses&3!";
							int streak = user.getStreak();
							if (streak > 1)
								formattedText += " They are on a &e" + streak + " day &3streak!";

							var message = new JsonBuilder(formattedText);
							message.hover(user.getNerd().getColoredName());
							message.hover("&7Wordle #" + gameConfig.getDaysSinceLaunch());
							message.hover("&7" + date.format(formatter));
							message.hover("");
							for (String guess : guesses)
								message.hover(" " + game.getColoredGuess(guess).stream()
									.peek(letter -> letter.setLetter(null))
									.peek(letter -> letter.setEmoji(WordleLetter.SMALL_BOX_EMOJI))
									.map(WordleLetter::toString)
									.collect(joining(" ")));
							message.hover("");
							message.hover("&eClick to see server results");
							message.command("/wordle results " + date.format(DateTimeFormatter.ISO_DATE));

							//noinspection UnstableApiUsage
							Broadcast.all()
								.sender(player)
								.message(message)
								.prefix("Wordle")
								.messageType(MessageType.CHAT)
								.muteMenuItem(MuteMenuItem.WORDLE)
								.send();
						}
					}

					if (guesses.contains(input))
						input = "";

					open(player, errorMessage == null ? 0 : -1);
				})
				.button("Go to archive", click -> new WordleArchiveMenu(YearMonth.from(date)).open(player));

			builder.open(player);
			configService.save(config);
			userService.save(user);
		}

		private static @NotNull List<WordleLetter> getKeyboardColors(String keysInRow, List<String> guesses, String solution) {
			Map<String, WordleLetterState> keys = new HashMap<>();
			for (String letter : keysInRow.toLowerCase().split(""))
				keys.put(letter, UNUSED);

			for (String guess : guesses) {
				var solution2 = new ArrayList<>(List.of(solution.toLowerCase().split("")));
				var guess2 = new ArrayList<>(List.of(guess.toLowerCase().split("")));
				for (int i = 0; i < 5; i++) {
					var guessChar = guess2.get(i).toLowerCase();
					var solutionChar = solution2.get(i).toLowerCase();

					var state = keys.getOrDefault(guessChar, UNUSED);
					if (state == CORRECT_POSITION)
						continue;

					if (guessChar.equals(solutionChar)) {
						state = CORRECT_POSITION;
						guess2.set(i, "_");
						solution2.set(i, "_");
					} else if (solution2.contains(guessChar))
						state = WRONG_POSITION;
					else if (state != WRONG_POSITION)
						state = NO_POSITION;

					keys.put(guessChar, state);
				}
			}

			List<WordleLetter> letters = new ArrayList<>();
			for (String letter : keysInRow.toLowerCase().split(""))
				letters.add(new WordleLetter(keys.getOrDefault(letter, UNUSED), letter));
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
				.title(yearMonth.format(FORMATTER));

			var week = geoip.getWeek();
			var initial = yearMonth.atDay(1);
			while (initial.getDayOfWeek() != week.getFirst())
				initial = initial.minusDays(1);

			var last = yearMonth.atEndOfMonth();
			while (last.getDayOfWeek() != week.getLast())
				last = last.plusDays(1);

			last = last.plusDays(1);

			int streak = user.getStreak();
			dialog.bodyText("Current streak: &e" + streak + StringUtils.plural(" day", streak));

			var diff = initial.until(last).getDays();
			if (diff < 8)
				dialog.bodyText("").bodyText("");

			var buttons = dialog.multiAction()
				.columns(7)
				.defaultButtonWidth(30);

			for (DayOfWeek dayOfWeek : week)
				buttons.button("&l" + dayOfWeek.name().charAt(0));

			var today = user.getZonedLocalDate();
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
				int day = config.getDaysSinceLaunch(current);

				String label = color + String.valueOf(current.getDayOfMonth());

				JsonBuilder tooltip = null;
				if (day >= 0) {
					tooltip = new JsonBuilder("#" + day);
					var solved = configService.getSolvedCount(date);
					if (solved > 0)
						tooltip.newline().next("&7Solved by &e" + solved + " &7nerds");
				}

				buttons.button(label, tooltip, click -> {
						if (finalColor != ChatColor.DARK_GRAY) {
							if (user.get(date).isComplete())
								new WordleResultsMenu(date).open(player);
							else
								new WordleMenu(date).open(player);
						}
				});
				current = current.plusDays(1);
			}

			var previous = yearMonth.minusMonths(1);
			var before = previous.atEndOfMonth().isBefore(WordleConfig.EPOCH);
			buttons.button((before ? "&8" : "") + "< " + FORMATTER.format(previous), 107,click -> {
				if (before)
					return;

				yearMonth = previous;
				open(player);
			});

			var next = yearMonth.plusMonths(1);
			var after = next.atDay(1).isAfter(today);
			buttons.button((after ? "&8" : "") + FORMATTER.format(next) + " >", 107, click -> {
				if (after)
					return;

				yearMonth = next;
				open(player);
			});

			buttons.open(player);
		}
	}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		Tasks.wait(TickTime.SECOND, () -> new WordleUserService().get(event.getPlayer()).notifyOfNewGame());
	}

}
