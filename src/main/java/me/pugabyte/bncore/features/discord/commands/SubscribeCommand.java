package me.pugabyte.bncore.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.ChatColor;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@HandledBy(Bot.KODA)
public class SubscribeCommand extends Command {

	public SubscribeCommand() {
		this.name = "subscribe";
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/subscribe <role>`");

				Role role = getRole(args[0]);
				if (role == null)
					throw new InvalidInputException("Unknown role, available options are `minigames` and `movienight`");

				Discord.addRole(event.getAuthor().getId(), role);
				event.reply(event.getAuthor().getAsMention() + " You have subscribed to " + camelCase(role.name()));
			} catch (Exception ex) {
				ex.printStackTrace();
				event.reply(ChatColor.stripColor(ex.getMessage()));
			}
		});
	}

	static DiscordId.Role getRole(String input) {
		switch (input) {
			case "minigames":
			case "minigame":
			case "minigamesnews":
			case "minigamenews":
				return Role.MINIGAME_NEWS;
			case "movienight":
			case "theatre":
			case "moviegoer":
			case "moviegoers":
				return Role.MOVIE_GOERS;
		}

		return null;
	}

}
