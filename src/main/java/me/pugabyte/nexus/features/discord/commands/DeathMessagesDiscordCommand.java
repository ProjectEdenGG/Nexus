package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import eden.exceptions.EdenException;
import eden.utils.EnumUtils;
import eden.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.deathmessages.DeathMessages;
import me.pugabyte.nexus.models.deathmessages.DeathMessages.Behavior;
import me.pugabyte.nexus.models.deathmessages.DeathMessagesService;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class DeathMessagesDiscordCommand extends Command {

	public DeathMessagesDiscordCommand() {
		this.name = "deathmessages";
		this.aliases = new String[]{"deathmessage"};
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				Runnable usageError = () -> { throw new InvalidInputException("Usage: /deathmessages behavior <behavior> <player> [duration]"); };

				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					usageError.run();

				if (args.length < 3)
					usageError.run();

				Behavior behavior;
				try {
					behavior = Behavior.valueOf(args[1].toUpperCase());
				} catch (IllegalArgumentException ex) {
					throw new InvalidInputException("Valid behaviors are " + EnumUtils.valueNamesPretty(Behavior.class));
				}

				OfflinePlayer player = PlayerUtils.getPlayer(args[2]);
				Timespan duration = Timespan.of(0);
				if (args.length >= 4)
					duration = Timespan.of(String.join(" ", Arrays.copyOfRange(args, 3, args.length)));

				DeathMessagesService service = new DeathMessagesService();
				DeathMessages deathMessages = service.get(player);
				deathMessages.setBehavior(behavior);
				if (!duration.isNull())
					deathMessages.setExpiration(duration.fromNow());

				service.save(deathMessages);

				String message = "Set " + Nickname.of(player) + "'s death message behavior to "
						+ camelCase(behavior) + (duration.isNull() ? "" : " for " + duration.format());

				event.reply(StringUtils.getDiscordPrefix("DeathMessages") + message);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
