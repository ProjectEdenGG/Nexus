package me.pugabyte.bncore.features.store;

import com.google.gson.Gson;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.Koda;
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
import me.pugabyte.bncore.models.task.Task;
import me.pugabyte.bncore.models.task.TaskService;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.uuidFormat;

public class HandlePurchaseCommand extends CustomCommand {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

	public HandlePurchaseCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.MINUTE, () -> {
			TaskService service = new TaskService();
			List<Task> tasks = service.process("package-expire");
			tasks.forEach(task -> {
				Map<String, String> map = new Gson().fromJson(task.getData(), Map.class);
				String uuid = map.get("uuid");
				String packageId = map.get("packageId");
				Package packageType = Package.getPackage(packageId);
				if (packageType == null) {
					BNCore.severe("Tried to expire a package that doesn't exist: UUID: " + uuid + ", PackageId: " + packageId);
					return;
				}

				packageType.getPermissions().forEach(permission -> BNCore.getPerms().playerRemove(null, Utils.getPlayer(uuid), permission));
				packageType.getExpirationCommands().stream()
						.map(StringUtils::trimFirst)
						.map(command -> command.replaceAll("\\[player]", Utils.getPlayer(uuid).getName()))
						.forEach(Utils::runCommandAsConsole);

				service.complete(task);
			});
		});
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
				.packageId(args[9])
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
					BNCore.getPerms().playerAdd(null, Utils.getPlayer(purchase.getPurchaserUuid()), "donated");

					DiscordUser user = new DiscordService().get(purchase.getPurchaserUuid());
					if (user.getUserId() != null)
						Discord.addRole(user.getUserId(), Role.SUPPORTER);
					else
						discordMessage += "\nUser does not have a linked discord account";
				}
			}

			OfflinePlayer permsUser = Utils.getPlayer(purchase.getName().length() < 2 ? purchase.getPurchaserName() : purchase.getName());
			packageType.getPermissions().forEach(permission ->
					runCommandAsConsole("lp user " + permsUser.getUniqueId().toString() + " permission set " + permission + " true"));

			packageType.getCommands().stream()
					.map(command -> command.replaceAll("\\[player]", Utils.getPlayer(purchase.getUuid()).getName()))
					.forEach(Utils::runCommandAsConsole);

			if (packageType.getExpirationDays() > 0)
				new TaskService().save(new Task("package-expire", new HashMap<String, Object>() {{
					put("uuid", purchase.getUuid());
					put("packageId", String.valueOf(purchase.getPackageId()));
				}}, LocalDateTime.now().plusDays(packageType.getExpirationDays())));

			discordMessage += "\nPurchase successfully processed.";
		}

		Discord.send(discordMessage, Channel.ADMIN_LOG);
		new PurchaseService().save(purchase);
	}

}
