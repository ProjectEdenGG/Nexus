package gg.projecteden.nexus.models.voter;

import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.models.voter.Voter.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopVoter {
	private Voter voter;

	private YearMonth yearMonth;
	private List<Vote> votes;

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
