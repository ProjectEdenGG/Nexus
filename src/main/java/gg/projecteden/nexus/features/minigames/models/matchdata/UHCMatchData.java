package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.minigames.mechanics.UHC;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@MatchDataFor(UHC.class)
public class UHCMatchData extends MatchData {
	private LocalDateTime startTime = LocalDateTime.now(); // placeholder, is updated on true game start
	private final Map<UUID, Long> timeAlive = new HashMap<>();

	public UHCMatchData(Match match) {
		super(match);
	}

	public void died(Minigamer minigamer) {
		timeAlive.put(minigamer.getUniqueId(), Timespan.of(startTime, LocalDateTime.now()).getOriginal());
	}

}
