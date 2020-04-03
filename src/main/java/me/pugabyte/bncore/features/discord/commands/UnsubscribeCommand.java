package me.pugabyte.bncore.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.ChatColor;

import static me.pugabyte.bncore.features.discord.commands.SubscribeCommand.getRole;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public class UnsubscribeCommand extends Command {

	public UnsubscribeCommand() {
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
				ex.printStackTrace();
				event.reply(ChatColor.stripColor(ex.getMessage()));
			}
		});
	}

}
