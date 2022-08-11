package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.MonsterMaze;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Data
@MatchDataFor(MonsterMaze.class)
public class MonsterMazeMatchData extends MatchData {
	private List<Location> goals = new ArrayList<>();

	public MonsterMazeMatchData(Match match) {
		super(match);
	}
}
