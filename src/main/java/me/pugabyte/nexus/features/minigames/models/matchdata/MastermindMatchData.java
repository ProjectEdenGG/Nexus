package me.pugabyte.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.Mastermind;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;

@Data
@MatchDataFor(Mastermind.class)
public class MastermindMatchData extends IMastermindMatchData {

	public MastermindMatchData(Match match) {
		super(match);
		answerLength = match.getArena().getName().equals("Megamind") ? 5 : 4;
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
		minigamer.tell("You are the Mastermind! You cracked the code in " + Timespan.of(minigamer.getScore()).format());
		Tasks.wait(Time.SECOND.x(4), () -> endOfGameChatButtons(minigamer));
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
				.command("/mgm mastermind playAgain")
				.hover("Reset the board and play again")
				.group()
				.next("  &3||  &3")
				.group()
				.next("&c&lQuit")
				.command("/mgm quit")
				.hover("End the game")
				.newline()
				.send(minigamer.getPlayer());
	}

}
