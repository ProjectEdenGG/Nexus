package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.utils.TimeUtils;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.NonNull;

@Description("Check when a player was last online, or how long they have been online")
public class SeenCommand extends CustomCommand {

	public SeenCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	public void seen(@Arg("self") Nerd nerd) {
		String nickname = nerd.getNickname();
		if (nerd.isOnline() && PlayerUtils.canSee(player(), nerd))
			send(PREFIX + "&e" + nickname + " &3has been &aonline &3for &e" + Timespan.of(nerd.getLastJoin(player())).format() + " &3(" + TimeUtils.longDateTimeFormat(nerd.getLastJoin(player())) + ")");
		else
			send(PREFIX + "&e" + nickname + " &3has been &coffline &3for &e" + Timespan.of(nerd.getLastQuit(player())).format() + " &3(" + TimeUtils.longDateTimeFormat(nerd.getLastQuit(player())) + ")");
	}
}
