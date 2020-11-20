package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.framework.exceptions.BNException;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;

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
				OfflinePlayer player = Utils.getPlayer(user.getUuid());

				String[] args = event.getArgs().split(" ");
				if (args.length > 0 && !isNullOrEmpty(args[0]))
					player = Utils.getPlayer(args[0]);

				String formatted = NumberFormat.getCurrencyInstance().format(Nexus.getEcon().getBalance(player));
				boolean isSelf = user.getUuid().equals(player.getUniqueId().toString());
				event.reply("Balance" + (isSelf ? "" : " of " + player.getName()) + ": " + formatted);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

}
