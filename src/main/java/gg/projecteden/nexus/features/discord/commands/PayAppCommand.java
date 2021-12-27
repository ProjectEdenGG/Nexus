package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Default;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NegativeBalanceException;
import gg.projecteden.nexus.framework.exceptions.preconfigured.NotEnoughMoneyException;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;

import java.math.BigDecimal;

import static gg.projecteden.nexus.utils.StringUtils.prettyMoney;

@Verify
@HandledBy(Bot.KODA)
public class PayAppCommand extends NexusAppCommand {

	public PayAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command("Pay a player")
	void run(
		@Desc("Player") Banker banker,
		@Desc("Amount") double amount,
		@Desc("Gamemode") @Default("Survival") ShopGroup shopGroup,
		@Desc("Reason") String reason
	) {
		if (isSelf(banker))
			throw new InvalidInputException("You cannot pay yourself");

		if (amount < .01)
			throw new InvalidInputException("Amount must be greater than $0.01");

		try {
			final Transaction transaction = TransactionCause.PAY.of(user(), banker, BigDecimal.valueOf(amount), shopGroup, reason);
			new BankerService().transfer(user(), banker, BigDecimal.valueOf(amount), shopGroup, transaction);
		} catch (NegativeBalanceException ex) {
			throw new NotEnoughMoneyException();
		}

		String formatted = prettyMoney(amount);
		PlayerUtils.send(banker, "&a" + formatted + " has been received from " + nickname());

		reply("Successfully sent " + formatted + " to " + banker.getNickname());
	}

}
