package me.pugabyte.bncore.features.tickets;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.ticket.Ticket;
import me.pugabyte.bncore.models.ticket.TicketService;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.Jingles;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Aliases({"helpop", "report"})
public class TicketCommand extends CustomCommand {
	private TicketService service = new TicketService();

	public TicketCommand(CommandEvent event) {
		super(event);
		PREFIX = Tickets.PREFIX;
	}

	@Path
	void help() {
		send("&3To request &ehelp &3or report &egrief&3, stand at the &erelevant location &3and open a &c/ticket " +
				"&3with an &einformative description &3of the issue.");
		send("&3Please be &epatient&3, as staff can be very busy!");
	}

	@Path("<description...>")
	void ticket(String description) {
		if (Arrays.asList("help", "info", "pls", "plz", "please").contains(description))
			error("Please make a ticket with a more informative description of the problem");

		if (Utils.isInt(description))
			error("Prevented accidental ticket");

		Ticket ticket = new Ticket(player(), ChatColor.stripColor(colorize(description)));
		service.saveSync(ticket);

		send(PREFIX + "You have submitted a ticket. Staff have been alerted, please wait patiently for a response. &eThank you!");
		send(" &eYour ticket (&c#" + ticket.getId() + "&e): &3" + ticket.getDescription());

		// TODO: #staff-alerts if no staff are on
		SkriptFunctions.log("[Tickets] " + player().getName() + " (" + ticket.getId() + "): " + ticket.getDescription());

		Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Tickets.PERMISSION_MOD)).forEach(staff -> {
			Jingles.ping(staff);
			send(staff, "");
			send(staff, PREFIX + "&e" + player().getName() + " &3opened ticket &c#" + ticket.getId() + "&3: &e" + ticket.getDescription());
			Tickets.sendTicketButtons(staff, ticket);
		});
	}

}
