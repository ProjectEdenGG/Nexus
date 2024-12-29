package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.api.discord.appcommands.AppCommandEvent;
import gg.projecteden.api.discord.appcommands.annotations.Command;
import gg.projecteden.api.discord.appcommands.annotations.Desc;
import gg.projecteden.api.discord.appcommands.annotations.Optional;
import gg.projecteden.nexus.features.discord.commands.common.NexusAppCommand;
import gg.projecteden.nexus.features.discord.commands.common.annotations.Verify;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NotEnoughMoneyException;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;

import java.math.BigDecimal;

@Verify
@Command("Pay a player")
public class PayAppCommand extends NexusAppCommand {

	public PayAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "Pay a player", literals = false)
	void run(
		@Desc("Player") Banker player,
		@Desc("Amount") double amount,
		@Desc("Gamemode") @Optional("Survival") ShopGroup gamemode,
		@Desc("Reason") @Optional String reason
	) {
		Tasks.sync(() -> {
			if (isSelf(player))
				throw new InvalidInputException("You cannot pay yourself");

			if (amount < .01)
				throw new InvalidInputException("Amount must be greater than $0.01");

			try {
				final Transaction transaction = TransactionCause.PAY.of(user(), player, BigDecimal.valueOf(amount), gamemode, reason);
				new BankerService().transfer(user(), player, BigDecimal.valueOf(amount), gamemode, transaction);
			} catch (NegativeBalanceException ex) {
				throw new NotEnoughMoneyException();
			}

			String formatted = StringUtils.prettyMoney(amount);
			PlayerUtils.send(player, "&a" + formatted + " has been received from " + nickname());

			reply("Successfully sent " + formatted + " to " + player.getNickname());
		});
	}

}
