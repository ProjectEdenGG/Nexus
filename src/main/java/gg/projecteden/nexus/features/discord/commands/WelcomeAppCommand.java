package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.commands.staff.WelcomeCommand;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.features.discord.commands.common.annotations.Verify;
import gg.projecteden.nexus.models.nickname.Nickname;
import org.bukkit.entity.Player;

@Verify
@RequiredRole("Staff")
@Command("Welcome a player to the server")
public class WelcomeAppCommand extends NexusAppCommand {

	public WelcomeAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Welcome a player to the server", literals = false)
	void run(@Desc("Player") Player player) {
		WelcomeCommand.welcome(nerd(), player);
		replyEphemeral("Welcoming " + Nickname.of(player) + " to the server");
	}

}
