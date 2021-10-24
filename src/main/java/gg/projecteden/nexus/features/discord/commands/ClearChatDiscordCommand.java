package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Utils;
import net.dv8tion.jda.api.entities.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class ClearChatDiscordCommand extends Command {

	public ClearChatDiscordCommand() {
		this.name = "clearchat";
		this.aliases = new String[]{ "cc", "clear" };
		this.guildOnly = true;
		this.requiredRole = "Staff";
	}

	private static final String regex = "--skip=\\d+";

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				int skip = 0;
				final Matcher matcher = Pattern.compile(regex).matcher(event.getArgs());
				if (matcher.find())
					skip = Integer.parseInt(matcher.group().split("=")[1]);

				final String amountString = event.getArgs().replaceAll(regex, "").trim();
				if (!Utils.isInt(amountString))
					throw new InvalidInputException("Amount of messages to delete not provided");

				final int amount = Math.min(20, Integer.parseInt(amountString));

				++skip; // skip command message // TODO Remove when using appcommand

				int skipIndex = 0;
				int deleteIndex = 0;
				for (Message message : event.getChannel().getIterableHistory()) {
					if (++skipIndex <= skip)
						continue;

					if (++deleteIndex > amount)
						return;

					message.delete().queue();
				}

				event.getMessage().delete().queue();
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
