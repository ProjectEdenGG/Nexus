package gg.projecteden.nexus.features.minigames.models;

import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MatchData {
	@ToString.Exclude
	protected Match match;
	protected Arena arena;

	protected Team winnerTeam;
	protected Minigamer winnerPlayer;

	protected Team turnTeam;
	protected Minigamer turnMinigamer;
	protected List<Team> turnTeamList = new ArrayList<>();
	protected List<Minigamer> turnMinigamerList = new ArrayList<>();
	protected LocalDateTime turnStarted;
	protected int turns;
	@Accessors(fluent = true)
	protected boolean isEnding;

	public MatchData(Match match) {
		this.match = match;
		this.arena = match.getArena();
	}

	public WorldEditUtils worldedit() {
		return match.worldedit();
	}

	public WorldGuardUtils worldguard() {
		return match.worldguard();
	}

}
