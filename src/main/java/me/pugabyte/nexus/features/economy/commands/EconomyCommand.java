package me.pugabyte.nexus.features.economy.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.utils.StringUtils;

import java.math.BigDecimal;

import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

@Aliases("eco")
public class EconomyCommand extends CustomCommand {
	private final BankerService service = new BankerService();

	public EconomyCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Path("set <player> <balance>")
	void set(Banker banker, BigDecimal balance) {
		banker.setBalance(balance);
		service.save(banker);
		send(PREFIX + "Set &e" + banker.getName() + "'s &3balance to &e" + banker.getBalanceFormatted());
	}

	@Path("give <player> <balance>")
	void give(Banker banker, BigDecimal balance) {
		banker.deposit(balance);
		service.save(banker);
		send(PREFIX + "Added &e" + prettyMoney(balance) + " &3to &e" + banker.getName() + "'s &3balance. New balance: &e" + banker.getBalanceFormatted());
	}

	@Path("take <player> <balance>")
	void take(Banker banker, BigDecimal balance) {
		banker.withdraw(balance);
		service.save(banker);
		send(PREFIX + "Removed &e" + prettyMoney(balance) + " &3from &e" + banker.getName() + "'s &3balance. New balance: &e" + banker.getBalanceFormatted());
	}

//	@Async
//	@Path("convertBalances")
//	void convertBalance() {
//		int wait = 0;
//		for (UUID uuid : Nexus.getEssentials().getUserMap().getAllUniqueUsers()) {
//			int count = 0;
//			User user = Nexus.getEssentials().getUser(uuid);
//			Banker banker = service.get(uuid);
//			if (user.getMoney().doubleValue() > 550 || user.getMoney().doubleValue() < 450) {
//				++count;
//				Tasks.wait(wait, () -> {
//					banker.setBalance(user.getMoney());
//					service.save(banker);
//				});
//				if (count > 200)
//					wait += 3;
//			}
//		}
//	}

}
