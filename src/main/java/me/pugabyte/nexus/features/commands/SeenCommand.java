package me.pugabyte.nexus.features.commands;

import eden.utils.TimeUtils.Timespan;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.TimeUtils;

public class SeenCommand extends CustomCommand {

	public SeenCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	public void seen(Nerd nerd) {
		if (nerd.getOfflinePlayer().isOnline())
			send(PREFIX + "&e" + Nickname.of(nerd) + " &3has been &aonline &3for &e" + Timespan.of(nerd.getLastJoin()).format() + " &3(" + TimeUtils.longDateTimeFormat(nerd.getLastJoin()) + ")");
		else
			send(PREFIX + "&e" + Nickname.of(nerd) + " &3has been &coffline &3for &e" + Timespan.of(nerd.getLastQuit()).format() + " &3(" + TimeUtils.longDateTimeFormat(nerd.getLastQuit()) + ")");
	}
}
