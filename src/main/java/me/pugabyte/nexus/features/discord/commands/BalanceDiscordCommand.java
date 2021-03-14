package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class BalanceDiscordCommand extends Command {

	public BalanceDiscordCommand() {
		this.name = "balance";
		this.aliases = new String[]{"bal", "money"};
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				DiscordUser user = new DiscordService().checkVerified(event.getAuthor().getId());
				OfflinePlayer player = PlayerUtils.getPlayer(user.getUuid());

				String[] args = event.getArgs().split(" ");
				if (args.length > 0 && !isNullOrEmpty(args[0]))
					player = PlayerUtils.getPlayer(args[0]);

				String formatted = new BankerService().getBalanceFormatted(player);
				boolean isSelf = user.getUuid().equals(player.getUniqueId().toString());
				event.reply("Balance" + (isSelf ? "" : " of " + player.getName()) + ": " + formatted);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}

}
