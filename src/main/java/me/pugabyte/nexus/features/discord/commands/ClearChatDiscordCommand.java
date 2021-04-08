package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;

import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;

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
