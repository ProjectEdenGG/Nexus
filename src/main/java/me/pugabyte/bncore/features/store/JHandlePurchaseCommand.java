package me.pugabyte.bncore.features.store;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.koda.Koda;
import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.purchase.Purchase;
import me.pugabyte.bncore.models.purchase.PurchaseService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.uuidFormat;

public class JHandlePurchaseCommand extends CustomCommand {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

	public JHandlePurchaseCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<data...>")
	void run(String data) {
		BNCore.log("Purchase caught; processing... " + data);

		String[] args = data.split("\\|");
		Purchase purchase = Purchase.builder()
				.name(args[0])
				.uuid(uuidFormat(args[1]))
				.transactionId(args[2])
				.price(Double.parseDouble(args[3]))
				.currency(args[4])
				.timestamp(LocalDateTime.parse(args[6] + " " + args[5], formatter))
				.email(args[7])
				.ip(args[8])
				.packageId(Integer.parseInt(args[9]))
				.packagePrice(Double.parseDouble(args[10]))
				.packageExpiry(args[11])
				.packageName(args[12])
				.purchaserName(args[13])
				.purchaserUuid(uuidFormat(args[14]))
				.build();

		String discordMessage = purchase.toDiscordString();

		Package packageType = Package.getPackage(purchase.getPackageId());
		if (packageType == null)
			discordMessage += "\nPackage not recognized!";
		else {
			if (purchase.getPrice() > 0) {
				OfflinePlayer player = Utils.getPlayer(purchase.getUuid());
				if (player.isOnline())
					player.getPlayer().sendMessage(colorize("&eThank you for buying " + purchase.getPackageName() + "! " +
							"&3Your donation is &3&ogreatly &3appreciated and will be put to good use."));

				if (packageType == Package.CUSTOM_DONATION)
					Koda.say("Thank you for your custom donation, " + purchase.getName() + "! " +
							"We greatly appreciate your selfless contribution &4❤");
				else
					Koda.say("Thank you for your purchase, " + purchase.getName() + "! " +
							"Enjoy your " + purchase.getPackageName() + " perk!");

				if (purchase.getPurchaserUuid().length() == 36) {
					PermissionsEx.getUser(purchase.getPurchaserName()).addPermission("donated");

					DiscordUser user = new DiscordService().get(purchase.getPurchaserUuid());
					if (user.getUserId() != null)
						Discord.addRole(user, Role.SUPPORTER);
					else
						discordMessage += "\nUser does not have a linked discord account";
				}
			}

			PermissionUser pexUser = PermissionsEx.getUser(purchase.getName().length() < 2 ? purchase.getPurchaserName() : purchase.getName());
			packageType.getPermissions().forEach(pexUser::addPermission);
			packageType.getCommands().stream().map(StringUtils::noSlash).forEach(Utils::runConsoleCommand);
			// TODO: Expiry

			discordMessage += "\nPurchase successfully processed.";
		}

		Discord.send(discordMessage, Channel.ADMIN_LOG);
		new PurchaseService().save(purchase);
	}

}
