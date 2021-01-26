package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

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

				OfflinePlayer player = PlayerUtils.getPlayer(user.getUuid());
				OfflinePlayer target = PlayerUtils.getPlayer(args[0]);
				double amount = Double.parseDouble(args[1]);

				if (player.getUniqueId().equals(target.getUniqueId()))
					throw new InvalidInputException("You cannot pay yourself");

				if (amount < 0)
					throw new InvalidInputException("Amount must be greater than $0");

				EconomyResponse withdrawal = Nexus.getEcon().withdrawPlayer(player, amount);
				if (!withdrawal.transactionSuccess())
					throw new InvalidInputException("You do not have enough money to complete this transaction ("
							+ NumberFormat.getCurrencyInstance().format(Nexus.getEcon().getBalance(player)) + ")");

				EconomyResponse deposit = Nexus.getEcon().depositPlayer(target, amount);
				if (!deposit.transactionSuccess())
					if (!isNullOrEmpty(deposit.errorMessage))
						throw new InvalidInputException(deposit.errorMessage);
					else
						throw new InvalidInputException("Transaction was not successful");

				String formatted = NumberFormat.getCurrencyInstance().format(amount);
				if (target.isOnline() && target.getPlayer() != null)
					PlayerUtils.send(target, "&a" + formatted + " has been received from " + player.getName());

				event.reply("Successfully sent " + formatted + " to " + target.getName());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}

}
