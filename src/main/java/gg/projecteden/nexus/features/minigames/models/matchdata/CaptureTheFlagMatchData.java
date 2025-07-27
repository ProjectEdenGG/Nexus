package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.CaptureTheFlag;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.minigames.models.matchdata.shared.Flag;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@MatchDataFor(CaptureTheFlag.class)
public class CaptureTheFlagMatchData extends MatchData {
	private Map<Team, Flag> flags = new HashMap<>();

	public CaptureTheFlagMatchData(Match match) {
		super(match);
	}

	public Flag getFlag(Team team) {
		if (flags.containsKey(team))
			return flags.get(team);
		return null;
	}

	public void addFlag(Team team, Flag flag) {
		flags.put(team, flag);
	}

	public Flag getFlagByCarrier(Minigamer minigamer) {
		Optional<Map.Entry<Team, Flag>> optionalFlag = getFlags().entrySet().stream()
				.filter(teamFlagEntry -> {
					Flag _flag = teamFlagEntry.getValue();
					return minigamer.equals(_flag.getCarrier());
				}).findFirst();
		return optionalFlag.map(Map.Entry::getValue).orElse(null);
	}

	public void removeFlagCarrier(Minigamer minigamer) {
		Flag flag = getFlagByCarrier(minigamer);
		if (flag == null) return;

		flag.setCarrier(null);

		addFlag(flag.getTeam(), flag);
	}

	public void addFlagCarrier(Flag flag, Minigamer minigamer) {
		flag.setCarrier(minigamer);
		addFlag(flag.getTeam(), flag);
	}

}
