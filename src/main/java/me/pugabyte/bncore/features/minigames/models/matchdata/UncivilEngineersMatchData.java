package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.UncivilEngineers;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@MatchDataFor(UncivilEngineers.class)
public class UncivilEngineersMatchData extends MatchData {
	public Map<Entity, UUID> entities = new HashMap<>();

	public UncivilEngineersMatchData(Match match) {
		super(match);
	}
}
