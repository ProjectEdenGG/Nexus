package me.pugabyte.nexus.features.commands.staff.punishments;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.chat.events.ChatEvent;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment.PunishmentType;
import me.pugabyte.nexus.models.punishments.PunishmentsService;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.JsonBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Environments(Env.DEV)
@NoArgsConstructor
@Permission("group.moderator")
public class NexusMuteCommand extends CustomCommand implements Listener {

	public NexusMuteCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Punishments.PREFIX;
		DISCORD_PREFIX = Punishments.DISCORD_PREFIX;
	}

	@Path("<player> <time/reason...>")
	void ban(Punishments punishments, String input) {
		punishments.add(Punishment.ofType(PunishmentType.MUTE)
				.uuid(punishments.getUuid())
				.punisher(uuid())
				.input(input));
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		Chatter chatter = event.getChatter();
		if (chatter == null)
			return;

		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(chatter);
		Optional<Punishment> muteMaybe = punishments.getActiveMute();
		muteMaybe.ifPresent(mute -> {
			event.setCancelled(true);
			mute.received();

			String message = "&e" + punishments.getName() + " &cspoke while muted: &7" + mute.getReason() + " &c(&e" + mute.getTimeLeft() + "&c)";

			JsonBuilder ingame = json(PREFIX + message)
					.hover("&eClick for more information")
					.command("/history " + punishments.getName());

			Chat.broadcastIngame(ingame, StaticChannel.STAFF);
			Chat.broadcastDiscord(DISCORD_PREFIX + stripColor(message), StaticChannel.STAFF);
		});
	}

}
