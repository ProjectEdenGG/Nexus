package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import eden.exceptions.EdenException;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.utils.Tasks;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class SocialMediaDiscordCommand extends Command {

	public SocialMediaDiscordCommand() {
		this.name = "socialmedia";
		this.aliases = new String[]{"sm"};
		this.guildOnly = true;
		this.requiredRole = "Staff";
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {

			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
