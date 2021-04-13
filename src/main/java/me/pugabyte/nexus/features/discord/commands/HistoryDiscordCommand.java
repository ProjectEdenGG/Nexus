package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import joptsimple.internal.Strings;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.HandledBy;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.litebans.LiteBansService;
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
				OfflinePlayer player = null;

				String[] args = event.getArgs().split(" ");
				if (!(args.length > 0 && !Strings.isNullOrEmpty(args[0])))
					throw new InvalidInputException("Correct usage: `/history <player>`");
				else
					player = PlayerUtils.getPlayer(args[0]);

				if (new LiteBansService().getHistory(player.getUniqueId().toString()) > 0)
					event.reply("https://bans.bnn.gg/history.php?uuid=" + player.getUniqueId().toString());
				else
					event.reply("No history found");
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}

}
