package me.pugabyte.bncore.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class BalanceDiscordCommand extends Command {

	public BalanceDiscordCommand() {
		this.name = "balance";
		this.aliases = new String[]{"bal", "money"};
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				if (!event.getMember().getRoles().contains(Role.VERIFIED.get()))
					throw new InvalidInputException("You must link your Discord and Minecraft accounts before using this command");

				DiscordUser user = new DiscordService().getFromUserId(event.getAuthor().getId());
				OfflinePlayer player = Utils.getPlayer(user.getUuid());

				String[] args = event.getArgs().split(" ");
				if (args.length > 0 && !isNullOrEmpty(args[0]))
					player = Utils.getPlayer(args[0]);

				String formatted = NumberFormat.getCurrencyInstance().format(BNCore.getEcon().getBalance(player));
				boolean isSelf = user.getUuid().equals(player.getUniqueId().toString());
				event.reply("Balance" + (isSelf ? "" : " of " + player.getName()) + ": " + formatted);
			} catch (Throwable ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

}
