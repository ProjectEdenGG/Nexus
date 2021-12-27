package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;

@RequiredRole("Staff")
@HandledBy(Bot.RELAY)
public class ClearIngameChatAppCommand extends NexusAppCommand {

	public ClearIngameChatAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Clear ingame chat")
	void run() {
		runCommandAsConsole("clearchat");
	}

}
