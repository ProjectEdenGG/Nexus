package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;

import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class HoursDiscordCommand extends Command {

	public HoursDiscordCommand() {
		this.name = "hours";
		this.aliases = new String[]{"playtime", "days", "minutes", "seconds"};
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				UUID playerUUID;

				String[] args = event.getArgs().split(" ");
				if (args.length > 0 && !StringUtils.isNullOrEmpty(args[0]))
					playerUUID = PlayerUtils.getPlayer(args[0]).getUniqueId();
				else
					try {
						DiscordUser user = new DiscordUserService().checkVerified(event.getAuthor().getId());
						playerUUID = user.getUniqueId();
					} catch (InvalidInputException ex) {
						throw new InvalidInputException("You must either link your Discord and Minecraft accounts or supply a name");
					}

				HoursService service = new HoursService();
				Hours hours = service.get(playerUUID);

				String message = "**[Hours]** " + hours.getName() + "'s in-game playtime";
				message += System.lineSeparator() + "Total: **" + TimespanBuilder.of(hours.getTotal()).noneDisplay(true).format() + "**";
				message += System.lineSeparator() + "- Today: **" + TimespanBuilder.of(hours.getDaily()).noneDisplay(true).format() + "**";
				message += System.lineSeparator() + "- This month: **" + TimespanBuilder.of(hours.getMonthly()).noneDisplay(true).format() + "**";
				message += System.lineSeparator() + "- This year: **" + TimespanBuilder.of(hours.getYearly()).noneDisplay(true).format() + "**";

				event.reply(message);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
