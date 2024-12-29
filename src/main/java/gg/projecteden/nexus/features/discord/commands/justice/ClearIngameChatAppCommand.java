package gg.projecteden.nexus.features.discord.commands.justice;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.utils.PlayerUtils;

@RequiredRole("Staff")
@Command("Clear ingame chat")
public class ClearIngameChatAppCommand extends NexusAppCommand {

	public ClearIngameChatAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Clear ingame chat", literals = false)
	void run() {
		PlayerUtils.runCommandAsConsole("clearchat");
	}

}
