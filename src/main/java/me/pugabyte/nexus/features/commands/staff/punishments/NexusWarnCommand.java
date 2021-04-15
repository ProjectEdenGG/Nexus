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
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Environments(Env.DEV)
@NoArgsConstructor
@Permission("group.moderator")
public class NexusWarnCommand extends CustomCommand implements Listener {

	public NexusWarnCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	@Path("<player> <reason...>")
	void ban(Punishments punishments, String input) {
		punishments.add(Punishment.ofType(PunishmentType.WARN)
				.uuid(punishments.getUuid())
				.punisher(uuid())
				.input(input));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(Time.SECOND.x(5), () -> {
			if (!event.getPlayer().isOnline())
				return;

			final PunishmentsService service = new PunishmentsService();
			final Punishments punishments = service.get(event.getPlayer());
			List<Punishment> warnings = punishments.getNewWarnings();
			if (!warnings.isEmpty()) {
				// TODO Not sure I like this formatting
				punishments.send("&cYou received a warning while you were offline:");
				for (Punishment warning : warnings) {
					punishments.send(" &7- &c" + warning.getReason() + " (" + warning.getTimeSince() + ")");

					String message = "&e" + punishments.getName() + " &chas received their warning for &7" + warning.getReason();

					JsonBuilder ingame = json(PREFIX + message)
							.hover("&eClick for more information")
							.command("/history " + punishments.getName());

					Chat.broadcastIngame(ingame, StaticChannel.STAFF);
					Chat.broadcastDiscord(DISCORD_PREFIX + stripColor(message), StaticChannel.STAFF);
				}
				punishments.send("");
				punishments.send("&cPlease make sure to read the /rules to avoid future punishments");
			}

			// Try to be more sure they actually saw the warning
			Tasks.wait(Time.SECOND.x(5), () -> {
				if (!event.getPlayer().isOnline())
					return;

				for (Punishment warning : warnings)
					warning.received();

				service.save(punishments);
			});
		});

	}

}
