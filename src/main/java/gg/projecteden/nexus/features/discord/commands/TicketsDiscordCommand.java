package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.tickets.TicketFeature;
import gg.projecteden.nexus.features.tickets.TicketFeature.TicketAction;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.ticket.Tickets;
import gg.projecteden.nexus.models.ticket.Tickets.Ticket;
import gg.projecteden.nexus.models.ticket.TicketsService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.DiscordId.Role;
import gg.projecteden.utils.DiscordId.TextChannel;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class TicketsDiscordCommand extends Command {

	public TicketsDiscordCommand() {
		this.name = "tickets";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(TextChannel.STAFF_BRIDGE.getId()))
			return;

		Tasks.async(() -> {
			final String PREFIX = "**[Tickets]** ";
			try {
				DiscordUser user = new DiscordUserService().checkVerified(event.getAuthor().getId());
				OfflinePlayer player = PlayerUtils.getPlayer(user.getUuid());

				String[] args = event.getArgs().split(" ");
				if (args.length == 0 || !args[0].toLowerCase().matches("(list|view|close|reopen)"))
					throw new InvalidInputException("Correct usage: `/tickets <list|view|close|reopen> <ticketId>`");

				final String nl = System.lineSeparator();
				final TicketsService service = new TicketsService();
				final Tickets tickets = service.get0();

				if (args.length == 1) {
					if ("list".equalsIgnoreCase(args[0])) {
						List<Ticket> opened = tickets.getTickets().stream()
							.filter(Ticket::isOpen).collect(Collectors.toList());

						if (opened.isEmpty())
							throw new InvalidInputException("There are no open tickets");

						String ids = opened.stream()
							.map(_ticket -> "#" + _ticket.getId())
							.collect(Collectors.joining(", "));

						event.reply(PREFIX + ids);
						return;
					}
				}

				String id = args[1];
				if (!Utils.isInt(id))
					throw new InvalidInputException("Ticket ID must be a number");

				final Ticket ticket = tickets.get(Integer.parseInt(id));
				switch (args[0].toLowerCase()) {
					case "view" -> {
						String message = PREFIX + "**#" + ticket.getId() + "** ";
						message += ticket.isOpen() ? "(Open)" : "(Closed)";
						message += nl + "**Owner:** " + ticket.getNickname();
						message += nl + "**When:** " + ticket.getTimespan() + " ago";
						message += nl + "**Description:** " + ticket.getDescription();
						event.reply(message);
					}
					case "close" -> {
						if (!ticket.isOpen())
							throw new InvalidInputException("Ticket already closed");

						ticket.setOpen(false);
						service.save(tickets);

						TicketFeature.broadcastDiscord(ticket, Nickname.of(player), TicketAction.CLOSE);
					}
					case "reopen" -> {
						if (ticket.isOpen())
							throw new InvalidInputException("Ticket already open");

						ticket.setOpen(true);
						service.save(tickets);

						TicketFeature.broadcastDiscord(ticket, Nickname.of(player), TicketAction.REOPEN);
					}
				}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof EdenException))
					ex.printStackTrace();
			}
		});
	}


}
