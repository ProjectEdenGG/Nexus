package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class UncivilEngineersMatchData extends MatchData {
	public Map<Entity, UUID> entities = new HashMap<>();
}
