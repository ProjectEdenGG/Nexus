package gg.projecteden.nexus.models.voter;

import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TopVoter {
	private final Voter voter;
	private final List<Vote> votes;
	private YearMonth yearMonth;

	public int getCount() {
		return votes.size();
	}

	public String getNickname() {
		try {
			return getVoter().getNickname();
		} catch (PlayerNotFoundException ignore) {}

		return "Unknown";
	}

}
