package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ThimbleMatchData extends MatchData {
	private List<Minigamer> turnList = new ArrayList<>();
	private Set<Short> chosenConcrete = new HashSet<>();
	private int turns;
	private Minigamer turnPlayer;
	private int turnWaitTaskId;
	@Accessors(fluent = true)
	private boolean isEnding;

	public ThimbleMatchData(Match match) {
		super(match);
	}

}
