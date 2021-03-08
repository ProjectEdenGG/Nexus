package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.HideAndSeek;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Data
@MatchDataFor(HideAndSeek.class)
public class HideAndSeekMatchData extends MatchData {
	private Map<UUID, Material> blockChoices = new HashMap<>();
	private List<Material> mapMaterials = new ArrayList<>(arena.getBlockList());
	private Map<UUID, FallingBlock> solidBlocks = new HashMap<>();
	private Map<Minigamer, Location> solidPlayers = new HashMap<>();
	private final Random random = new Random();

	public HideAndSeekMatchData(Match match) {
		super(match);
	}

	public Material getBlockChoice(UUID userId) {
		blockChoices.computeIfAbsent(userId, $ -> mapMaterials.get(random.nextInt(mapMaterials.size())));
		return blockChoices.get(userId);
	}

	public Material getBlockChoice(Player player) {
		return getBlockChoice(player.getUniqueId());
	}

	public Material getBlockChoice(Minigamer minigamer) {
		return getBlockChoice(minigamer.getPlayer());
	}
}
