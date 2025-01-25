package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Aliases({"baltop", "moneytop", "btop"})
public class BalanceTopCommand extends CustomCommand {
	private final BankerService service = new BankerService();
	private static final Set<UUID> processing = new HashSet<>();

	public BalanceTopCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Economy");
	}

	@Async
	@Path("[page] [--world]")
	@Description("View a world's balance leaderboard")
	void baltop(@Arg(value = "1", min = 1) int page, @Switch @Arg("current") ShopGroup world) {
		if (processing.contains(uuid()))
			error("Please wait for your last command to finish");
		else
			processing.add(uuid());

		if (world == null)
			error("World cannot be null");

		List<Banker> bankers = service.getAll().stream()
				.filter(banker -> !banker.isMarket() && banker.getBalance(world).compareTo(BigDecimal.valueOf(500)) != 0)
				.sorted(Comparator.comparing(banker -> banker.getBalance(world), Comparator.reverseOrder()))
				.collect(Collectors.toList());

		if (bankers.isEmpty()) {
			error("No balances found");
			processing.remove(uuid());
		}

		double sum = bankers.stream().mapToDouble(banker -> banker.getBalance(world).doubleValue()).sum();

		send(PREFIX + "Top " + camelCase(world) + " balances  &3|  Total: &e" + StringUtils.prettyMoney(sum));
		new Paginator<Banker>()
			.values(bankers)
			.formatter((banker, index) -> json(index + " &e" + banker.getNickname() + " &7- " + banker.getBalanceFormatted(world)))
			.command("/baltop --world=" + world.name().toLowerCase())
			.page(page)
			.send();

		processing.remove(uuid());
	}

}
