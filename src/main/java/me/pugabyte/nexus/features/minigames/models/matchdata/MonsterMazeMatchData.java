package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.MonsterMaze;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.entity.Mob;

import java.util.ArrayList;
import java.util.List;

@Data
@MatchDataFor(MonsterMaze.class)
public class MonsterMazeMatchData extends MatchData {
	private List<Mob> monsters = new ArrayList<>();

	public MonsterMazeMatchData(Match match) {
		super(match);
	}
}
