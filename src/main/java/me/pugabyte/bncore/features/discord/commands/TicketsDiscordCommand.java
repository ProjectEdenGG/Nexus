package me.pugabyte.bncore.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bncore.features.discord.Bot;
import me.pugabyte.bncore.features.discord.Bot.HandledBy;
import me.pugabyte.bncore.features.discord.DiscordId.Channel;
import me.pugabyte.bncore.features.discord.DiscordId.Role;
import me.pugabyte.bncore.features.tickets.Tickets;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.models.discord.DiscordService;
import me.pugabyte.bncore.models.discord.DiscordUser;
import me.pugabyte.bncore.models.ticket.Ticket;
import me.pugabyte.bncore.models.ticket.TicketService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@HandledBy(Bot.RELAY)
public class TicketsDiscordCommand extends Command {

	public TicketsDiscordCommand() {
		this.name = "tickets";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(Channel.STAFF_BRIDGE.getId()))
			return;

		Tasks.async(() -> {
			DiscordUser user = new DiscordService().checkVerified(event.getAuthor().getId());
			OfflinePlayer player = Utils.getPlayer(user.getUuid());

			String[] args = event.getArgs().split(" ");
			if (args.length == 0 || !args[0].toLowerCase().matches("(view|close|reopen)"))
				throw new InvalidInputException("Correct usage: `/tickets <view|close|reopen> <ticketId>`");

			String id = args[1];
			if (!Utils.isInt(id))
				throw new InvalidInputException("Ticket ID must be a number");

			final String PREFIX = "**[Tickets]** ";
			String message = "";
			String nl = System.lineSeparator();
			final TicketService service = new TicketService();
			Ticket ticket = service.get(Integer.parseInt(id));

			switch (args[0].toLowerCase()) {
				case "view":
					message += nl + PREFIX + "**#" + ticket.getId() + "**";
					if (!ticket.isOpen())
						message += " (Closed)";
					message += nl + "**Owner:** " + ticket.getOwnerName();
					message += nl + "**When:** " + ticket.getTimespan() + " ago";
					message += nl + "**Description:** " + ticket.getDescription();
					event.reply(message);
					break;
				case "close": {
					if (!ticket.isOpen())
						throw new InvalidInputException("Ticket already closed");

					ticket.setOpen(false);
					service.save(ticket);

					message += player.getName() + " closed ticket #" + ticket.getId();
					Tickets.tellOtherStaff(null, message);
					if (ticket.getOwner() instanceof Player)
						Utils.send((Player) ticket.getOwner(), PREFIX + message);
					break;
				}
				case "reopen": {
					if (ticket.isOpen())
						throw new InvalidInputException("Ticket already open");

					ticket.setOpen(true);
					service.save(ticket);

					message += player.getName() + " reopened ticket #" + ticket.getId();
					Tickets.tellOtherStaff(null, message);
					if (ticket.getOwner() instanceof Player)
						Utils.send((Player) ticket.getOwner(), PREFIX + message);
					break;
				}
			}
		});
	}


}
