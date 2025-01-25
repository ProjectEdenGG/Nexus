package gg.projecteden.nexus.features.tickets;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.tickets.TicketFeature.TicketAction;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.ticket.Tickets;
import gg.projecteden.nexus.models.ticket.Tickets.Ticket;
import gg.projecteden.nexus.models.ticket.TicketsService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketsCommand extends CustomCommand {
	private final TicketsService service = new TicketsService();
	private final Tickets tickets = service.get0();

	public TicketsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("View open tickets")
	void tickets() {
		List<Ticket> open = tickets.getAllOpen();
		if (open.size() == 0)
			error("There are no open tickets");

		open.stream()
				.filter(ticket -> ticket.canBeSeenBy(player()))
				.forEach(ticket -> TicketFeature.showTicket(player(), ticket));
	}

	@Path("page [page]")
	@Description("View your tickets")
	void run(@Arg("1") int page) {
		List<Ticket> collect = tickets.getAll().stream()
			.filter(ticket -> ticket.canBeSeenBy(player()))
			.sorted(Comparator.comparingInt(Ticket::getId).reversed())
				.collect(Collectors.toList());

		new Paginator<Ticket>()
			.values(collect)
			.formatter((ticket, index) -> TicketFeature.formatTicket(player(), ticket))
			.command("/tickets page")
			.page(page)
			.send();
	}

	@Path("view <id>")
	@Description("View detailed information about a ticket")
	void view(Ticket ticket) {
		send(PREFIX + "&c#" + ticket.getId());
		send("&3Owner: &e" + ticket.getNickname());
		send("&3When: &e" + ticket.getTimespan() + " &3ago");
		send("&3Description: &e" + ticket.getDescription());
		send(TicketFeature.getTicketButtons(ticket));
	}

	@Path("(tp|teleport) <id>")
	@Description("Teleport to the location a ticket was created")
	void teleport(Ticket ticket) {
		if (ticket.getLocation() == null)
			if (UUIDUtils.isUUID0(ticket.getUuid()))
				error("That ticket was created by console, so you can not teleport to it");
			else
				error("That ticket does not have a location");

		player().teleportAsync(ticket.getLocation(), TeleportCause.COMMAND);

		TicketFeature.broadcast(ticket, player(), TicketAction.TELEPORT);

		send(PREFIX + "Teleporting to ticket &e#" + ticket.getId());

		Tasks.wait(15 * 20, () -> {
			if (ticket.isOpen())
				send(json(PREFIX + "&3Click here to &cclose &3the ticket")
					.command("/tickets confirmclose " + ticket.getId())
					.hover("&eClick to close"));
		});
	}

	@Confirm
	@HideFromWiki
	@HideFromHelp
	@TabCompleteIgnore
	@Path("confirmclose <id>")
	void confirmClose(Ticket ticket) {
		if (ticket.isOpen())
			close(ticket);
		else
			send(player(), PREFIX + "&cTicket already closed");
	}

	@Path("close <id>")
	@Description("Close a ticket")
	void close(Ticket ticket) {
		if (!ticket.isOpen())
			error("Ticket already closed");

		ticket.setOpen(false);
		ticket.setClosedBy(uuid());
		service.save(tickets);

		TicketFeature.broadcast(ticket, player(), TicketAction.CLOSE);

		send(PREFIX + "Ticket &e#" + ticket.getId() + " &cclosed");
	}

	@Path("reopen <id>")
	@Description("Re-open a closed ticket")
	void reopen(Ticket ticket) {
		if (ticket.isOpen())
			error("Ticket already open");

		ticket.setOpen(true);
		service.save(tickets);

		TicketFeature.broadcast(ticket, player(), TicketAction.REOPEN);

		send(PREFIX + "Ticket &e#" + ticket.getId() + " &areopened");
	}

	@Path("stats closed [page]")
	@Permission(Group.MODERATOR)
	@Description("View which staff member has closed the most tickets")
	void statsClosed(@Arg("1") int page) {
		Map<UUID, Integer> closers = new HashMap<>();
		for (Ticket ticket : tickets.getAll()) {
			if (ticket.isOpen())
				continue;

			if (ticket.getClosedBy() == null)
				continue;

			if (ticket.getClosedBy().equals(ticket.getUuid()))
				continue;

			closers.put(ticket.getClosedBy(), closers.getOrDefault(ticket.getClosedBy(), 0) + 1);
		}

		new Paginator<UUID>()
			.values(Utils.sortByValueReverse(closers).keySet())
			.formatter((uuid, index) -> json(index + " &e" + Nerd.of(uuid).getColoredName() + " &7- " + closers.get(uuid)))
			.command("/tickets stats closed")
			.page(page)
			.send();
	}

	@ConverterFor(Ticket.class)
	Ticket convertToTicket(String value) {
		if (!Utils.isInt(value))
			error("Ticket ID must be a number");

		Ticket ticket = tickets.get(Integer.parseInt(value));

		if (!ticket.canBeSeenBy(player()))
			error("You cannot view that ticket");

		return ticket;
	}

}
