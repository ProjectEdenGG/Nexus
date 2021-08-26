package gg.projecteden.nexus.features.discord.commands.justice;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.justice.Justice;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import joptsimple.internal.Strings;
import org.bukkit.OfflinePlayer;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class HistoryDiscordCommand extends Command {

	public HistoryDiscordCommand() {
		this.name = "history";
		this.guildOnly = true;
		this.requiredRole = "Staff";
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (!(args.length > 0 && !Strings.isNullOrEmpty(args[0])))
					throw new InvalidInputException("Correct usage: `/history <player>`");

				OfflinePlayer player = PlayerUtils.getPlayer(args[0]);

				if (Punishments.of(player).hasHistory())
					event.reply("<" + Justice.URL + "/history/" + Name.of(player) + ">");
				else
					event.reply("No history found for " + Nickname.of(player));
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
