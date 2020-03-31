package me.pugabyte.bncore.features.commands.poof;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bncore.models.poof.Poof;
import me.pugabyte.bncore.models.poof.PoofService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.time.LocalDateTime;

@Aliases("tpa")
@Redirect(from = "/tpcancel", to = "/poof cancel")
@Redirect(from = {"/tpno", "/tpdeny"}, to = "/poof deny")
@Redirect(from = {"/tpyes", "/tpaccept"}, to = "/poof accept")
public class PoofCommand extends CustomCommand {
	PoofService service = new PoofService();

	public PoofCommand(CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeatAsync(Time.SECOND.x(5), Time.MINUTE, () -> {
			PoofService poofService = new PoofService();
			poofService.getActivePoofs().forEach((poof -> {
				if (poof.getTimeSent().isBefore(LocalDateTime.now().minusMinutes(10))) {
					poof.setExpired(true);
					poofService.save(poof);
				}
			}));
		});
	}

	@Path("<player>")
	void player(Player target) {
		if (target == player().getPlayer())
			error("You cannot poof to yourself");

		Poof request = new Poof(player(), target);
		service.save(request);
		send(json("&ePoof &3request sent to " + target.getName() + ". ").next("&eClick to cancel").command("poof cancel"));
		send(target, "  &e" + player().getName() + " &3is asking to poof &eto you&3.");
		send(target, json("&3  Click one  ||  &a&lAccept")
				.command("poof accept")
				.hover("&eClick &3to accept")
				.group()
				.next("  &3||  &3")
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

		OfflinePlayer sender = request.getSenderPlayer();
		if (!sender.isOnline())
			throw new PlayerNotOnlineException(sender);

		sender.getPlayer().teleport(request.getReceiverLocation(), TeleportCause.COMMAND);
		send("&3You accepted &e" + sender.getName() + "'s &3poof request");
		send(sender.getPlayer(), "&e" + request.getReceiverPlayer().getName() + " &3accepted your poof request");
		request.setExpired(true);
		service.save(request);
	}

	@Path("deny")
	void deny() {
		Poof request = service.getByReceiver(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof requests");

		OfflinePlayer sender = request.getSenderPlayer();
		if (!sender.isOnline())
			throw new PlayerNotOnlineException(sender);

		send("&3You denied &e" + sender.getName() + "'s &3poof request");
		send(sender.getPlayer(), "&e" + Utils.getPlayer(request.getReceiver()).getName() + " &3denied your poof request");
		request.setExpired(true);
		service.save(request);
	}

	@Path("cancel")
	void cancel() {
		Poof request = service.getBySender(player().getPlayer());
		if (request == null || request.isExpired())
			error("You do not have any pending Poof requests");

		OfflinePlayer receiver = request.getReceiverPlayer();
		OfflinePlayer sender = request.getSenderPlayer();
		if (!receiver.isOnline())
			throw new PlayerNotOnlineException(receiver);

		send(receiver.getPlayer(), "&e" + sender.getName() + " &3canceled their poof request");
		send("&3You canceled your poof request to &e" + receiver.getName());
		request.setExpired(true);
		service.save(request);
	}

}
