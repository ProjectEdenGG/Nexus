package me.pugabyte.bncore.features.commands;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.StringUtils;

public class SeenCommand extends CustomCommand {

	public SeenCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path()
	public void seen(Nerd nerd) {
		if (nerd.getOfflinePlayer().isOnline())
			send(PREFIX + "&e" + nerd.getName() + " &3has been &aonline &3for &e" + StringUtils.timespanDiff(nerd.getLastJoin()));
		else
			send(PREFIX + "&e" + nerd.getName() + " &3has been &coffline &3for &e" + StringUtils.timespanDiff(nerd.getLastQuit()));
	}
}
