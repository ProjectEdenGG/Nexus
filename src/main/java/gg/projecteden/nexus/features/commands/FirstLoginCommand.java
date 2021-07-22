package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;

import static gg.projecteden.utils.TimeUtils.longDateTimeFormat;

@Aliases("firstjoin")
public class FirstLoginCommand extends CustomCommand {

	public FirstLoginCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void firstJoin(@Arg("self") Nerd nerd) {
		send("&e&l" + Nickname.of(nerd) + " &3first joined Project Eden on &e" + longDateTimeFormat(nerd.getFirstJoin()) + " &3US Eastern Time");
	}
}
