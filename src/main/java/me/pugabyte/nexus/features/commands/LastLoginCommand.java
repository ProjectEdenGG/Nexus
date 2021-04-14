package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;

import static me.pugabyte.nexus.utils.TimeUtils.timespanDiff;

@Aliases("lastjoin")
public class LastLoginCommand extends CustomCommand {

	public LastLoginCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void lastLogin(@Arg("self") Nerd nerd) {
		send("&e&l" + Nickname.of(nerd) + " &3last logged in &e" + timespanDiff(nerd.getLastJoin()) + " &3ago");
	}
}
