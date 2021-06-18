package me.pugabyte.nexus.features.tickets;

import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.ticket.Ticket;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Tickets {
	public static final String PREFIX = StringUtils.getPrefix("Tickets");

	public static void showTicket(Player player, Ticket ticket) {
		formatTicket(player, ticket).send(player);
	}

	public static JsonBuilder formatTicket(Player player, Ticket ticket) {
		return new JsonBuilder()
				.next("&7#" + ticket.getId() + " &3" + ticket.getOwnerName() + " &7- &e" + ticket.getDescription())
				.command("/tickets view " + ticket.getId());
	}

	public static JsonBuilder getTicketButtons(Ticket ticket) {
		return new JsonBuilder()
				.line()
				.next("&3 |&3|   &3").group()
				.next("&6&lTeleport").command("/tickets tp " + ticket.getId()).hover("&eClick to teleport").group()
				.next("&3   |&3|   &3").group()
				.next("&b&lMessage").suggest("/msg " + ticket.getOwnerName()).hover("&eClick to message the player").group()
				.next("&3   |&3|   &3").group()
				.next("&c&lClose").command("/tickets confirmclose " + ticket.getId()).hover("&eClick to close").group()
				.next("&3   |&3|")
				.line();
	}

	public static void broadcast(Ticket ticket, Player player, String message) {
		Discord.log("**[Tickets]** " + message);

		Set<UUID> uuids = new HashSet<>();
		for (Player staff : PlayerUtils.getOnlinePlayers())
			if (Rank.of(staff).isMod())
				if (player == null || !staff.getUniqueId().equals(player.getUniqueId()))
					uuids.add(staff.getUniqueId());

		if (ticket.getOwner() instanceof Player owner)
			uuids.add(owner.getUniqueId());

		uuids.forEach(uuid -> PlayerUtils.send(uuid, PREFIX + message));
	}

}
