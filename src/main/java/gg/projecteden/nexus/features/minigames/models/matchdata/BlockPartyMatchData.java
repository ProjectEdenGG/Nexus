package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.features.minigames.mechanics.BlockParty;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;
import org.bukkit.Material;

@Data
@MatchDataFor(BlockParty.class)
public class BlockPartyMatchData extends MatchData {

	private BlockParty.BlockPartySong song;
	private int songTimeInSeconds = 0;
	private boolean playing;
	private Material block;

	public Region getPasteRegion() {
		return getMatch().getArena().getRegion("floor");
	}

}
