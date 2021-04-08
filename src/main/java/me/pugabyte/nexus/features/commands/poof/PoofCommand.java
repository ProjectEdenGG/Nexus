package me.pugabyte.nexus.features.commands.poof;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.poof.Poof;
import me.pugabyte.nexus.models.poof.PoofService;
import me.pugabyte.nexus.models.trust.Trust;
import me.pugabyte.nexus.models.trust.Trust.Type;
import me.pugabyte.nexus.models.trust.TrustService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.time.LocalDateTime;

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
		Tasks.repeatAsync(Time.SECOND.x(5), Time.MINUTE, () -> {
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

		Location targetLocation = Nerd.of(target).getLocation();
		WorldGroup targetWorldGroup = WorldGroup.get(targetLocation);

		if (!isStaff() && targetWorldGroup.equals(WorldGroup.MINIGAMES))
			error("Cannot teleport to " + nickname(target) + ", they are playing minigames");

		if (!isStaff() && targetWorldGroup.equals(WorldGroup.STAFF))
			error("Cannot teleport to " + nickname(target) + ", they are in a staff world");

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
		send(targetPlayer, "  &e" + nickname() + " &3is asking to poof &eto you&3.");
		send(targetPlayer, json("&3  Click one  ||  &a&lAccept")
				.command("/poof accept")
				.hover("&eClick &3to accept")
				.group()
				.next("  &3||  &3")
				.group()
				.next("&c&lDeny")
				.command("/poof deny")
				.hover("&eClick &3to deny.")
				.group()
				.next("&3  ||"));
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
