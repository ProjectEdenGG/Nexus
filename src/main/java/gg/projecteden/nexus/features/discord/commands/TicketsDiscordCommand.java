package gg.projecteden.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import gg.projecteden.exceptions.EdenException;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.DiscordId.Role;
import gg.projecteden.nexus.features.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.HandledBy;
import gg.projecteden.nexus.features.tickets.Tickets;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.discord.DiscordUser;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.ticket.Ticket;
import gg.projecteden.nexus.models.ticket.TicketService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;

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
				if (args.length == 0 || !args[0].toLowerCase().matches("(view|close|reopen)"))
					throw new InvalidInputException("Correct usage: `/tickets <view|close|reopen> <ticketId>`");

				String id = args[1];
				if (!Utils.isInt(id))
					throw new InvalidInputException("Ticket ID must be a number");

				final String nl = System.lineSeparator();
				final TicketService service = new TicketService();
				final Ticket ticket = service.get(Integer.parseInt(id));

				switch (args[0].toLowerCase()) {
					case "view" -> {
						String message = PREFIX + "**#" + ticket.getId() + "** ";
						message += ticket.isOpen() ? "(Open)" : "(Closed)";
						message += nl + "**Owner:** " + ticket.getOwnerName();
						message += nl + "**When:** " + ticket.getTimespan() + " ago";
						message += nl + "**Description:** " + ticket.getDescription();
						event.reply(message);
					}
					case "close" -> {
						if (!ticket.isOpen())
							throw new InvalidInputException("Ticket already closed");

						ticket.setOpen(false);
						service.save(ticket);

						Tickets.broadcast(ticket, null, Nickname.of(player) + " closed ticket #" + ticket.getId());
					}
					case "reopen" -> {
						if (ticket.isOpen())
							throw new InvalidInputException("Ticket already open");

						ticket.setOpen(true);
						service.save(ticket);

						Tickets.broadcast(ticket, null, Nickname.of(player) + " reopened ticket #" + ticket.getId());
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
