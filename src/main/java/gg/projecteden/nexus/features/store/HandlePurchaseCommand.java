package gg.projecteden.nexus.features.store;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.contributor.Contributor;
import gg.projecteden.nexus.models.contributor.Contributor.Purchase;
import gg.projecteden.nexus.models.contributor.ContributorService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.DiscordId.Role;
import gg.projecteden.utils.DiscordId.TextChannel;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.uuidFormat;

public class HandlePurchaseCommand extends CustomCommand {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

	public HandlePurchaseCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<data...>")
	void run(String data) {
		Nexus.log("Purchase caught; processing... " + data);

		String[] args = data.split("\\|");
		Purchase purchase = Purchase.builder()
				.id(UUID.randomUUID())
				.name(args[0])
				.uuid(UUID.fromString(uuidFormat(args[1])))
				.transactionId(args[2])
				.price(Double.parseDouble(args[3]))
				.currency(args[4])
				.timestamp(LocalDateTime.parse(args[6] + " " + args[5], formatter))
				.email(args[7])
				.ip(args[8])
				.packageId(args[9])
				.packagePrice(Double.parseDouble(args[10]))
				.packageExpiry(args[11])
				.packageName(args[12])
				.purchaserName(args[13])
				.purchaserUuid(UUID.fromString(uuidFormat(args[14])))
				.build();

		String discordMessage = purchase.toDiscordString();

		final ContributorService contributorService = new ContributorService();
		final Contributor contributor = contributorService.get(purchase.getPurchaserUuid());

		Package packageType = Package.getPackage(purchase.getPackageId());

		if (packageType == null)
			discordMessage += "\nPackage not recognized!";
		else {
			if (purchase.getPrice() > 0) {
				send(purchase.getUuid(), ("&eThank you for buying " + purchase.getPackageName() + "! " +
						"&3Your donation is &3&ogreatly &3appreciated and will be put to good use."));

				if (contributor.isBroadcasts())
					if (packageType == Package.CUSTOM_DONATION) {
						Koda.say("Thank you for your custom donation, " + purchase.getNickname() + "! " +
								"We greatly appreciate your selfless contribution &4❤");
						// this is not necessarily what they donated
						// if they make a custom donation and purchase items at the same
						// time, i have no way to break down the price from the payload
						// but that is very rare, so its better than nothing i guess
						contributor.giveCredit(purchase.getPrice());
					} else
						Koda.say("Thank you for your purchase, " + purchase.getNickname() + "! " +
								"Enjoy your " + purchase.getPackageName() + " perk!");

				if (StringUtils.isV4Uuid(purchase.getPurchaserUuid())) {
					new BadgeUserService().edit(purchase.getPurchaserUuid(), badgeUser -> badgeUser.give(Badge.SUPPORTER));

					DiscordUser user = new DiscordUserService().get(purchase.getPurchaserUuid());
					if (user.getUserId() != null)
						Discord.addRole(user.getUserId(), Role.SUPPORTER);
					else
						discordMessage += "\nUser does not have a linked discord account";
				}
			} else {
				send(purchase.getUuid(), "Your free " + purchase.getPackageName() + " has been successfully processed, enjoy!");
			}

			packageType.apply(purchase.getUuid());

			discordMessage += "\nPurchase successfully processed.";
		}

		Discord.send(discordMessage, TextChannel.ADMIN_LOG);

		contributor.add(purchase);
		contributorService.save(contributor);
	}

}
