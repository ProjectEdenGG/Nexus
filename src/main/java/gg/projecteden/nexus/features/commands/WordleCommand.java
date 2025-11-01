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
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
		new WordleMenu().open(player());
	}

	public static class WordleMenu {
		private String errorMessage;

		public void refresh(Player player) {
			userService.save(userService.get(player));
			open(player);
		}

		public void open(Player player) {
			var user = userService.get(player);
			var today = new GeoIPService().get(player).getCurrentTime().toLocalDate();
			var gameConfig = config.get(today);
			var game = user.get(today);
			var solution = gameConfig.getSolution().toUpperCase();

			var dialog = new DialogBuilder()
				.title("Wordle #" + gameConfig.getDaysSinceLaunch() + " | " + today.format(formatter));

			for (var guess : game.getGuesses())
				dialog.bodyText(String.join(" ", getColoredGuesses(guess, solution)));

			dialog.bodyText("");

			for (String row : KEYBOARD)
				dialog.bodyText(String.join(" ", getKeyboardColors(row, game, solution)));

			if (!game.getGuesses().contains(solution))
				dialog.inputText("answer", errorMessage != null ? errorMessage : "");

			var builder = dialog.multiAction();
			if (game.getGuesses().contains(solution))
				builder.button("Go to archive", click -> {
					// TODO back to day picker
					user.sendMessage("TODO");
				});
			else
				builder.button("Submit", click -> {
					var input = click.getText("answer");
					if (isNullOrEmpty(input))
						errorMessage = "&cGuess a word";
					else if (!config.getAllowedWords().contains(input.toLowerCase()))
						errorMessage = "&cWord not in dictionary";
					else if (game.getGuesses().contains(input))
						errorMessage = "&cYou already guessed that word";
					else {
						errorMessage = null;
						game.getGuesses().add(input);
					}

					refresh(player);
				});

			builder.open(player);
		}

		private static @NotNull List<String> getKeyboardColors(String row, WordleGame game, String solution) {
			List<String> keys = new ArrayList<>();
			for (String letter : row.split("")) {
				ChatColor color = ChatColor.WHITE;

				for (String guess : game.getGuesses()) {
					if (!guess.contains(letter))
						continue;

					if (guess.indexOf(letter) == solution.indexOf(letter)) {
						color = ChatColor.GREEN;
						break;
					} else if (solution.contains(letter)) {
						color = ChatColor.YELLOW;
					} else if (color != ChatColor.YELLOW) {
						color = ChatColor.DARK_GRAY;
					}
				}

				keys.add(color + letter);
			}
			return keys;
		}

		private static @NotNull List<String> getColoredGuesses(String guess, String solution) {
			var solution2 = new ArrayList<>(List.of(solution.toUpperCase().split("")));
			var guess2 = new ArrayList<>(List.of(guess.toUpperCase().split("")));
			List<String> guessColored = new ArrayList<>();

			for (var letter : guess2) {
				ChatColor color = ChatColor.GRAY;

				for (int i = 0; i < 5; i++) {
					int index = guess2.indexOf(letter);
					if (index == -1)
						continue;

					if (index == solution2.indexOf(letter)) {
						color = ChatColor.GREEN;
						solution2.set(index, "_");
						guess2.set(index, "_");
					}

					if (solution2.contains(letter))
						color = ChatColor.YELLOW;
				}

				guessColored.add(color + letter);
			}
			return guessColored;
		}

	}
}
