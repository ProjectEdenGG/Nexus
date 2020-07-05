package me.pugabyte.bncore.features.minigames.models.arenas;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Team;
import me.pugabyte.bncore.features.minigames.models.exceptions.MinigameException;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@Data
@SerializableAs("BattleshipArena")
public class BattleshipArena extends Arena {

	public BattleshipArena(Map<String, Object> map) {
		super(map);
	}

	public Team getOtherTeam(Team team) {
		return getTeams().stream()
				.filter(_team -> !team.getName().equals(_team.getName()))
				.findFirst()
				.orElseThrow(() -> new MinigameException("Could not find opposite team of " + team.getName() + " in " + getName()));
	}

	public Team getTeam(Location location) {
		for (ProtectedRegion region : getWGUtils().getRegionsAt(location))
			if (ownsRegion(region.getId(), "team"))
				for (Team team : getTeams())
					if (region.getId().split("_")[3].equalsIgnoreCase(team.getName().replaceAll(" ", "").toLowerCase()))
						return team;
		return null;
	}

}
