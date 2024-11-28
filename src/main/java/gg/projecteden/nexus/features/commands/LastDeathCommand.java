package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;

import java.time.LocalDateTime;

public class LastDeathCommand extends CustomCommand {

	public LastDeathCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player] [worldgroup]")
	@Description("View the time since a player last died")
	void lastLogin(@Arg("self") Nerd nerd, WorldGroup worldGroup) {
		if (worldGroup == null)
			worldGroup = nerd.getWorldGroup();

		String worldGroupName = StringUtils.camelCase(worldGroup);
		LocalDateTime lastDeath = nerd.getLastDeath(worldGroup);

		if (lastDeath == null)
			error("There is no record of " + nerd.getNickname() + " dying in " + worldGroupName + " yet");

		send(PREFIX + "&e&l" + nerd.getNickname() + " &3last died &e" + Timespan.of(lastDeath).format() + " &3ago in worldgroup &e" + worldGroupName);
	}
}
