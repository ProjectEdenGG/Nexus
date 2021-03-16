package me.pugabyte.nexus.features.minigames.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class MatchData {
	@ToString.Exclude
	protected Match match;
	protected Arena arena;
	protected WorldGuardUtils WGUtils;
	protected WorldEditUtils WEUtils;

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

	protected Map<Team, PublicChannel> teamChannels = new HashMap<>();

	public MatchData(Match match) {
		this.match = match;
		this.arena = match.getArena();
		WGUtils = match.getArena().getWGUtils();
		WEUtils = match.getArena().getWEUtils();
	}

}
