package me.pugabyte.nexus.features.discord.commands.justice;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import eden.exceptions.EdenException;
import joptsimple.internal.Strings;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

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
					event.reply("<https://justice.projecteden.gg/history/" + player.getName() + ">");
				else
					event.reply("No history found");
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
