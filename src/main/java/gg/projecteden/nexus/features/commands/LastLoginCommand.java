package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;

@Aliases("lastjoin")
public class LastLoginCommand extends CustomCommand {

	public LastLoginCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("View the last time a player logged in")
	void lastLogin(@Arg("self") Nerd nerd) {
		send(PREFIX + "&e&l" + nerd.getNickname() + " &3last logged in &e" + Timespan.of(nerd.getLastJoin(isPlayer() ? player() : null)).format() + " &3ago");
	}
}
