package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.models.punishments.Punishments;

import java.util.stream.Collectors;

@RequiredRole("Staff")
@Command("View a player's alts")
public class AltsAppCommand extends NexusAppCommand {

	public AltsAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "View a player's alts", literals = false)
	void run(
		@Desc("Player") Punishments player
	) {
		// TODO Categorize by active type? See ingame /alts
		String alts = player.getAlts().stream()
			.map(Punishments::of).map(_player -> {
				if (player.getAnyActiveBan().isPresent())
					return "**" + _player.getName() + "**";
				else if (_player.isOnline())
					return "_" + _player.getName() + "_";
				else return _player.getName();
			}).distinct().collect(Collectors.joining(", "));

		reply("Alts of `" + player.getName() + "` [_Online_ Offline **Banned**]:" + System.lineSeparator() + alts);
	}

}
