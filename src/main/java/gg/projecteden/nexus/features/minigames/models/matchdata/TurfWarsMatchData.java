package gg.projecteden.nexus.features.minigames.models.matchdata;

import com.sk89q.worldedit.regions.CuboidRegion;
import gg.projecteden.api.common.utils.EnumUtils.IterableEnum;
import gg.projecteden.nexus.features.minigames.mechanics.TurfWars;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@Data
@MatchDataFor(TurfWars.class)
public class TurfWarsMatchData extends MatchData {

	private State state = State.BUILD;
	private int time;
	private int phase;
	private List<FloorRow> rows = new ArrayList<>();

	private CuboidRegion team1Region;
	private CuboidRegion team2Region;

	public TurfWarsMatchData(Match match) {
		super(match);
	}

	public int getFloorWorth() {
		return this.phase / 2;
	}

	public enum State implements IterableEnum {
		BUILD,
		FIGHT;

		public String getTitle() {
			return "&e&l" + StringUtils.camelCase(name());
		}
	}

	@Data
	public static class FloorRow {
		private final List<Block> blockList;
		private Team team;

		public void setTeam(Team team) {
			if (this.team == null || this.team != team) {
				for (Block block : this.blockList) {
					block.setType(team.getColorType().getTerracotta());
					for (int i = 1; i <= 5; i++) {
						Block relative = block.getRelative(0, i, 0);
						if (!MaterialTag.WOOL.isTagged(relative))
							continue;

						relative.getLocation().getWorld().spawnParticle(Particle.BLOCK, relative.getLocation().toCenterLocation(), 50, relative.getType().createBlockData());
						relative.setType(Material.AIR);
					}
				}
			}
			this.team = team;
		}

	}

}
