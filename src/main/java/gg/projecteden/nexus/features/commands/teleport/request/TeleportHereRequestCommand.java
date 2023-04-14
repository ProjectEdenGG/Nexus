package gg.projecteden.nexus.features.commands.teleport.request;

import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.teleport.TeleportRequests.TeleportRequest;
import gg.projecteden.nexus.models.teleport.TeleportRequests.TeleportRequest.RequestType;
import org.bukkit.entity.Player;

@Aliases({"tprhere", "tpahere"})
public class TeleportHereRequestCommand extends ITeleportRequestCommand {

	public TeleportHereRequestCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Request a player teleport to you")
	void run(Player player) {
		if (isSelf(player))
			error("You cannot teleport to yourself");

		if (MuteMenuUser.hasMuted(player, MuteMenuItem.TP_REQUESTS))
			error(player.getName() + " has teleport requests disabled!");

		removeDuplicateRequests(player.getUniqueId());

		final TeleportRequest request = new TeleportRequest(player(), player, RequestType.SUMMON);
		requests.getPending().add(request);

		send(json("&eSummon &3request sent to " + Nickname.of(player) + ". ").next("&eClick to cancel").command("tprhere cancel " + request.getId()));
		send(player, "  &e" + nickname() + " &3is asking you to teleport &eto them");
		send(player, json("&3  Click one  ||  &a&lAccept")
				.command("/tprhere accept " + request.getId())
				.hover("&eClick &3to accept")
				.group()
				.next("  &3||  &3")
				.group()
				.next("&c&lDeny")
				.command("/tprhere deny " + request.getId())
				.hover("&eClick &3to deny.")
				.group()
				.next("&3  ||"));

	}

}
