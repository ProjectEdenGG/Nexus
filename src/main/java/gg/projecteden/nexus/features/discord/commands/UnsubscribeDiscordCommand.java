package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.DiscordId.Role;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Tasks;

import static gg.projecteden.nexus.features.discord.commands.SubscribeDiscordCommand.getRole;
import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class UnsubscribeDiscordCommand extends Command {

	public UnsubscribeDiscordCommand() {
		this.name = "unsubscribe";
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/unsubscribe <role>`");

				Role role = getRole(args[0]);
				if (role == null)
					throw new InvalidInputException("Unknown role, available options are `minigames` and `movienight`");

				Discord.removeRole(event.getAuthor().getId(), role);
				event.reply(event.getAuthor().getAsMention() + " You have unsubscribed from " + camelCase(role.name()));

			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
