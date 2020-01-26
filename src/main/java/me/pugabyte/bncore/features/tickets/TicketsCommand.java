package me.pugabyte.bncore.features.tickets;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.ticket.Ticket;
import me.pugabyte.bncore.models.ticket.TicketService;

public class TicketsCommand extends CustomCommand {
	private TicketService service = new TicketService();

	public TicketsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void tickets() {
		service.getAllOpen().stream()
				.filter(ticket -> ticket.canBeSeenBy(player()))
				.forEach(ticket -> Tickets.showTicket(player(), ticket));
	}

	// TODO: Pagination
	@Path("all")
	void all() {
		service.getAll().stream()
				.filter(ticket -> ticket.canBeSeenBy(player()))
				.forEach(ticket -> Tickets.showTicket(player(), ticket));
	}

	@Path("view <id>")
	void view(int id) {
		Ticket ticket = service.get(id);
		if (!ticket.canBeSeenBy(player()))
			error("You can't view that ticket");

		send(PREFIX + "&c#" + ticket.getId());
		send("&3Owner: &e" + ticket.getOwner().getName());
		send("&3When: &e" + ticket.getTimespan() + " &3ago");
		send("&3Description: &e" + ticket.getDescription());
		Tickets.sendTicketButtons(player(), ticket);
	}

	@Path("(tp|teleport) <id>")
	void teleport(int id) {
		Ticket ticket = service.get(id);
		if (!ticket.canBeSeenBy(player()))
			error("You can't view that ticket");

		player().teleport(ticket.getLocation());

		String message = PREFIX + "&e" + player().getName() + " &3teleported to ticket &e#" + ticket.getId();
		Tickets.tellOtherStaff(player(), message);
		send(ticket.getOwner(), message);

		send(PREFIX + "Teleporting to ticket &e#" + ticket.getId());
	}

	// TODO: Confirm menu
	@Path("close <id>")
	void close(int id) {
		Ticket ticket = service.get(id);
		if (!ticket.canBeSeenBy(player()))
			error("You can't modify that ticket");
		if (!ticket.isOpen())
			error("Ticket already closed");

		ticket.setOpen(false);
		service.save(ticket);

		String message = PREFIX + "&e" + player().getName() + " &cclosed &3ticket &e#" + ticket.getId();
		Tickets.tellOtherStaff(player(), message);
		send(ticket.getOwner(), message);

		send(PREFIX + "Ticket &e#" + ticket.getId() + " &cclosed");
	}

	@Path("reopen <id>")
	void reopen(int id) {
		Ticket ticket = service.get(id);
		if (!ticket.canBeSeenBy(player()))
			error("You can't modify that ticket");
		if (ticket.isOpen())
			error("Ticket already open");

		ticket.setOpen(true);
		service.save(ticket);

		String message = PREFIX + "&e" + player().getName() + " &areopened &3ticket &e#" + ticket.getId();
		Tickets.tellOtherStaff(player(), message);
		send(ticket.getOwner(), message);

		send(PREFIX + "Ticket &e#" + ticket.getId() + " &areopened");
	}

}
