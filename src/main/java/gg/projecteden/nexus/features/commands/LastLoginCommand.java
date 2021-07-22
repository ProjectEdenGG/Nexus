package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.utils.TimeUtils.Timespan;

@Aliases("lastjoin")
public class LastLoginCommand extends CustomCommand {

	public LastLoginCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void lastLogin(@Arg("self") Nerd nerd) {
		send("&e&l" + Nickname.of(nerd) + " &3last logged in &e" + Timespan.of(nerd.getLastJoin()).format() + " &3ago");
	}
}
