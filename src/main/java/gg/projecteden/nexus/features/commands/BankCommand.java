package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Aliases("wallet")
public class BankCommand extends CustomCommand {

	public BankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	@Description("Displays all currencies owned by the player")
	void wallet(@Arg("self") Nerd nerd) {
		send(PREFIX + "&e" + nerd.getNickname() + "&3's Wallet: ");
		for (String line : getLines(nerd(), nerd)) {
			send(line);
		}

		line();
	}

	public static List<String> getLines(Nerd viewer, Nerd nerd) {
		List<String> lines = new ArrayList<>();

		if (viewer.getRank().isStaff() || PlayerUtils.isSelf(viewer, nerd)) {
			Contributor contributor = new ContributorService().get(nerd);
			lines.add("&3Store Credit: &e" + contributor.getCreditFormatted());
		}

		// Vote Points
		Voter voter = new VoterService().get(nerd);
		lines.add("&3Vote Points: &e" + voter.getPoints());

		// Event Store
		EventUser eventUser = new EventUserService().get(nerd);
		lines.add("&3Event Tokens: &e" + eventUser.getTokens());

		// Minigames
		PerkOwner perkOwner = new PerkOwnerService().get(nerd);
		lines.add("&3Minigame Tokens: &e" + perkOwner.getTokens());

		// Economy
		lines.add("&3Economy Balances: &e");
		Banker banker = new BankerService().get(nerd);
		Map<ShopGroup, BigDecimal> balances = banker.getBalances();
		for (ShopGroup shopGroup : balances.keySet()) {
			String name = StringUtils.camelCase(shopGroup);
			String bal = banker.getBalanceFormatted(shopGroup);

			lines.add("&3 - " + name + ": &e" + bal);
		}

		return lines;
	}

}
