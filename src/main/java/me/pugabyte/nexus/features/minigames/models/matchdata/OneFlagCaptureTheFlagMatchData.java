package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.OneFlagCaptureTheFlag;
import me.pugabyte.nexus.features.minigames.mechanics.Siege;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;

@Data
@MatchDataFor({OneFlagCaptureTheFlag.class, Siege.class})
public class OneFlagCaptureTheFlagMatchData extends MatchData {
	private Flag flag;
	private Minigamer flagCarrier;

	public OneFlagCaptureTheFlagMatchData(Match match) {
		super(match);
	}

}
