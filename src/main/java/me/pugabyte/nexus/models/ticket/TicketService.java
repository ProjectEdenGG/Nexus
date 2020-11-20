package me.pugabyte.nexus.models.ticket;

import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.MySQLService;

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
