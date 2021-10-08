package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NotEnoughMoneyException;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

import static gg.projecteden.nexus.utils.StringUtils.prettyMoney;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class PayDiscordCommand extends Command {

	public PayDiscordCommand() {
		this.name = "pay";
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				DiscordUser user = new DiscordUserService().checkVerified(event.getAuthor().getId());

				String[] args = event.getArgs().split(" ");
				if (args.length < 2 || !Utils.isDouble(args[1]))
					throw new InvalidInputException("Correct usage: `/pay <player> <amount> [shopGroup] [reason...]`");

				OfflinePlayer player = PlayerUtils.getPlayer(user.getUuid());
				OfflinePlayer target = PlayerUtils.getPlayer(args[0]);
				double amount = Double.parseDouble(args[1]);
				ShopGroup shopGroup = ShopGroup.SURVIVAL;
				String reason = null;
				if (args.length > 2)
					shopGroup = ShopGroup.valueOf(args[2].toUpperCase());
				if (args.length > 3)
					reason = event.getArgs().split(" ", 4)[3];

				if (player.getUniqueId().equals(target.getUniqueId()))
					throw new InvalidInputException("You cannot pay yourself");

				if (amount < .01)
					throw new InvalidInputException("Amount must be greater than $0.01");

				try {
					ShopGroup finalShopGroup = shopGroup;
					String finalReason = reason;
					Tasks.sync(() ->
							new BankerService().transfer(player, target, BigDecimal.valueOf(amount), finalShopGroup,
									TransactionCause.PAY.of(player, target, BigDecimal.valueOf(amount), finalShopGroup, finalReason)));
				} catch (NegativeBalanceException ex) {
					throw new NotEnoughMoneyException();
				}

				String formatted = prettyMoney(amount);
				PlayerUtils.send(target, "&a" + formatted + " has been received from " + Nickname.of(player));

				event.reply("Successfully sent " + formatted + " to " + Nickname.of(target));
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}

}
