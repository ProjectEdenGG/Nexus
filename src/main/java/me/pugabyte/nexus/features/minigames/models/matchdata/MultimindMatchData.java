package me.pugabyte.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.Mastermind;
import me.pugabyte.nexus.features.minigames.mechanics.Multimind;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.TimeUtils.Timespan;

import java.util.Set;

@Data
@MatchDataFor(Multimind.class)
public class MultimindMatchData extends IMastermindMatchData {

	public MultimindMatchData(Match match) {
		super(match);
		createAnswer();
	}

	public int getSectionNumber(Minigamer minigamer) {
		Set<ProtectedRegion> section = arena.getRegionsLikeAt("section", minigamer.getPlayer().getLocation());
		if (section.size() != 1)
			throw new InvalidInputException("Could not determine which section you are in");

		return Arena.getRegionNumber(section.iterator().next());
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

		int number = getSectionNumber(minigamer);

		Region wallRegion = arena.getRegion("wall_" + number);
		Region guessRegion = arena.getRegion("guess_" + number);
		Region resultsSignRegion = arena.getRegion("results_sign_" + number);

		validate(minigamer, wallRegion, guessRegion, resultsSignRegion);
	}

	void lose(Minigamer minigamer) {
		showAnswer(minigamer);
		minigamer.tell("You were not able to crack the code! Better luck next time");
//		endOfGameChatButtons(minigamer);
	}

	void win(Minigamer minigamer) {
		showAnswer(minigamer);
//		int number = getSectionNumber(minigamer);
//		fireworks("fireworks_" + number);
		guesses.put(minigamer, maxGuesses + 1);
		minigamer.tell("You are the Mastermind! You cracked the code in " + Timespan.of(minigamer.getScore()).format());
//		Tasks.wait(Time.SECOND.x(4), () -> endOfGameChatButtons(minigamer));
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
