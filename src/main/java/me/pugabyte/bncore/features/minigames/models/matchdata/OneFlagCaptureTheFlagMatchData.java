package me.pugabyte.bncore.features.minigames.models.matchdata;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.MatchData;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import org.bukkit.Location;

public class OneFlagCaptureTheFlagMatchData extends MatchData {
	@Getter
	@Setter
	Location originalFlagLocation;
	@Getter
	@Setter
	Minigamer flagCarrier;

	public OneFlagCaptureTheFlagMatchData(Match match) {
		super(match);
	}
}
