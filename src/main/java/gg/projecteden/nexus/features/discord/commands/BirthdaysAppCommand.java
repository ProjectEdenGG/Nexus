package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Default;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Verify
@Command("Manage birthdays")
public class BirthdaysAppCommand extends NexusAppCommand {
	private final NerdService service = new NerdService();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

	public BirthdaysAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("View player birthdays")
	void of(@Desc("Player") @Default("self") Nerd player) {
		if (player.getBirthday() == null)
			error((isSelf(player) ? "You haven't" : player.getNickname() + " hasn't") + " set a birthday");

		replyEphemeral((isSelf(player) ? "Your" : player.getNickname() + "'s") + " birthday is " + formatter.format(player.getBirthday()));
	}

	@Command("Set your birthday")
	void set(
		@Desc("Date") LocalDate birthday,
		@Desc("Player") @RequiredRole("Staff") @Default("self") Nerd player
	) {
		if (!player.getRank().isStaff())
			if (player.getBirthday() != null)
				error("You have already set your birthday to &e" + formatter.format(birthday) + ". If it is incorrect, please ask a staff member to change it.");

		player.setBirthday(birthday);
		service.save(player);
		replyEphemeral("Set " + (isSelf(player) ? "your" : player.getNickname() + "'s") + " birthday to " + formatter.format(birthday));
	}

	@RequiredRole("Staff")
	@Command("Unset birthdays")
	void unset(@Default("self") Nerd player) {
		if (player.getBirthday() == null)
			error(player.getNickname() + " does not have a birthday set");

		player.setBirthday(null);
		service.save(player);
		replyEphemeral("Unset " + (isSelf(player) ? "your" : player.getNickname() + "'s") + " birthday");
	}

}
