package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Data
@MatchDataFor(Battleship.class)
public class MurderMatchData extends MatchData {
	private static HashMap<Player, Integer> locators = new HashMap<>();

	public MurderMatchData(Match match) {
		super(match);
	}
}
