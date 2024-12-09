package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.HideAndSeek;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.Data;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@MatchDataFor(HideAndSeek.class)
public class HideAndSeekMatchData extends MatchData {
	private static final Set<Material> BANNED_MATERIALS = new HashSet<>();
	private final Map<UUID, Disguise> disguises = new HashMap<>();
	private final Map<UUID, Material> blockChoices = new HashMap<>();
	private final List<Material> mapMaterials = arena.getBlockList().stream().filter(material -> !BANNED_MATERIALS.contains(material) && material.isBlock()).collect(Collectors.toList());
	private final Map<UUID, FallingBlock> solidBlocks = new HashMap<>();
	private final Map<Minigamer, Location> solidPlayers = new HashMap<>();
	private final List<Item> flashBangItems = new ArrayList<>();
	private final List<HideAndSeek.Decoy.DecoyInstance> decoyLocations = new ArrayList<>();
	private static final Random random = new Random();

	static {
		// drunk wakka told me to use MaterialTag.INVENTORY_BLOCKS but that includes shit like
		// barrels and furnaces and anvils and a lot of perfectly fine & functional blocks
		// so i'm just manually doing these instead
		BANNED_MATERIALS.addAll(Arrays.asList(
				Material.CHEST,
				Material.TRAPPED_CHEST,
				Material.ENDER_CHEST,
				Material.BARRIER,
				Material.AIR,
				Material.CAULDRON,
				Material.HOPPER,
				Material.COMPOSTER
		));

		// i've found some broken walls that don't have hitboxes before from conversion errors, so just to be safe...
		BANNED_MATERIALS.addAll(MaterialTag.WALLS.getValues());
	}

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
		return getBlockChoice(minigamer.getOnlinePlayer());
	}
}
