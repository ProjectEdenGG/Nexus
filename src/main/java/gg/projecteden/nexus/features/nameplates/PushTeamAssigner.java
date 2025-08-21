package gg.projecteden.nexus.features.nameplates;

import gg.projecteden.nexus.models.push.PushService;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;

@Getter
public class PushTeamAssigner implements TeamAssigner {
	private final PushService service = new PushService();
	private final String pushTeamName = "NEXUS_PUSH";
	private final String noPushTeamName = "NEXUS_NO_PUSH";
	private final Team pushTeam;
	private final Team noPushTeam;

	public PushTeamAssigner() {
		this.pushTeam = initializeTeam(pushTeamName, true);
		this.noPushTeam = initializeTeam(noPushTeamName, false);
	}

	public @NotNull Team teamFor(Entity entity) {
		if (WorldGroup.of(entity).isPushDisabled())
			return noPushTeam;

		if (service.get(entity).isDisabled())
			return noPushTeam;

		return pushTeam;
	}

	private Team initializeTeam(String name, boolean allowPush) {
		Scoreboard scoreboard = TeamAssigner.scoreboard();
		Team team = scoreboard.getTeam(name);

		if (team != null)
			team.unregister();

		team = scoreboard.registerNewTeam(name);
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
		team.setOption(Option.COLLISION_RULE, allowPush ? OptionStatus.ALWAYS : OptionStatus.NEVER);
		team.setOption(Option.COLLISION_RULE, allowPush ? OptionStatus.ALWAYS : OptionStatus.NEVER);
		team.setCanSeeFriendlyInvisibles(false);

		return team;
	}

}
