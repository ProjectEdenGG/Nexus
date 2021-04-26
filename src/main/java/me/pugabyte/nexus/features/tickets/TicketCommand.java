package me.pugabyte.nexus.features.tickets;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.ticket.Ticket;
import me.pugabyte.nexus.models.ticket.TicketService;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;

import java.util.Arrays;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

public class TicketCommand extends CustomCommand {
	private final TicketService service = new TicketService();

	public TicketCommand(CommandEvent event) {
		super(event);
		PREFIX = Tickets.PREFIX;
	}

	@Path
	@Override
	public void help() {
		send("&3To request &ehelp &3or report &egrief&3, stand at the &erelevant location &3and open a &c/ticket " +
				"&3with an &einformative description &3of the issue.");
		send("&3Please be &epatient&3, as staff can be very busy!");
	}

	@Cooldown(@Part(Time.MINUTE))
	@Path("<description...>")
	void ticket(String description) {
		if (Arrays.asList("help", "info", "pls", "plz", "please").contains(description))
			error("Please make a ticket with a more informative description of the problem");

		if (Utils.isInt(description))
			error("Prevented accidental ticket");

		if (StringUtils.right(description, 5).equalsIgnoreCase("close"))
			error("Prevented accidental ticket (close)");

		Runnable run = () -> {
			Ticket ticket = new Ticket(player(), stripColor(description));
			service.saveSync(ticket);

			send(PREFIX + "You have submitted a ticket. Staff have been alerted, please wait patiently for a response. &eThank you!");
			send(" &eYour ticket (&c#" + ticket.getId() + "&e): &3" + ticket.getDescription());

			List<Nerd> onlineMods = Rank.getOnlineMods();
			String message = "**[Tickets]** " + name() + " (" + ticket.getId() + "): " + ticket.getDescription();
			Discord.staffLog(message);
			Discord.staffBridge(message + (onlineMods.size() == 0 ? " [ @here ]" : ""));

			onlineMods.forEach(mod -> Jingle.PING.play(mod.getPlayer()));
			Chat.broadcastIngame("", StaticChannel.STAFF);
			Chat.broadcastIngame(PREFIX + "&e" + name() + " &3opened ticket &c#" + ticket.getId() + "&3: &e" + ticket.getDescription(), StaticChannel.STAFF);
			Chat.broadcastIngame(Tickets.getTicketButtons(ticket), StaticChannel.STAFF);
		};

		if (isStaff())
			ConfirmationMenu.builder()
					.onConfirm(e -> run.run())
					.open(player());
		else
			run.run();
	}

}
