package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static gg.projecteden.api.common.utils.TimeUtils.longDateTimeFormat;

@Aliases("firstjoin")
public class FirstLoginCommand extends CustomCommand {

	public FirstLoginCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("View when a player first joined the server")
	void firstJoin(@Optional("self") Nerd nerd) {
		send("&e&l" + Nickname.of(nerd) + " &3first joined Project Eden on &e" + longDateTimeFormat(nerd.getFirstJoin()) + " &3US Eastern Time");
	}

	@Permission(Group.ADMIN)
	@Path("set <player> <date>")
	@Description("Set a player's first join date")
	void set(Nerd nerd, LocalDateTime dateTime) {
		nerd.setFirstJoin(dateTime);
		new NerdService().save(nerd);
		send(PREFIX + "Updated first join of &e" + nerd.getNickname() + " &3to &e" + TimeUtils.shortDateTimeFormat(dateTime));
	}

	@Async
	@Path("stats")
	@Permission(Group.ADMIN)
	@Description("Write all join dates to a CSV")
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
