package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;

import static me.pugabyte.nexus.utils.TimeUtils.longDateTimeFormat;

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
