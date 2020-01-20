package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;
import java.util.List;

@Data
public class MonsterMazeMatchData extends MatchData {
	private List<Zombie> zombies = new ArrayList<>();

	public MonsterMazeMatchData(Match match) {
		super(match);
	}
}
