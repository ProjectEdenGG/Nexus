package me.pugabyte.bncore.features.tickets;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.ticket.Ticket;
import me.pugabyte.bncore.models.ticket.TicketService;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class Tickets {
	public static final String PREFIX = StringUtils.getPrefix("Tickets");
	public static String PERMISSION_MOD = "tickets.mod";

	static {
		BNCore.registerPlaceholder("tickets_open", event ->
				String.valueOf(new TicketService().getAllOpen().size()));
		BNCore.registerPlaceholder("tickets_total", event ->
				String.valueOf(new TicketService().getAll().size()));
	}

	static void showTicket(Player player, Ticket ticket) {
		new JsonBuilder()
				.next("&7#" + ticket.getId() + " &3" + ticket.getOwnerName() + " &7- &e" + ticket.getDescription())
				.command("/tickets view " + ticket.getId())
				.send(player);
	}

	public static void sendTicketButtons(Player staff, Ticket ticket) {
		staff.sendMessage("");
		new JsonBuilder()
				.next("&3 |&3|   &3").group()
				.next("&6&lTeleport").command("/tickets tp " + ticket.getId()).hover("&eClick to teleport").group()
				.next("&3   |&3|   &3").group()
				.next("&b&lMessage").suggest("/msg " + ticket.getOwnerName()).hover("&eClick to message the player").group()
				.next("&3   |&3|   &3").group()
				.next("&c&lClose").command("/tickets confirmclose " + ticket.getId()).hover("&eClick to close").group()
				.next("&3   |&3|")
				.send(staff);
		staff.sendMessage("");
	}

	public static void tellOtherStaff(Player player, String message) {
		SkriptFunctions.log(stripColor(message));

		Bukkit.getOnlinePlayers().stream()
				.filter(staff -> !staff.getUniqueId().equals(player.getUniqueId()))
				.filter(staff -> staff.hasPermission(PERMISSION_MOD))
				.forEach(staff -> staff.sendMessage(colorize(message)));
	}

}
