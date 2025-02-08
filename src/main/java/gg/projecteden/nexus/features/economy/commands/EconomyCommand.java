package gg.projecteden.nexus.features.economy.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

import java.math.BigDecimal;

@Aliases("eco")
public class EconomyCommand extends CustomCommand {
	public static final String PREFIX = StringUtils.getPrefix("Economy");
	private final BankerService service = new BankerService();

	public EconomyCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Override
	@Description("View general information about the server's economy")
	public void help() {
		line(3);
		send("&3Each player starts out with &e$500&3.");
		send("&3There are multiple ways to make money, such as:");
		line();
		send(json("&3[+] &eSelling items at the &c/market &3in the &eresource world").hover("&3Non auto-farmable resources sell for more in this world").suggest("/warp resource"));
		send(json("&3[+] &eSelling items to other players").command("/economy selling").hover("&3Click for a few tips on how to sell to other players"));
		// TODO Jobs
		send(json("&3[+] &eKilling mobs").url(WikiType.SERVER.getBasePath() + "Economy#Mobs").hover("&3Click to open the wiki section on mobs"));
		send("&3[+] &eWorking for other players");
		send(json("&3[+] &eVoting").command("/vote"));
		send(json("&3[+] &eWinning Events").hover("&3Make sure to check Discord's &e#announcements &3channel and the home page for upcoming events!"));
		line();
		send(json("&3[+] &eEconomy related commands").command("/economy commands"));
	}

	@Path("selling")
	@Description("View ways to sell items to another player")
	void sell() {
		line(3);
		send("&3There are a few ways you can trade with other players:");
		send(json("&3[+] &eShops").url(WikiType.SERVER.getBasePath() + "Shops").hover("&3Click to open the wiki section on Shops."));
		send(json("&3[+] &eAdvertise in &c#survival &eon our " + SocialMediaSite.DISCORD.getColor() + "Discord").command("/discord"));
		send("&3[+] &eSimply ask in chat!");
		line();
		send(json("&3 « &eClick here to return to the economy menu.").command("/economy"));
	}

	@Path("commands")
	@Description("View economy related commands")
	void commands() {
		line(3);
		send("&eEconomy Related Commands");
		send(json("&3[+] &c/bal [player] &7- View your or another player's balance").suggest("/bal "));
		send(json("&3[+] &c/baltop &7- View the richest people on the server").suggest("/baltop"));
		send(json("&3[+] &c/pay <player> <amount> &7- Send a player money").suggest("/pay "));
//		send(json("&3[+] &c/jobs").hover("&3").suggest("/jobs")); // TODO Jobs
		send(json("&3[+] &c/txn history &7- View your transaction history").suggest("/txn history"));
		line();
		send(json("&3 « &eClick here to return to the economy menu.").command("/economy"));
	}

	@Path("set <player> <balance> [cause] [reason...] [--world]")
	@Description("Modify a player's balance")
	@Permission(Group.ADMIN)
	void set(Banker banker, BigDecimal balance, @Arg("server") TransactionCause cause, String reason, @Switch  @Arg("current") ShopGroup world) {
		service.setBalance(cause.of(null, banker, balance, world, reason));
		send(PREFIX + "Set &e" + banker.getNickname() + "'s &3balance to &e" + banker.getBalanceFormatted(world));
	}

	@Path("give <player> <balance> [cause] [reason...] [--world]")
	@Description("Modify a player's balance")
	@Permission(Group.ADMIN)
	void give(Banker banker, BigDecimal balance, @Arg("server") TransactionCause cause, String reason, @Switch @Arg("current") ShopGroup world) {
		service.deposit(cause.of(null, banker, balance, world, reason));
		send(PREFIX + "Added &e" + StringUtils.prettyMoney(balance) + " &3to &e" + banker.getNickname() + "'s &3balance. New balance: &e" + banker.getBalanceFormatted(world));
	}

	@Path("take <player> <balance> [cause] [reason...] [--world]")
	@Description("Modify a player's balance")
	@Permission(Group.ADMIN)
	void take(Banker banker, BigDecimal balance, @Arg("server") TransactionCause cause, String reason, @Switch @Arg("current") ShopGroup world) {
		service.withdraw(cause.of(null, banker, balance.multiply(new BigDecimal(-1)), world, reason));
		send(PREFIX + "Removed &e" + StringUtils.prettyMoney(balance) + " &3from &e" + banker.getNickname() + "'s &3balance. New balance: &e" + banker.getBalanceFormatted(world));
	}

	@ConverterFor(ShopGroup.class)
	ShopGroup convertToShopGroup(String value) {
		if ("current".equalsIgnoreCase(value)) {
			if (isConsole())
				error("You must specify a shop group");
			ShopGroup shopGroup = ShopGroup.of(world());
			if (shopGroup == null)
				return ShopGroup.SURVIVAL;
			return shopGroup;
		}

		return (ShopGroup) convertToEnum(value, ShopGroup.class);
	}

}
