package me.pugabyte.bncore.models.ticket;

import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.MySQLService;

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
