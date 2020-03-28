package me.pugabyte.bncore.features.commands.poof;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.models.poof.Poof;
import me.pugabyte.bncore.models.poof.PoofService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Aliases("tpahere")
public class PoofHereCommand extends CustomCommand {
	PoofService service = new PoofService();

	public PoofHereCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void player(Player target) {
		if (target == player().getPlayer())
			error("You cannot poof to yourself");

		Poof request = new Poof(player(), target);
		service.save(request);
		send(json("&ePoof-here &3request sent to " + target.getName() + ". ").next("&eClick to cancel").command("poof cancel"));
		send(target, "  &e" + player().getName() + " &3is asking you to poof &eto them&3.");
		send(target, json("&3  Click one  ||  &a&lAccept")
				.command("poofhere accept")
				.hover("&eClick &3to accept")
				.group()
				.next("  &3||  &3")
				.group()
				.next("&c&lDeny")
				.command("poofhere deny")
				.hover("&eClick &3to deny.")
				.group()
				.next("&3  ||"));

	}

	@Path("accept")
	void accept() {
		Poof request = service.getByReceiver(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof-here requests");

		OfflinePlayer receiver = request.getReceiverPlayer();
		OfflinePlayer sender = request.getSenderPlayer();
		if (!sender.isOnline())
			throw new PlayerNotOnlineException(sender);

		receiver.getPlayer().teleport(request.getSenderLocation());
		send("&3You accepted &e" + sender.getName() + "'s &3poof-here request");
		send(sender.getPlayer(), "&e" + receiver.getName() + " &3accepted your poof-here request");
		request.setExpired(true);
		service.save(request);
	}

	@Path("deny")
	void deny() {
		Poof request = service.getByReceiver(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof-here requests");

		OfflinePlayer receiver = request.getReceiverPlayer();
		OfflinePlayer sender = request.getSenderPlayer();
		if (!sender.isOnline())
			throw new PlayerNotOnlineException(sender);

		send("&3You denied &e" + sender.getName() + "'s &3poof-here request");
		send(sender.getPlayer(), "&e" + receiver.getName() + " &3denied your poof-here request");
		request.setExpired(true);
		service.save(request);
	}

	@Path("cancel")
	void cancel() {
		Poof request = service.getBySender(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof-here requests");

		OfflinePlayer receiver = request.getReceiverPlayer();
		OfflinePlayer sender = request.getSenderPlayer();
		if (!receiver.isOnline())
			throw new PlayerNotOnlineException(receiver);

		send(receiver.getPlayer(), "&e" + sender.getName() + " &3canceled their poof-here request");
		send("&3You canceled your poof-here request to &e" + receiver.getName());
		request.setExpired(true);
		service.save(request);
	}

}
