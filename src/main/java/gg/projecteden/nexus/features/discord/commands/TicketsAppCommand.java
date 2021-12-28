package gg.projecteden.nexus.features.discord.commands;

import gg.projecteden.discord.appcommands.AppCommandEvent;
import gg.projecteden.discord.appcommands.AppCommandRegistry;
import gg.projecteden.discord.appcommands.annotations.Command;
import gg.projecteden.discord.appcommands.annotations.Desc;
import gg.projecteden.discord.appcommands.annotations.RequiredRole;
import gg.projecteden.nexus.features.discord.appcommands.NexusAppCommand;
import gg.projecteden.nexus.features.discord.appcommands.annotations.Verify;
import gg.projecteden.nexus.features.tickets.TicketFeature;
import gg.projecteden.nexus.features.tickets.TicketFeature.TicketAction;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.ticket.Tickets;
import gg.projecteden.nexus.models.ticket.Tickets.Ticket;
import gg.projecteden.nexus.models.ticket.TicketsService;
import gg.projecteden.nexus.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@Verify
@RequiredRole("Staff")
@Command("Manage tickets")
public class TicketsAppCommand extends NexusAppCommand {
	private static final String PREFIX = "**[Tickets]** ";
	private final TicketsService service = new TicketsService();
	private final Tickets tickets = service.get0();

	public TicketsAppCommand(AppCommandEvent event) {
		super(event);
	}

	@Command(value = "List open tickets", literals = false)
	void list() {
		List<Ticket> opened = tickets.getTickets().stream()
			.filter(Ticket::isOpen).collect(Collectors.toList());

		if (opened.isEmpty())
			throw new InvalidInputException("There are no open tickets");

		String ids = opened.stream()
			.map(_ticket -> "#" + _ticket.getId())
			.collect(Collectors.joining(", "));

		replyEphemeral(PREFIX + ids);
	}

	@Command("View a ticket")
	void view(@Desc("Ticket Id") Ticket ticket) {
		final String nl = System.lineSeparator();
		String message = PREFIX + "**#" + ticket.getId() + "** ";
		message += ticket.isOpen() ? "(Open)" : "(Closed)";
		message += nl + "**Owner:** " + ticket.getNickname();
		message += nl + "**When:** " + ticket.getTimespan() + " ago";
		message += nl + "**Description:** " + ticket.getDescription();
		replyEphemeral(message);
	}

	@Command("Close a ticket")
	void close(@Desc("Ticket Id") Ticket ticket) {
		if (!ticket.isOpen())
			throw new InvalidInputException("Ticket already closed");

		ticket.setOpen(false);
		service.save(tickets);

		TicketFeature.broadcastDiscord(ticket, nickname(), TicketAction.CLOSE);
		thumbsupEphemeral();
	}

	@Command("Reopen a ticket")
	void reopen(@Desc("Ticket Id") Ticket ticket) {
		if (ticket.isOpen())
			throw new InvalidInputException("Ticket already open");

		ticket.setOpen(true);
		service.save(tickets);

		TicketFeature.broadcastDiscord(ticket, nickname(), TicketAction.REOPEN);
		thumbsupEphemeral();
	}

	static {
		AppCommandRegistry.registerConverter(Ticket.class, argument -> {
			if (!Utils.isInt(argument.getInput()))
				throw new InvalidInputException("Ticket ID must be a number");

			return new TicketsService().get0().get(Integer.parseInt(argument.getInput()));
		});
	}

}
