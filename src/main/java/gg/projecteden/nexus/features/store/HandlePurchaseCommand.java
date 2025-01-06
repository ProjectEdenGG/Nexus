package gg.projecteden.nexus.features.store;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.store.annotations.Category.StoreCategory;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.Contributor.Purchase;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@HideFromWiki
public class HandlePurchaseCommand extends CustomCommand {
	private final String PREFIX = StringUtils.getPrefix("Store");
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
				.uuid(UUID.fromString(UUIDUtils.uuidFormat(args[1])))
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
				.purchaserUuid(UUID.fromString(UUIDUtils.uuidFormat(args[14])))
				.build();

		String discordMessage = purchase.toDiscordString();

		final ContributorService contributorService = new ContributorService();
		final Contributor contributor = contributorService.get(purchase.getPurchaserUuid());

		Package packageType = Package.getPackage(purchase.getPackageId());

		if (packageType == null)
			discordMessage += "\nPackage not recognized!";
		else {
			if (purchase.getPrice() > 0) {
				send(purchase.getUuid(), PREFIX + "Thank you for buying &e" + purchase.getPackageName() + "&3! " +
						"Your contribution is &3&ogreatly &3appreciated and will be put to good use");

				if (contributor.isBroadcasts())
					if (packageType == Package.STORE_CREDIT) {
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

				if (packageType.getCategory() == StoreCategory.BOOSTS)
					send(purchase.getUuid(), PREFIX + "Make sure you activate your boost with &c/boost menu");

				if (UUIDUtils.isV4Uuid(purchase.getPurchaserUuid())) {
					new BadgeUserService().edit(purchase.getPurchaserUuid(), badgeUser -> badgeUser.give(Badge.SUPPORTER));

					DiscordUser user = new DiscordUserService().get(purchase.getPurchaserUuid());
					if (user.getUserId() != null)
						Discord.addRole(user.getUserId(), Role.SUPPORTER);
					else
						discordMessage += "\nUser does not have a linked discord account";
				}
			} else {
				send(purchase.getUuid(), PREFIX + "Your free " + purchase.getPackageName() + " has been successfully processed, enjoy!");
			}

			packageType.apply(purchase.getUuid());

			discordMessage += "\nPurchase successfully processed";
		}

		Discord.send(discordMessage, TextChannel.ADMIN_LOG);

		contributor.add(purchase);
		contributorService.save(contributor);
	}

}
