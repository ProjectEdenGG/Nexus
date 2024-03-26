package gg.projecteden.nexus.features.commands.teleport.request;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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

	@Path("<player>")
	@Description("Request a player teleport to you")
	@Cooldown(value = TickTime.SECOND, x = 5, bypass = Group.ADMIN)
	void player(Player target) {
		if (isSelf(target))
			error("You cannot teleport to yourself");

		if (MuteMenuUser.hasMuted(target, MuteMenuItem.TP_REQUESTS))
			error(target.getName() + " has teleport requests disabled!");

		removeDuplicateRequests(target.getUniqueId());

		final TeleportRequest request = new TeleportRequest(player(), target, RequestType.SUMMON);
		requests.getPending().add(request);

		send(json("&eSummon &3request sent to " + Nickname.of(target) + ". ").next("&eClick to cancel").command("tprhere cancel " + request.getId()));
		send(target, "  &e" + nickname() + " &3is asking you to teleport &eto them");
		send(target, json("&3  Click one  ||  &a&lAccept")
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
