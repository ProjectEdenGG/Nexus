package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.MonsterMaze;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.monstermaze.MonsterMazePathfinder;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@MatchDataFor(MonsterMaze.class)
public class MonsterMazeMatchData extends MatchData {
	private List<Location> goals = new ArrayList<>();
	private final Map<UUID, MonsterMazePathfinder> pathfinders = new HashMap<>();

	public MonsterMazeMatchData(Match match) {
		super(match);
	}

	public MonsterMazePathfinder getPathfinder(Mob mob) {
		return pathfinders.computeIfAbsent(mob.getUniqueId(), $ -> new MonsterMazePathfinder(mob));
	}

	public void launch(Minigamer minigamer, Mob mob) {
		Location playerCenterLocation = minigamer.getOnlinePlayer().getEyeLocation();
		Location playerToThrowLocation = mob.getEyeLocation();

		double x = playerCenterLocation.getX() - playerToThrowLocation.getX();
		double y = playerCenterLocation.getY() - playerToThrowLocation.getY();
		double z = playerCenterLocation.getZ() - playerToThrowLocation.getZ();

		Vector throwVector = new Vector(x, y, z).normalize().multiply(1.1).setY(1.25);

		minigamer.getOnlinePlayer().setVelocity(throwVector);
	}

	public void updatePath(Mob mob) {
		final int attempts = 10;
		final boolean success = Utils.attempt(attempts, () -> getPathfinder(mob).moveTo(getNewGoal(mob.getLocation(), goals)));

		if (!success)
			match.broadcast("Failed to assign path after " + attempts + " attempts");
	}

	private Location getNewGoal(Location start, List<Location> goals) {
		HashMap<Location, Double> distances = new HashMap<>();
		goals.forEach(goal -> distances.put(goal, Distance.distance(start, goal).get()));

		List<Location> sorted = distances.entrySet().stream()
			.sorted(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.toList();

		return sorted.get(RandomUtils.randomInt(sorted.size() / 2, sorted.size() - 1));
	}

}
