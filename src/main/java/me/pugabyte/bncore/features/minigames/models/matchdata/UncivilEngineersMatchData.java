package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.mechanics.UncivilEngineers;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@MatchDataFor(UncivilEngineers.class)
public class UncivilEngineersMatchData extends CheckpointData {
	public List<Entity> entities = new ArrayList<>();
	public Map<UUID, Integer> playerStrips = new HashMap<>();
	public Map<UUID, List<EntityType>> playerEntities = new HashMap<>();

	public UncivilEngineersMatchData(Match match) {
		super(match);
	}
}
