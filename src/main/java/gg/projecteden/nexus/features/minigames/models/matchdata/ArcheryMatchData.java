package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.mechanics.Archery;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.arenas.ArcheryArena;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Data;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@MatchDataFor(Archery.class)
public class ArcheryMatchData extends MatchData {
	private Map<String, Clipboard> targetSchematics = new HashMap<>();
	@ToString.Exclude
	private Map<ProtectedRegion, ArrayList<Location>> powderLocations = new HashMap<>();
	private Map<Minigamer, Integer> targetsHit = new HashMap<>();

	public void addTargets(Minigamer minigamer) {
		Integer targets = targetsHit.get(minigamer);
		if (targets == null)
			targets = 0;
		targetsHit.put(minigamer, ++targets);
	}

	public int getTargetsHit(Minigamer minigamer) {
		Integer targets = targetsHit.get(minigamer);
		if (targets == null)
			targets = 0;
		return targets;
	}

	public Map<Minigamer, Integer> getSortedScores(Match match) {
		List<Minigamer> minigamers = match.getMinigamers();
		Map<Minigamer, Integer> allScores = new HashMap<>();

		minigamers.forEach(minigamer -> allScores.put(minigamer, minigamer.getScore()));
		LinkedHashMap<Minigamer, Integer> reverseSortedMap = new LinkedHashMap<>();

		allScores.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

		return reverseSortedMap;
	}

	public void loadRangeLocations(int range, Match match) {
		ArcheryArena arena = match.getArena();

		String regex = "range_" + range + "_.*";
		Set<ProtectedRegion> colorRegions = arena.getRegionsLike(regex);
		colorRegions.forEach(colorRegion -> {
			powderLocations.put(colorRegion, new ArrayList<>());

			worldedit().getBlocks(worldguard().convert(colorRegion)).forEach(block -> {
				if (MaterialTag.CONCRETE_POWDERS.isTagged(block.getType()))
					powderLocations.get(colorRegion).add(block.getLocation());
			});
		});
	}

	// This should never be used during onQuit if the game is not started
	public void unloadRangeLocations(int range, Match match) {
		ArcheryArena arena = match.getArena();

		Set<ProtectedRegion> colorRegions = arena.getRegionsLike("range_" + range + "_");
		colorRegions.forEach(colorRegion -> {
			if (powderLocations.get(colorRegion) != null)
				powderLocations.remove(colorRegion);
		});
	}

	public String getRangeColor(ProtectedRegion protectedRegion) {
		String[] strings = protectedRegion.getId().split("_");
		return strings[strings.length - 1];
	}

	public int getRangeNumber(ProtectedRegion protectedRegion) {
		String[] strings = protectedRegion.getId().split("_");
		return Integer.parseInt(strings[strings.length - 2]);
	}

	public Direction getRangeDirection(int range, Match match) {
		if (match.getAliveTeams().size() == 0) return null;
		List<Location> spawnpoints = match.getAliveTeams().get(0).getSpawnpoints();
		Location spawnpoint = spawnpoints.get(range - 1);
		Location down = spawnpoint.getBlock().getRelative(0, -1, 0).getLocation();
		Block north = down.getBlock().getRelative(0, 0, -1);
		Block south = down.getBlock().getRelative(0, 0, 1);

		// If block in direction is top dark oak wood slab == direction of range
		if (north.getType().equals(Material.DARK_OAK_SLAB))
			return Direction.NORTH;
		else if (south.getType().equals(Material.DARK_OAK_SLAB))
			return Direction.SOUTH;

		return null;
	}

	public Direction getTargetDirection(ProtectedRegion region) {
		String[] strings = region.getId().split("_");
		return Direction.valueOf(strings[strings.length - 1].toUpperCase());
	}

	public String getTargetColor(ProtectedRegion region) {
		String[] strings = region.getId().split("_");
		return strings[strings.length - 2];
	}

	public void removeInactiveRanges(Match match) {
		ArcheryArena arena = match.getArena();
		Set<ProtectedRegion> rangeRegions = arena.getRegionsLike("_range_[\\d]+_.*");
		Set<ProtectedRegion> activeRegions = new HashSet<>();

		// Get active regions
		List<Minigamer> minigamers = match.getMinigamers();
		minigamers.forEach(minigamer -> {
			Set<ProtectedRegion> regionsAt = worldguard().getRegionsAt(minigamer.getOnlinePlayer().getLocation());
			regionsAt.forEach(region -> {
				if (rangeRegions.contains(region))
					activeRegions.add(region);
			});
		});

		// Remove active regions from range regions
		activeRegions.forEach(rangeRegions::remove);

		// Unload inactive regions
		for (ProtectedRegion region : rangeRegions) {
			unloadRangeLocations(getRangeNumber(region), match);
		}
	}

	public ArcheryMatchData(Match match) {
		super(match);
	}
}
