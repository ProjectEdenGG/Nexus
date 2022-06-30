package gg.projecteden.nexus.features.commands.teleport.request;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.teleport.TeleportRequests;
import gg.projecteden.nexus.models.teleport.TeleportRequests.TeleportRequest;
import gg.projecteden.nexus.models.teleport.TeleportRequests.TeleportRequest.RequestType;
import gg.projecteden.nexus.models.teleport.TeleportRequestsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ITeleportRequestCommand extends CustomCommand {
	protected final TeleportRequestsService service = new TeleportRequestsService();
	protected final TeleportRequests requests = service.get0();

	public ITeleportRequestCommand(@NonNull CommandEvent event) {
		super(event);
	}

	protected void removeDuplicateRequests(UUID receiver) {
		requests.removeDuplicates(uuid(), receiver);
	}

	@NotNull
	private TeleportRequest getTeleportRequest(Integer id, String action, Function<Player, List<TeleportRequest>> getter) {
		TeleportRequest request = null;

		if (id == null) {
			final List<TeleportRequest> pending = getter.apply(player());
			switch (pending.size()) {
				case 0 -> error("You do not have any pending teleport requests");
				case 1 -> request = pending.get(0);
				default -> {
					final JsonBuilder error = new JsonBuilder("You have multiple pending requests, click on the one you want to " + action);
					for (TeleportRequest _request : pending) {
						error.group().newline();

						String tofrom = "from";
						String otherPlayer = Nickname.of(_request.getSenderPlayer());
						if ("cancel".equals(action)) {
							tofrom = "to";
							otherPlayer = Nickname.of(_request.getReceiverPlayer());
						}

						String type = "Teleport";
						if (_request.getType() == RequestType.SUMMON)
							type = "Summon";

						error.next("&e " + type + " &3request " + tofrom + " &e" + otherPlayer)
							.next(" &7(" + Timespan.of(_request.getTimeSent()).format() + " ago)")
							.command("/tpr " + action + " " + _request.getId())
							.hover("&3Click to " + ("accept".equals(action) ? "&e" : "&c") + action);
					}

					error(error);
				}
			}
		} else {
			request = requests.get(id);

			if (request == null)
				error("That teleport request is no longer valid");
		}

		requests.getPending().remove(request);
		service.save(requests);

		return request;
	}

	@Path("accept [id]")
	public void accept(Integer id) {
		TeleportRequest request = getTeleportRequest(id, "accept", requests::getByReceiver);

		OfflinePlayer toPlayer = request.getReceiverPlayer();
		OfflinePlayer fromPlayer = request.getSenderPlayer();
		if (request.getType() == RequestType.SUMMON) {
			toPlayer = request.getSenderPlayer();
			fromPlayer = request.getReceiverPlayer();
		}

		if (!fromPlayer.isOnline() || fromPlayer.getPlayer() == null)
			throw new PlayerNotOnlineException(fromPlayer);

		if (request.getType() == RequestType.TELEPORT)
			fromPlayer.getPlayer().teleportAsync(Nerd.of(toPlayer).getLocation(), TeleportCause.COMMAND);
		else
			fromPlayer.getPlayer().teleportAsync(request.getTeleportLocation(), TeleportCause.COMMAND);

		if (request.getType() == RequestType.TELEPORT) {
			send(toPlayer.getPlayer(), "&3You accepted &e" + Nickname.of(fromPlayer) + "'s &3teleport request");
			send(fromPlayer.getPlayer(), "&e" + Nickname.of(toPlayer) + " &3accepted your teleport request");
		} else {
			send(fromPlayer.getPlayer(), "&3You accepted &e" + Nickname.of(toPlayer) + "'s &3summon request");
			if (toPlayer.isOnline())
				send(toPlayer.getPlayer(), "&e" + Nickname.of(fromPlayer) + " &3accepted your summon request");
		}
	}

	@Path("deny [id]")
	public void deny(Integer id) {
		TeleportRequest request = getTeleportRequest(id, "deny", requests::getByReceiver);

		OfflinePlayer toPlayer = request.getReceiverPlayer();
		OfflinePlayer fromPlayer = request.getSenderPlayer();
		if (request.getType() == RequestType.SUMMON) {
			toPlayer = request.getSenderPlayer();
			fromPlayer = request.getReceiverPlayer();
		}

		if (!fromPlayer.isOnline())
			throw new PlayerNotOnlineException(fromPlayer);

		if (request.getType() == RequestType.TELEPORT) {
			send(toPlayer.getPlayer(), "&3You denied &e" + Nickname.of(fromPlayer) + "'s &3teleport request");
			send(fromPlayer.getPlayer(), "&e" + Nickname.of(toPlayer) + " &3denied your teleport request");
		} else {
			send(fromPlayer.getPlayer(), "&3You denied &e" + Nickname.of(toPlayer) + "'s &3summon request");
			send(toPlayer.getPlayer(), "&e" + Nickname.of(fromPlayer) + " &3denied your summon request");
		}
	}

	@Path("cancel [id]")
	public void cancel(Integer id) {
		TeleportRequest request = getTeleportRequest(id, "cancel", requests::getBySender);

		OfflinePlayer toPlayer = request.getReceiverPlayer();
		OfflinePlayer fromPlayer = request.getSenderPlayer();
		if (request.getType() == RequestType.SUMMON) {
			toPlayer = request.getSenderPlayer();
			fromPlayer = request.getReceiverPlayer();
		}

		if (!fromPlayer.isOnline())
			throw new PlayerNotOnlineException(fromPlayer);

		if (request.getType() == RequestType.TELEPORT) {
			send(toPlayer.getPlayer(), "&e" + Nickname.of(fromPlayer) + " &3canceled their teleport request");
			send(fromPlayer.getPlayer(), "&3You canceled your teleport request to &e" + Nickname.of(toPlayer));
		} else {
			send(fromPlayer.getPlayer(), "&e" + Nickname.of(toPlayer) + " &3canceled their summon request");
			send(toPlayer.getPlayer(), "&3You canceled your summon request to &e" + Nickname.of(fromPlayer));
		}
	}
}
