package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import eden.exceptions.EdenException;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.Tasks;

import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class AltsDiscordCommand extends Command {

	public AltsDiscordCommand() {
		this.name = "alts";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(TextChannel.STAFF_BRIDGE.getId()))
			return;

		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/alts <player>`");

				Punishments player = Punishments.of(args[0]);

				// TODO Categorize by active type? See ingame /alts
				String alts = player.getAlts().stream()
						.map(Punishments::of).map(_player -> {
							if (player.getAnyActiveBan().isPresent())
								return "**" + _player.getName() + "**";
							else if (_player.isOnline())
								return "_" + _player.getName() + "_";
							else return _player.getName();
						}).distinct().collect(Collectors.joining(", "));

				event.reply("Alts of `" + player.getName() + "` [_Online_ Offline **Banned**]:" + System.lineSeparator() + alts);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}


}
