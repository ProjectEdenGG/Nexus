package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.poof.Poof;
import me.pugabyte.bncore.models.poof.PoofService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class PoofCommand extends CustomCommand {

	public PoofCommand(CommandEvent event) {
		super(event);
	}

	PoofService service = new PoofService();

	static {
		Tasks.repeatAsync(100, 60 * 20, () -> {
			PoofService poofService = new PoofService();
			poofService.getActivePoofs().forEach((poof -> {
				if (poof.getTimeSent().isBefore(LocalDateTime.now().minusMinutes(1))) {
					poof.setExpired(true);
					poofService.save(poof);
				}
			}));
		});
	}

	@Path("<player>")
	void player(Player target) {
		//if (target == player().getPlayer()) error("You cannot poof to yourself");
		Poof request = new Poof(player().getUniqueId().toString(), target.getUniqueId().toString(), player().getLocation(), target.getLocation(), LocalDateTime.now());
		service.save(request);
		send(json("&ePoof &3request sent to " + target.getName() + ". ").next("&eClick to cancel").command("poof cancel"));
		send(target, "  &e" + player().getName() + " &3is asking to poof &eto you&3.");
		send(target, json("&3Click one || &a&lAccept")
				.command("poof accept")
				.hover("&eClick &3to accept")
				.group()
				.next("  &3||  ")
				.group()
				.next("&c&lDeny")
				.command("poof deny")
				.hover("&eClick &3to deny.")
				.group()
				.next("&3  ||"));

	}

	@Path("accept")
	void accept() {
		Poof request = service.getByReceiver(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof requests");
		Player sender = Utils.getPlayer(UUID.fromString(request.getSender())).getPlayer();
		sender.teleport(request.getReceiverLocation());
		send("&3You accepted &e" + sender.getName() + "'s &3poof request");
		send(sender, "&e" + Utils.getPlayer(UUID.fromString(request.getReceiver())).getName() + " &3accepted your poof request");
		request.setExpired(true);
		service.save(request);
	}

	@Path("deny")
	void deny() {
		Poof request = service.getByReceiver(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof requests");
		Player sender = Utils.getPlayer(UUID.fromString(request.getSender())).getPlayer();
		send("&3You denied &e" + sender.getName() + "'s &3poof request");
		send(sender, "&e" + Utils.getPlayer(UUID.fromString(request.getReceiver())).getName() + " &3denied your poof request");
		request.setExpired(true);
		service.save(request);
	}

	@Path("cancel")
	void cancel() {
		Poof request = service.getBySender(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof requests");
		Player receiver = Utils.getPlayer(UUID.fromString(request.getReceiver())).getPlayer();
		Player sender = Utils.getPlayer(UUID.fromString(request.getSender())).getPlayer();
		send(receiver, "&e" + sender.getName() + " &3canceled their poof request");
		send("&3You canceled your poof request to &e" + receiver.getName());
		request.setExpired(true);
		service.save(request);
	}

}
