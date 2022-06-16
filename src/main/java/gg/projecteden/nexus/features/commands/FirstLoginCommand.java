package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;

import java.time.format.DateTimeFormatter;

import static gg.projecteden.api.common.utils.TimeUtils.longDateTimeFormat;

@Aliases("firstjoin")
public class FirstLoginCommand extends CustomCommand {

	public FirstLoginCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void firstJoin(@Arg("self") Nerd nerd) {
		send("&e&l" + Nickname.of(nerd) + " &3first joined Project Eden on &e" + longDateTimeFormat(nerd.getFirstJoin()) + " &3US Eastern Time");
	}

	@Async
	@Path("stats")
	@Permission(Group.ADMIN)
	void stats() {
		StringBuilder data = new StringBuilder();
		for (Nerd nerd : new NerdService().getAll())
			if (nerd.getFirstJoin() != null)
				data
					.append(nerd.getNickname())
					.append(",")
					.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(nerd.getFirstJoin()))
					.append(System.lineSeparator());

		IOUtils.fileAppend("joindates.csv", data.toString());
		send(PREFIX + "Generated joindates.csv");
	}

}
