package gg.projecteden.nexus.features.tickets;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.ticket.Tickets.Ticket;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
			.next("&b&lMessage").suggest("/msg " + ticket.getNickname() + " ").hover("&eClick to message the player").group()
			.next("&3   |&3|   &3").group()
			.next("&c&lClose").command("/tickets confirmclose " + ticket.getId()).hover("&eClick to close").group()
			.next("&3   |&3|")
			.line();
	}

	public static void broadcastDiscord(Ticket ticket, String staffName, TicketAction action) {
		action.discordLog(ticket, staffName);
		sendMessage(ticket, null, staffName, action);
	}

	public static void broadcast(Ticket ticket, Player staff, TicketAction action) {
		String nickname = Nerd.of(staff).getNickname();
		action.discordLog(ticket, nickname);
		sendMessage(ticket, staff, nickname, action);
	}

	private static void sendMessage(Ticket ticket, @Nullable Player staff, String staffName, TicketAction action) {
		String staffNick = staffName;
		if (staff != null)
			staffNick = Nickname.of(staff);

		for (Nerd _staff : Rank.getOnlineStaff()) {
			if (staff == null || !_staff.getUniqueId().equals(staff.getUniqueId()))
				action.sendMessage(_staff.getUniqueId(), ticket, staffNick);
		}

		if (UUIDUtils.isUUID0(ticket.getUuid()))
			return;

		if (Rank.of(ticket.getUuid()).isStaff())
			return;

		if (!PlayerUtils.canSee(ticket.getPlayer(), staff)) {
			if (action.equals(TicketAction.TELEPORT))
				return;

			staffNick = "A staff member";
		}

		action.sendMessage(ticket.getUuid(), ticket, staffNick);
	}

	@AllArgsConstructor
	public enum TicketAction {
		CLOSE("&e<staff> &cclosed &3ticket &e#<id>"),
		REOPEN("&e<staff> &areopened &3ticket &e#<id>"),
		TELEPORT("&e<staff> &3teleported to ticket &e#<id>"),
		;

		final String message;

		public void discordLog(Ticket ticket, String staff) {
			String _message = "**[Tickets]** " + StringUtils.stripColor(formatMessage(message, ticket, staff));
			Discord.log(_message);
			if (this == CLOSE) {
				Discord.staffLog(_message);
			}
		}

		public void sendMessage(UUID receiver, Ticket ticket, String staffName) {
			PlayerUtils.send(receiver, PREFIX + formatMessage(message, ticket, staffName));
		}

		private String formatMessage(String message, Ticket ticket, String staffName) {
			message = message.replaceAll("<opener>", ticket.getNerd().getNickname());
			message = message.replaceAll("<id>", String.valueOf(ticket.getId()));
			message = message.replaceAll("<staff>", staffName);

			return message.trim();
		}
	}

}
