package gg.projecteden.nexus.features.discord.commands.justice;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.utils.DiscordId.Role;
import gg.projecteden.utils.DiscordId.TextChannel;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;

@HandledBy(Bot.RELAY)
public class ClearChatDiscordCommand extends Command {

	public ClearChatDiscordCommand() {
		this.name = "clearchat";
		this.aliases = new String[]{"cc"};
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(TextChannel.STAFF_BRIDGE.getId()))
			return;

		runCommandAsConsole("clearchat");
	}


}
