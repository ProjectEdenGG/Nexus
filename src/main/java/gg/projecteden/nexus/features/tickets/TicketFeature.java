package gg.projecteden.nexus.features.tickets;

import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.ticket.Tickets.Ticket;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TicketFeature {
	public static final String PREFIX = StringUtils.getPrefix("Tickets");

	public static void showTicket(Player player, Ticket ticket) {
		formatTicket(player, ticket).send(player);
	}

	public static JsonBuilder formatTicket(Player player, Ticket ticket) {
		return new JsonBuilder()
				.next("&7#" + ticket.getId() + " &3" + ticket.getNickname() + " &7- &e" + ticket.getDescription())
				.command("/tickets view " + ticket.getId());
	}

	public static JsonBuilder getTicketButtons(Ticket ticket) {
		return new JsonBuilder()
				.line()
				.next("&3 |&3|   &3").group()
				.next("&6&lTeleport").command("/tickets tp " + ticket.getId()).hover("&eClick to teleport").group()
				.next("&3   |&3|   &3").group()
				.next("&b&lMessage").suggest("/msg " + ticket.getNickname()).hover("&eClick to message the player").group()
				.next("&3   |&3|   &3").group()
				.next("&c&lClose").command("/tickets confirmclose " + ticket.getId()).hover("&eClick to close").group()
				.next("&3   |&3|")
				.line();
	}

	public static void broadcast(Ticket ticket, Player player, String message) {
		Discord.log("**[Tickets]** " + message);

		Set<UUID> uuids = new HashSet<>();
		for (Player staff : OnlinePlayers.getAll())
			if (Rank.of(staff).isMod())
				if (player == null || !staff.getUniqueId().equals(player.getUniqueId()))
					uuids.add(staff.getUniqueId());

		if (!StringUtils.isUUID0(ticket.getUuid()))
			uuids.add(ticket.getUuid());

		uuids.forEach(uuid -> PlayerUtils.send(uuid, PREFIX + message));
	}

}
