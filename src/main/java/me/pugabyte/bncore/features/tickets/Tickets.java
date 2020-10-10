package me.pugabyte.bncore.features.tickets;

import me.pugabyte.bncore.features.discord.Discord;
import me.pugabyte.bncore.models.ticket.Ticket;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

	public static void tellOtherStaff(Player player, String message) {
		Discord.log("**[Tickets]** " + message);

		Bukkit.getOnlinePlayers().stream()
				.filter(staff -> player == null || !staff.getUniqueId().equals(player.getUniqueId()))
				.filter(staff -> staff.hasPermission("group.moderator"))
				.forEach(staff -> Utils.send(staff, PREFIX + message));
	}

}
