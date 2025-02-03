package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.features.minigames.mechanics.BlockParty;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@MatchDataFor(BlockParty.class)
public class BlockPartyMatchData extends MatchData {

	private int round;
	private BlockParty.BlockPartySong song;
	private double songTimeInSeconds = 0;
	private LocalDateTime playTime;
	private boolean playing;
	private Material block;
	private List<BlockParty.BlockPartySong> possibleSongs = new ArrayList<>();
	private Map<UUID, String> votes = new HashMap<>();
	private List<Minigamer> aliveAtStartOfRound = new ArrayList<>();
	private List<Minigamer> winners;
	private JsonBuilder actionBarMessage;

	private List<Location> colorChangingBlocks = new ArrayList<>();
	private BlockData colorChangingBlockData;
	private int eqTaskId = -1;
	private int eqCurrentFrame = 0;

	private boolean animateDiscoBall;
	private int discoBallTaskId = -1;

	public BlockPartyMatchData(Match match) {
		super(match);
	}

	public Region getPasteRegion() {
		return getMatch().getArena().getRegion("floor");
	}

	public Region getStackRegion() {
		return getMatch().getArena().worldguard().getRegion("blockparty_stack");
	}

	public int countFloors() {
		Location min = arena.worldedit().toLocation(getStackRegion().getMinimumPoint());
		int highest = min.getWorld().getHighestBlockYAt(min);

		return highest - (int) min.y();
	}

	public Region getFloorInStack(int index) {
		Location min = arena.worldedit().toLocation(getStackRegion().getMinimumPoint());
		Location max = arena.worldedit().toLocation(getStackRegion().getMaximumPoint());

		int highest = min.getWorld().getHighestBlockYAt(min);

		if (highest - min.y() < index)
			throw new InvalidInputException("That index does not exist");

		min = min.clone();
		min.setY(min.y() + index);
		max.setY(min.y() + index);

		return new CuboidRegion(arena.worldedit().toBlockVector3(min), arena.worldedit().toBlockVector3(max));
	}

	public void incRound() {
		this.round++;
	}
}
