package gg.projecteden.nexus.features.minigames.models.arenas;

import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.matchdata.TurfWarsMatchData;
import gg.projecteden.nexus.utils.LocationUtils.Axis;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@SerializableAs("TurfWarsArena")
public class TurfWarsArena extends Arena {

	Location team1FloorEnd;
	Location team2FloorEnd;
	Axis direction = Axis.X;

	public TurfWarsArena(Map<String, Object> map) {
		super(map);
		this.team1FloorEnd = (Location) map.getOrDefault("team1FloorEnd", team1FloorEnd);
		this.team2FloorEnd = (Location) map.getOrDefault("team2FloorEnd", team2FloorEnd);
		this.direction = Axis.valueOf(((String) map.getOrDefault("direction", direction.name())).toUpperCase());
	}

	@Override
	public int getCalculatedWinningScore(@NotNull Match match) {
		return ((TurfWarsMatchData) match.getMatchData()).getRows().size();
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("team1FloorEnd", team1FloorEnd);
		map.put("team2FloorEnd", team2FloorEnd);
		map.put("direction", direction.name());
		return map;
	}

	public void setDirection() {
		if (team1FloorEnd != null && team2FloorEnd != null && direction == null) {
			direction = team1FloorEnd.getX() == team2FloorEnd.getX() ? Axis.X : Axis.Z;
			ArenaManager.write(this);
		}
	}

}
