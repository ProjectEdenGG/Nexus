package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.mechanics.TNTTag;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

@Data
@MatchDataFor(TNTTag.class)
public class TNTTagMatchData extends MatchData {
	private int round;
	private final Team taggedTeam, evadingTeam;

	public TNTTagMatchData(Match match) {
		super(match);
		this.taggedTeam = findTaggedTeam();
		this.evadingTeam = findEvadingTeam();
	}

	private Team findTaggedTeam() {
		return match.getArena().getTeams().stream()
			.filter(team -> team.getChatColor() == ChatColor.RED)
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("No tagged team found (color == light red)"));
	}

	@NotNull
	private Team findEvadingTeam() {
		return match.getArena().getTeams().stream()
			.filter(team -> team.getChatColor() != ChatColor.RED)
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("No evading team found (color != light red)"));
	}


}
