package me.pugabyte.bncore.features.discord.commands;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import java.text.NumberFormat;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

@HandledBy(Bot.KODA)
public class PayDiscordCommand extends Command {

	public PayDiscordCommand() {
		this.name = "pay";
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				DiscordUser user = new DiscordService().checkVerified(event.getAuthor().getId());

				String[] args = event.getArgs().split(" ");
				if (args.length != 2 || !Utils.isDouble(args[1]))
					throw new InvalidInputException("Correct usage: `/pay <player> <amount>`");

				OfflinePlayer player = Utils.getPlayer(user.getUuid());
				OfflinePlayer target = Utils.getPlayer(args[0]);
				double amount = Double.parseDouble(args[1]);

				if (player.getUniqueId().equals(target.getUniqueId()))
					throw new InvalidInputException("You cannot pay yourself");

				if (amount < 0)
					throw new InvalidInputException("Amount must be greater than $0");

				EconomyResponse withdrawal = BNCore.getEcon().withdrawPlayer(player, amount);
				if (!withdrawal.transactionSuccess())
					throw new InvalidInputException("You do not have enough money to complete this transaction ("
							+ NumberFormat.getCurrencyInstance().format(BNCore.getEcon().getBalance(player)) + ")");

				EconomyResponse deposit = BNCore.getEcon().depositPlayer(target, amount);
				if (!deposit.transactionSuccess())
					if (!isNullOrEmpty(deposit.errorMessage))
						throw new InvalidInputException(deposit.errorMessage);
					else
						throw new InvalidInputException("Transaction was not successful");

				String formatted = NumberFormat.getCurrencyInstance().format(amount);
				if (target.isOnline() && target.getPlayer() != null)
					Utils.send(target, "&a" + formatted + " has been received from " + player.getName());

				event.reply("Successfully sent " + formatted + " to " + target.getName());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

}
