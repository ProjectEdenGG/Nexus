package me.pugabyte.bncore.features.tickets;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.ticket.Ticket;
import me.pugabyte.bncore.models.ticket.TicketService;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class Tickets {
	public static final String PREFIX = Utils.getPrefix("Tickets");
	public static String PERMISSION_MOD = "is.op"; // "tickets.mod";

	static {
		PlaceholderAPI.registerPlaceholder(BNCore.getInstance(), "tickets_open", event ->
				String.valueOf(new TicketService().getAllOpen().size()));
		PlaceholderAPI.registerPlaceholder(BNCore.getInstance(), "tickets_total", event ->
				String.valueOf(new TicketService().getAll().size()));
	}

	static void showTicket(Player player, Ticket ticket) {
		SkriptFunctions.json(player, "&7#" + ticket.getId() + " &3" + ticket.getOwner().getName() + " &7- &e" +
				ticket.getDescription() + "||cmd:/tickets view " + ticket.getId());
	}

	public static void sendTicketButtons(Player staff, Ticket ticket) {
		staff.sendMessage("");
		SkriptFunctions.json(staff, "||&3 |&3|   &6&lTeleport||cmd:/tickets tp " + ticket.getId() + "||ttp:&eClick to teleport" +
				"||&3   |&3|   ||&b&lMessage||sgt:/msg " + ticket.getOwner().getName() + " ||ttp:&eClick to message the player" +
				"||&3   |&3|   ||&c&lClose||cmd:/tickets close " + ticket.getId() + "||ttp:&eClick to close" +
				"||&3   |&3|");
		staff.sendMessage("");
	}

	public static void tellOtherStaff(Player player, String message) {
		SkriptFunctions.log(ChatColor.stripColor(colorize(message)));

		Bukkit.getOnlinePlayers().stream()
				.filter(staff -> !staff.getUniqueId().equals(player.getUniqueId()))
				.filter(staff -> staff.hasPermission(PERMISSION_MOD))
				.forEach(staff -> staff.sendMessage(colorize(message)));
	}

}
