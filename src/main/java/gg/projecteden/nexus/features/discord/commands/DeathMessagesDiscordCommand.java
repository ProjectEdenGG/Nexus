package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessages.Behavior;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.TimeUtils.Timespan;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.utils.StringUtils.camelCase;

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
