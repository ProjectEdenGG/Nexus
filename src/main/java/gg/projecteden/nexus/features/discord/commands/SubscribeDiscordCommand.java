package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.discord.DiscordId;
import gg.projecteden.nexus.features.discord.DiscordId.Role;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Tasks;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class SubscribeDiscordCommand extends Command {

	public SubscribeDiscordCommand() {
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
					throw new InvalidInputException("Unknown role, available options are `minigames`, `movienight` and `coding`");

				Discord.addRole(event.getAuthor().getId(), role);
				event.reply(event.getAuthor().getAsMention() + " You have subscribed to " + camelCase(role.name()));
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

	static DiscordId.Role getRole(String input) {
		return switch (input) {
			case "minigames", "minigame", "minigamesnews", "minigamenews" -> Role.MINIGAME_NEWS;
			case "movienight", "theatre", "moviegoer", "moviegoers" -> Role.MOVIE_GOERS;
			case "coding", "codinglessons" -> Role.CODING_LESSONS;
			default -> null;
		};

	}

}
