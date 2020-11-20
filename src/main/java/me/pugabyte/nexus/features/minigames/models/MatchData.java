package me.pugabyte.nexus.features.minigames.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MatchData {
	@ToString.Exclude
	private Match match;
	protected WorldGuardUtils WGUtils;
	protected WorldEditUtils WEUtils;

	private Team turnTeam;
	private Minigamer turnMinigamer;
	private List<Team> turnTeamList = new ArrayList<>();
	private List<Minigamer> turnMinigamerList = new ArrayList<>();
	private LocalDateTime turnStarted;
	private int turns;
	@Accessors(fluent = true)
	private boolean isEnding;

	public MatchData(Match match) {
		this.match = match;
		WGUtils = match.getArena().getWGUtils();
		WEUtils = match.getArena().getWEUtils();
	}

}
