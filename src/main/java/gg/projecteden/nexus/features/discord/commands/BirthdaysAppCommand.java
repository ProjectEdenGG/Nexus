package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Verify
@Command("Manage birthdays")
public class BirthdaysAppCommand extends NexusAppCommand {

	public BirthdaysAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Set your birthday")
	void set(@Desc("Date") LocalDate birthday) {
		final Nerd nerd = nerd();
		if (!nerd.getRank().isStaff())
			if (nerd.getBirthday() != null)
				error("You have already set your birthday to &e" + birthday + ". If it is incorrect, please ask a staff member to change it.");

		new NerdService().edit(nerd, player -> player.setBirthday(birthday));
		replyEphemeral("Set your birthday to " + DateTimeFormatter.ofPattern("MMMM d, yyyy").format(birthday));
	}

}
