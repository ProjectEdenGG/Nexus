package me.pugabyte.nexus.features.minigames.models.matchdata;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.mechanics.OneFlagCaptureTheFlag;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import org.bukkit.Location;

@Data
@MatchDataFor(OneFlagCaptureTheFlag.class)
public class OneFlagCaptureTheFlagMatchData extends MatchData {
	Location originalFlagLocation;
	Minigamer flagCarrier;

	public OneFlagCaptureTheFlagMatchData(Match match) {
		super(match);
	}

}
