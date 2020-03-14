package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.poof.Poof;
import me.pugabyte.bncore.models.poof.PoofService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class PoofHereCommand extends CustomCommand {

	PoofService service = new PoofService();

	public PoofHereCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void player(Player target) {
		//if (target == player().getPlayer()) error("You cannot poof to yourself");
		Poof request = new Poof(player().getUniqueId().toString(), target.getUniqueId().toString(), player().getLocation(), target.getLocation(), LocalDateTime.now());
		service.save(request);
		send(json("&ePoof-here &3request sent to " + target.getName() + ". ").next("&eClick to cancel").command("poof cancel"));
		send(target, "  &e" + player().getName() + " &3is asking you to poof &eto them&3.");
		send(target, json("&3Click one || &a&lAccept")
				.command("poofhere accept")
				.hover("&eClick &3to accept")
				.group()
				.next("  &3||  ")
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
		if (request == null)
			error("You do not have any pending Poof-here requests");
		Player receiver = Utils.getPlayer(UUID.fromString(request.getReceiver())).getPlayer();
		receiver.teleport(request.getSenderLocation());
		send(receiver, "&3You accepted &e" + receiver.getName() + "'s &3poof-here request");
		send("&e" + Utils.getPlayer(UUID.fromString(request.getReceiver())).getName() + " &3accepted your poof-here request");
		service.remove(request);
	}

	@Path("deny")
	void deny() {
		Poof request = service.getByReceiver(player().getPlayer());
		if (request == null)
			error("You do not have any pending Poof-here requests");
		Player receiver = Utils.getPlayer(UUID.fromString(request.getReceiver())).getPlayer();
		send(receiver, "&3You denied &e" + receiver.getName() + "'s &3poof-here request");
		send("&e" + Utils.getPlayer(UUID.fromString(request.getReceiver())).getName() + " &3denied your poof-here request");
		service.remove(request);
	}

	@Path("cancel")
	void cancel() {
		Poof request = service.getBySender(player().getPlayer());
		if (request == null)
			error("You do not have any pending Poof-here requests");
		Player receiver = Utils.getPlayer(UUID.fromString(request.getReceiver())).getPlayer();
		Player sender = Utils.getPlayer(UUID.fromString(request.getSender())).getPlayer();
		send(receiver, "&e" + sender.getName() + " &3canceled their poof-here request");
		send("&3You canceled your poof-here request to &e" + receiver.getName());
		service.remove(request);
	}

}
