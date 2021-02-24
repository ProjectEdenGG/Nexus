package me.pugabyte.nexus.features.commands.staff.punishments;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.models.punishments.PunishmentsService;
import me.pugabyte.nexus.utils.Env;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.Optional;

@Environments(Env.DEV)
@NoArgsConstructor
@Permission("group.moderator")
public class NexusBanCommand extends CustomCommand implements Listener {

	public NexusBanCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	@Path("<player> <time reason>")
	void ban(Punishments punishments, String args) {
		Punishment ban = Punishment.builder()
				.uuid(punishments.getUuid())
				.punisher(uuid())
				.type(PunishmentType.BAN)
				.input(args)
				.build();

		punishments.add(ban);
	}

	@EventHandler
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(event.getUniqueId());
		Optional<Punishment> banMaybe = punishments.getActiveBan();
		if (banMaybe.isPresent()) {
			Punishment ban = banMaybe.get();
			event.disallow(Result.KICK_BANNED, ban.getDisconnectMessage());
			Chat.broadcastIngame(
					json(PREFIX + "&e" + punishments.getName() + " &ctried to join, but is banned for &7" + ban.getReason()
							+ " &c(&e" + ban.getTimeLeft() + "&c)")
							.hover("&eClick for more information")
							.command("/history " + punishments.getName()),
					StaticChannel.STAFF
			);
			Chat.broadcastDiscord(
					json(DISCORD_PREFIX + punishments.getName() + " tried to join, but is banned for " + ban.getReason()
							+ " (" + ban.getTimeLeft() + ")"),
					StaticChannel.STAFF
			);
		}
	}

}
