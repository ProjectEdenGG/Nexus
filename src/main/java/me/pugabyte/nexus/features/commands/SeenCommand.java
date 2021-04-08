package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.StringUtils;

public class SeenCommand extends CustomCommand {

	public SeenCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	public void seen(Nerd nerd) {
		if (nerd.getOfflinePlayer().isOnline())
			send(PREFIX + "&e" + Nickname.of(nerd) + " &3has been &aonline &3for &e" + StringUtils.timespanDiff(nerd.getLastJoin()) + " &3(" + StringUtils.longDateTimeFormat(nerd.getLastJoin()) + ")");
		else
			send(PREFIX + "&e" + Nickname.of(nerd) + " &3has been &coffline &3for &e" + StringUtils.timespanDiff(nerd.getLastQuit()) + " &3(" + StringUtils.longDateTimeFormat(nerd.getLastQuit()) + ")");
	}
}
