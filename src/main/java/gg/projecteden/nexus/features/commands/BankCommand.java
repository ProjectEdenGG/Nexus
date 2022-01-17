package gg.projecteden.nexus.features.commands;

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
import gg.projecteden.utils.StringUtils;
import lombok.NonNull;

import java.math.BigDecimal;
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
		// Store Credit
		Contributor contributor = new ContributorService().get(nerd);
		send("&3Store Credit: &e" + contributor.getCreditFormatted());

		// Vote Points
		Voter voter = new VoterService().get(nerd);
		send("&3Vote Points: &e" + voter.getPoints());

		// Event Store
		EventUser eventUser = new EventUserService().get(nerd);
		send("&3Event Tokens: &e" + eventUser.getTokens());

		// Minigames
		PerkOwner perkOwner = new PerkOwnerService().get(nerd);
		send("&3Minigame Tokens: &e" + perkOwner.getTokens());

		// Economy
		send("&3Economy Balances: &e");
		Banker banker = new BankerService().get(nerd);
		Map<ShopGroup, BigDecimal> balances = banker.getBalances();
		for (ShopGroup shopGroup : balances.keySet()) {
			String name = StringUtils.camelCase(shopGroup);
			String bal = banker.getBalanceFormatted(shopGroup);

			send("&3 - " + name + ": &e" + bal);
		}

		line();
	}

}
