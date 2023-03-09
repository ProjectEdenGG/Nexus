package gg.projecteden.nexus.features.commands.teleport.request;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.teleport.TeleportRequests;
import gg.projecteden.nexus.models.teleport.TeleportRequests.TeleportRequest;
import gg.projecteden.nexus.models.teleport.TeleportRequests.TeleportRequest.RequestType;
import gg.projecteden.nexus.models.teleport.TeleportRequestsService;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static gg.projecteden.nexus.utils.PlayerUtils.canSee;

@Aliases({"tpr", "tprequest", "tpa", "tpask"})
@Redirect(from = "/tpcancel", to = "/tpr cancel")
@Redirect(from = {"/tpno", "/tpdeny"}, to = "/tpr deny")
@Redirect(from = {"/tpyes", "/tpaccept"}, to = "/tpr accept")
public class TeleportRequestCommand extends ITeleportRequestCommand {

	public TeleportRequestCommand(CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND.x(5), TickTime.MINUTE, () -> {
			final TeleportRequestsService service = new TeleportRequestsService();
			final TeleportRequests requests = service.get0();

			new ArrayList<>(requests.getPending()).forEach(request -> {
				if (request.getTimeSent().isBefore(LocalDateTime.now().minusMinutes(10)))
					requests.getPending().remove(request);
			});

			service.save(requests);
		});
	}

	@Path("<player>")
	@Description("Request to teleport to a player")
	void player(OfflinePlayer target) {
		if (isSelf(target))
			error("You cannot teleport to yourself");

		if (!canSee(player(), target))
			throw new PlayerNotOnlineException(target);

		if (MuteMenuUser.hasMuted(target, MuteMenuItem.TP_REQUESTS))
			error(target.getName() + " has teleport requests disabled!");

		Location targetLocation = Nerd.of(target).getLocation();
		World targetWorld = targetLocation.getWorld();
		WorldGroup targetWorldGroup = WorldGroup.of(targetWorld);

		if (!isStaff()) {
			String cannotTeleport = "Cannot teleport to " + nickname(target);
			if (Minigamer.of(target).isPlaying())
				error(cannotTeleport + ", they are playing minigames");

			if (!rank().isNoble() && targetWorldGroup == WorldGroup.STAFF)
				error(cannotTeleport + ", they are in a staff world");
		}

		Trust trust = new TrustService().get(target);
		if (trust.trusts(Type.TELEPORTS, player())) {
			player().teleportAsync(targetLocation, TeleportCause.COMMAND);
			send(PREFIX + "Teleporting to &e" + Nickname.of(target) + (target.isOnline() && PlayerUtils.canSee(player(), target) ? "" : " &3(Offline)"));
			return;
		}

		// Validate online & can see
		Player targetPlayer = convertToPlayer(target);

		removeDuplicateRequests(target.getUniqueId());

		final TeleportRequest request = new TeleportRequest(player(), targetPlayer, RequestType.TELEPORT);
		requests.getPending().add(request);

		send(json("&eTeleport &3request sent to " + Nickname.of(targetPlayer) + ". ").next("&eClick to cancel").command("tpr cancel " + request.getId()));
		send(targetPlayer, " &e" + nickname() + " &3is asking to teleport &eto you");
		send(targetPlayer, json("&3 Click one &3 || &3 ").group()
			.next("&a&lAccept").command("/tpr accept " + request.getId()).hover("&eClick &3to accept").group()
			.next("&3 &3 || &3 ").group()
			.next("&c&lDeny").command("/tpr deny " + request.getId()).hover("&eClick &3to deny").group()
			.next("&3 &3 ||"));
	}

}
