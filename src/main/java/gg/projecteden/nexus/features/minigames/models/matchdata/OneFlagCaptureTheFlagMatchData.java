package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.FlagRush;
import gg.projecteden.nexus.features.minigames.mechanics.Siege;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.Flag;
import lombok.Data;

@Data
@MatchDataFor({FlagRush.class, Siege.class})
public class OneFlagCaptureTheFlagMatchData extends MatchData {
	private Flag flag;
	private Minigamer flagCarrier;

	public OneFlagCaptureTheFlagMatchData(Match match) {
		super(match);
	}

}
