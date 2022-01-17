package gg.projecteden.nexus.features.commands.poof;

import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.events.y2021.bearfair21.BearFair21;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.poof.Poof;
import gg.projecteden.nexus.models.poof.PoofService;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.time.LocalDateTime;

import static gg.projecteden.nexus.utils.PlayerUtils.canSee;

@Aliases({"tpa", "tpask"})
@NoArgsConstructor
@Redirect(from = "/tpcancel", to = "/poof cancel")
@Redirect(from = {"/tpno", "/tpdeny"}, to = "/poof deny")
@Redirect(from = {"/tpyes", "/tpaccept"}, to = "/poof accept")
public class PoofCommand extends CustomCommand {
	PoofService service = new PoofService();

	public PoofCommand(CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND.x(5), TickTime.MINUTE, () -> {
			PoofService poofService = new PoofService();
			poofService.getActivePoofs().forEach((poof -> {
				if (poof.getTimeSent().isBefore(LocalDateTime.now().minusMinutes(10))) {
					poof.setExpired(true);
					poofService.save(poof);
				}
			}));
		});
	}

	@Path("<player>")
	void player(OfflinePlayer target) {
		if (isSelf(target))
			error("You cannot poof to yourself");

		if (!canSee(player(), target))
			throw new PlayerNotOnlineException(target);

		if (MuteMenuUser.hasMuted(target, MuteMenuItem.TP_REQUESTS))
			error(target.getName() + " has teleport requests disabled!");

		Location targetLocation = Nerd.of(target).getLocation();
		World targetWorld = targetLocation.getWorld();
		WorldGroup targetWorldGroup = WorldGroup.of(targetWorld);

		if (!isStaff()) {
			String cannotTeleport = "Cannot teleport to " + nickname(target);
			if (targetWorldGroup.isMinigames())
				error(cannotTeleport + ", they are playing minigames");

			if (targetWorldGroup.equals(WorldGroup.STAFF) || (targetWorld.equals(BearFair21.getWorld()) && !BearFair21.canWarp()))
				error(cannotTeleport + ", they are in a staff world");
		}

		Trust trust = new TrustService().get(target);
		if (trust.trusts(Type.TELEPORTS, player())) {
			player().teleportAsync(targetLocation, TeleportCause.COMMAND);
			send(PREFIX + "Poofing to &e" + Nickname.of(target) + (target.isOnline() && PlayerUtils.canSee(player(), target) ? "" : " &3(Offline)"));
			return;
		}

		Player targetPlayer = convertToPlayer(target);

		Poof request = new Poof(player(), targetPlayer, Poof.PoofType.POOF);
		service.save(request);
		send(json("&ePoof &3request sent to " + Nickname.of(targetPlayer) + ". ").next("&eClick to cancel").command("poof cancel"));
		send(targetPlayer, " &e" + nickname() + " &3is asking to poof &eto you&3.");
		send(targetPlayer, json("&3 Click one &3 || &3 ")
				.group()
				.next("&a&lAccept")
				.command("/poof accept")
				.hover("&eClick &3to accept")
				.group()
				.next("&3 &3 || &3 ")
				.group()
				.next("&c&lDeny")
				.command("/poof deny")
				.hover("&eClick &3to deny")
				.group()
				.next("&3 &3 ||"));
	}

	@Path("accept")
	void accept() {
		accept(player());
	}

	@Path("deny")
	void deny() {
		deny(player());
	}

	@Path("cancel")
	void cancel() {
		cancel(player());
	}

	public void accept(Player receiver) {
		Poof request = service.getByReceiver(receiver);
		if (request == null || request.isExpired()) {
			error(receiver, "You do not have any pending Poof requests");
			return;
		}

		request.setExpired(true);
		service.save(request);

		OfflinePlayer toPlayer = request.getReceiverPlayer();
		OfflinePlayer fromPlayer = request.getSenderPlayer();
		if (request.getType() == Poof.PoofType.POOF_HERE) {
			toPlayer = request.getSenderPlayer();
			fromPlayer = request.getReceiverPlayer();
		}

		if (!fromPlayer.isOnline() || fromPlayer.getPlayer() == null)
			throw new PlayerNotOnlineException(fromPlayer);

		if (request.getType() == Poof.PoofType.POOF)
			fromPlayer.getPlayer().teleportAsync(toPlayer.getPlayer().getLocation(), TeleportCause.COMMAND);
		else
			fromPlayer.getPlayer().teleportAsync(request.getTeleportLocation(), TeleportCause.COMMAND);

		if (request.getType() == Poof.PoofType.POOF) {
			send(toPlayer.getPlayer(), "&3You accepted &e" + Nickname.of(fromPlayer) + "'s &3poof request");
			send(fromPlayer.getPlayer(), "&e" + Nickname.of(toPlayer) + " &3accepted your poof request");
		} else {
			send(fromPlayer.getPlayer(), "&3You accepted &e" + Nickname.of(toPlayer) + "'s &3poof-here request");
			if (toPlayer.isOnline())
				send(toPlayer.getPlayer(), "&e" + Nickname.of(fromPlayer) + " &3accepted your poof-here request");
		}
	}

	public void deny(Player receiver) {
		Poof request = service.getByReceiver(receiver);
		if (request == null || request.isExpired()) {
			error(receiver, "You do not have any pending Poof requests");
			return;
		}

		request.setExpired(true);
		service.save(request);

		OfflinePlayer toPlayer = request.getReceiverPlayer();
		OfflinePlayer fromPlayer = request.getSenderPlayer();
		if (request.getType() == Poof.PoofType.POOF_HERE) {
			toPlayer = request.getSenderPlayer();
			fromPlayer = request.getReceiverPlayer();
		}

		if (!fromPlayer.isOnline())
			throw new PlayerNotOnlineException(fromPlayer);

		if (request.getType() == Poof.PoofType.POOF) {
			send(toPlayer.getPlayer(), "&3You denied &e" + Nickname.of(fromPlayer) + "'s &3poof request");
			send(fromPlayer.getPlayer(), "&e" + Nickname.of(toPlayer) + " &3denied your poof request");
		} else {
			send(fromPlayer.getPlayer(), "&3You denied &e" + Nickname.of(toPlayer) + "'s &3poof-here request");
			send(toPlayer.getPlayer(), "&e" + Nickname.of(fromPlayer) + " &3denied your poof-here request");
		}
	}

	public void cancel(Player sender) {
		Poof request = service.getBySender(sender);
		if (request == null || request.isExpired()) {
			error(sender, "You do not have any pending Poof requests");
			return;
		}

		request.setExpired(true);
		service.save(request);

		OfflinePlayer toPlayer = request.getReceiverPlayer();
		OfflinePlayer fromPlayer = request.getSenderPlayer();
		if (request.getType() == Poof.PoofType.POOF_HERE) {
			toPlayer = request.getSenderPlayer();
			fromPlayer = request.getReceiverPlayer();
		}

		if (!fromPlayer.isOnline())
			throw new PlayerNotOnlineException(fromPlayer);

		if (request.getType() == Poof.PoofType.POOF) {
			send(toPlayer.getPlayer(), "&e" + Nickname.of(fromPlayer) + " &3canceled their poof request");
			send(fromPlayer.getPlayer(), "&3You canceled your poof request to &e" + Nickname.of(toPlayer));
		} else {
			send(fromPlayer.getPlayer(), "&e" + Nickname.of(toPlayer) + " &3canceled their poof-here request");
			send(toPlayer.getPlayer(), "&3You canceled your poof-here request to &e" + Nickname.of(fromPlayer));
		}
	}

}
