package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.minigames.mechanics.Mastermind;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;

@Data
@MatchDataFor(Mastermind.class)
public class MastermindMatchData extends IMastermindMatchData {

	public MastermindMatchData(Match match) {
		super(match);
		answerLength = "Megamind".equals(match.getArena().getName()) ? 5 : 4;
		createAnswer();
	}

	public void reset(Minigamer minigamer) {
		guesses.put(minigamer, 1);
		createAnswer();
		arena.regenerate();
		minigamer.setScore(0);
		giveLoadout(minigamer);
		resetResultsSign();
	}

	public void guess(Minigamer minigamer) {
		if (getGuess(minigamer) > maxGuesses)
			return;

		Region wallRegion = arena.getRegion("wall");
		Region guessRegion = arena.getRegion("guess");
		Region resultsSignRegion = arena.getRegion("results_sign");

		validate(minigamer, wallRegion, guessRegion, resultsSignRegion);
	}

	void lose(Minigamer minigamer) {
		showAnswer(minigamer);
		minigamer.tell("You were not able to crack the code! Better luck next time");
		endOfGameChatButtons(minigamer);
	}

	void win(Minigamer minigamer) {
		showAnswer(minigamer);
		fireworks("fireworks");
		guesses.put(minigamer, maxGuesses + 1);
		minigamer.tell("You are the Mastermind! You cracked the code in " + Timespan.ofSeconds(minigamer.getScore()).format());
		Tasks.wait(TickTime.SECOND.x(4), () -> endOfGameChatButtons(minigamer));
		match.getMatchStatistics().award(MatchStatistics.WINS, minigamer);
	}

	private void showAnswer(Minigamer minigamer) {
		showAnswer(minigamer, arena.getRegion("wall"));
	}

	void endOfGameChatButtons(Minigamer minigamer) {
		if (!minigamer.isPlaying(Mastermind.class))
			return;

		new JsonBuilder()
				.newline()
				.next("&a&l  Play Again")
				.command("/mgm mastermind reset")
				.hover("Reset the board and play again")
				.group()
				.next("  &3||  &3")
				.group()
				.next("&c&lQuit")
				.command("/mgm quit")
				.hover("End the game")
				.newline()
				.send(minigamer.getOnlinePlayer());
	}

}
