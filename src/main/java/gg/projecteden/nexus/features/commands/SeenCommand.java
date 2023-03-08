package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;

import java.time.LocalDateTime;

public class SeenCommand extends CustomCommand {

	public SeenCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("Check when a player was last online, or how long they have been online")
	void seen(@Arg("self") Nerd target) {
		send(getSeen(nerd(), target));
	}

	@HideFromWiki
	@Path("staff")
	@Permission(Group.ADMIN)
	void seenStaff() {
		for (Rank rank : Rank.STAFF_RANKS)
			rank.getNerds().thenAccept(nerds -> nerds.forEach(nerd -> send(getSeen(nerd(), nerd))));
	}

	private String getSeen(Nerd viewer, Nerd target) {
		String nickname = target.getNickname();

		if (target.isOnline() && PlayerUtils.canSee(viewer, target)) {
			LocalDateTime lastJoin = target.getLastJoin(player());
			String timespan = Timespan.of(lastJoin).format();
			String time = TimeUtils.longDateTimeFormat(lastJoin);

			return PREFIX + "&e" + nickname + " &3has been &aonline &3for &e" + timespan + " &3(" + time + ")";
		} else {
			LocalDateTime lastQuit = target.getLastQuit(player());
			String timespan = Timespan.of(lastQuit).format();
			String time = TimeUtils.longDateTimeFormat(lastQuit);

			return PREFIX + "&e" + nickname + " &3has been &coffline &3for &e" + timespan + " &3(" + time + ")";
		}
	}
}
