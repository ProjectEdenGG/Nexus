package me.pugabyte.nexus.features.tickets;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.ticket.Ticket;
import me.pugabyte.nexus.models.ticket.TicketService;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;
import java.util.stream.Collectors;

public class TicketsCommand extends CustomCommand {
	private TicketService service = new TicketService();

	public TicketsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void tickets() {
		List<Ticket> open = service.getAllOpen();
		if (open.size() == 0)
			error("There are no open tickets");

		open.stream()
				.filter(ticket -> ticket.canBeSeenBy(player()))
				.forEach(ticket -> Tickets.showTicket(player(), ticket));
	}

	@Path("page [page]")
	void run(@Arg("1") int page) {
		List<Ticket> collect = service.getAll().stream()
				.filter(ticket -> ticket.canBeSeenBy(player()))
				.collect(Collectors.toList());

		paginate(collect, (ticket, index) -> Tickets.formatTicket(player(), ticket), "/tickets page", page);
	}

	@Path("view <id>")
	void view(int id) {
		Ticket ticket = service.get(id);
		if (!ticket.canBeSeenBy(player()))
			error("You can't view that ticket");

		send(PREFIX + "&c#" + ticket.getId());
		send("&3Owner: &e" + ticket.getOwnerName());
		send("&3When: &e" + ticket.getTimespan() + " &3ago");
		send("&3Description: &e" + ticket.getDescription());
		send(Tickets.getTicketButtons(ticket));
	}

	@Path("(tp|teleport) <id>")
	void teleport(Ticket ticket) {
		if (ticket.getLocation() == null)
			if (ticket.getOwner() instanceof ConsoleCommandSender)
				error("That ticket was created by console, so you can not teleport to it");
			else
				error("That ticket does not have a location");

		player().teleport(ticket.getLocation(), TeleportCause.COMMAND);

		String message = "&e" + player().getName() + " &3teleported to ticket &e#" + ticket.getId();
		Tickets.broadcast(ticket, player(), message);

		send(PREFIX + "Teleporting to ticket &e#" + ticket.getId());

		Tasks.wait(15 * 20, () -> {
			if (service.get(ticket.getId()).isOpen())
				send(json(PREFIX + "&3Click here to &cclose &3the ticket")
						.command("/tickets confirmclose " + ticket.getId())
						.hover("&eClick to close"));
		});
	}

	@Confirm
	@Path("confirmclose <id>")
	void confirmClose(Ticket ticket) {
		if (service.get(ticket.getId()).isOpen())
			close(ticket);
		else
			send(player(), PREFIX + "&cTicket already closed");
	}

	@Path("close <id>")
	void close(Ticket ticket) {
		if (!ticket.isOpen())
			error("Ticket already closed");

		ticket.setOpen(false);
		service.save(ticket);

		String message = "&e" + player().getName() + " &cclosed &3ticket &e#" + ticket.getId();
		Tickets.broadcast(ticket, player(), message);

		send(PREFIX + "Ticket &e#" + ticket.getId() + " &cclosed");
	}

	@Path("reopen <id>")
	void reopen(Ticket ticket) {
		if (ticket.isOpen())
			error("Ticket already open");

		ticket.setOpen(true);
		service.save(ticket);

		String message = "&e" + player().getName() + " &areopened &3ticket &e#" + ticket.getId();
		Tickets.broadcast(ticket, player(), message);

		send(PREFIX + "Ticket &e#" + ticket.getId() + " &areopened");
	}

	@ConverterFor(Ticket.class)
	Ticket convertToTicket(String value) {
		if (!Utils.isInt(value))
			error("Ticket ID must be a number");

		Ticket ticket = service.get(Integer.parseInt(value));

		if (!ticket.canBeSeenBy(player()))
			error("You cannot view that ticket");

		return ticket;
	}

}
