package gg.projecteden.nexus.features.tickets;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.ticket.Tickets;
import gg.projecteden.nexus.models.ticket.Tickets.Ticket;
import gg.projecteden.nexus.models.ticket.TicketsService;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;

import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class TicketCommand extends CustomCommand {
	private final TicketsService service = new TicketsService();
	private final Tickets tickets = service.get0();

	public TicketCommand(CommandEvent event) {
		super(event);
		PREFIX = TicketFeature.PREFIX;
	}

	@Path
	@Override
	@Description("Help menu")
	public void help() {
		send("&3To request &ehelp &3or report &egrief&3, stand at the &erelevant location &3and open a &c/ticket " +
				"&3with an &einformative description &3of the issue.");
		send("&3Please be &epatient&3, as staff can be very busy!");
	}

	@Cooldown(TickTime.MINUTE)
	@Path("<description...>")
	@Description("Open a ticket with the staff team")
	void ticket(String description) {
		if (Arrays.asList("help", "info", "pls", "plz", "please").contains(description))
			error("Please make a ticket with a more informative description of the problem");

		if (Utils.isInt(description))
			error("Prevented accidental ticket");

		if (StringUtils.right(description, 5).equalsIgnoreCase("close"))
			error("Prevented accidental ticket (close)");

		Runnable run = () -> {
			Ticket ticket = new Ticket(player(), stripColor(description));
			service.save(tickets);

			send(PREFIX + "You have submitted a ticket. Staff have been alerted, please wait patiently for a response. &eThank you!");
			send(" &eYour ticket (&c#" + ticket.getId() + "&e): &3" + ticket.getDescription());

			List<Nerd> onlineMods = Rank.getOnlineMods();
			String message = "**[Tickets]** " + nickname() + " (" + ticket.getId() + "): " + ticket.getDescription();
			Discord.staffLog(message);
			Discord.staffBridge(message + (onlineMods.isEmpty() ? " [ @here ]" : ""));

			onlineMods.forEach(mod -> Jingle.PING.play(mod.getOnlinePlayer()));
			Broadcast.staffIngame().message("").send();
			Broadcast.staffIngame().message(PREFIX + "&e" + name() + " &3opened ticket &c#" + ticket.getId() + "&3: &e" + ticket.getDescription()).send();
			Broadcast.staffIngame().message(TicketFeature.getTicketButtons(ticket)).send();
		};

		if (isStaff())
			ConfirmationMenu.builder()
					.onConfirm(e -> run.run())
					.open(player());
		else
			run.run();
	}

}
