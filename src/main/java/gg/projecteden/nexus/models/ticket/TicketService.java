package gg.projecteden.nexus.models.ticket;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.MySQLService;

import java.util.List;

public class TicketService extends MySQLService {

	public Ticket get(int id) {
		Ticket ticket = database.where("id = ?", id).first(Ticket.class);
		if (ticket.getId() == 0)
			throw new InvalidInputException("Ticket not found");
		return ticket;
	}

	public List<Ticket> getAllOpen() {
		return database.where("open = 1").results(Ticket.class);
	}

	public List<Ticket> getAll() {
		return database.results(Ticket.class);
	}

}
