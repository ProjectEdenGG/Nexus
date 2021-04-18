package me.pugabyte.nexus.features.commands.staff.moderator.punishments;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.chat.events.ChatEvent;
import me.pugabyte.nexus.features.chat.events.DiscordChatEvent;
import me.pugabyte.nexus.features.chat.events.PrivateChatEvent;
import me.pugabyte.nexus.features.commands.BoopCommand;
import me.pugabyte.nexus.features.commands.poof.PoofCommand;
import me.pugabyte.nexus.features.commands.poof.PoofHereCommand;
import me.pugabyte.nexus.features.economy.commands.PayCommand;
import me.pugabyte.nexus.features.tickets.ReportCommand;
import me.pugabyte.nexus.features.tickets.TicketCommand;
import me.pugabyte.nexus.framework.commands.Commands;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.Punishment;
import me.pugabyte.nexus.models.punishments.PunishmentsService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
@Permission("group.moderator")
public class PunishmentsCommand extends CustomCommand implements Listener {

	public PunishmentsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send("&eGriefing");
		send("    &c/calcban <# of past griefing bans> <# of blocks griefed>");
		line();
		send("&eChat");
		send("    &3Try to keep it to &cmutes &3for established members of the community. Give them time to cool down. Otherwise short bans");
		line();
		send("&eHacks / death traps / obscene structures/skins");
		send("    &cMax of 3 days&3, then permanent");
		line();
		send("&eBan evasions");
		send("    &cMatch original ban&3, add a little to both if it was malicious");
		line();
		send("&eOther assholery");
		send("    &3Generally &c1 day&3, max of 3 days for first ban");
		line();
		send("&3If they are &enot active &3when you find reason to ban, make sure to send a &c/warning &3too/instead so they will receive the message instead of not knowing they were banned at all.");
	}

	// Ban
	@EventHandler
	public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(event.getUniqueId());
		Optional<Punishment> banMaybe = punishments.getAnyActiveBan();
		banMaybe.ifPresent(ban -> {
			event.disallow(Result.KICK_BANNED, ban.getDisconnectMessage());
			ban.received();
			service.save(punishments);

			String message = "&e" + punishments.getName() + " &ctried to join, but is banned for &7" + ban.getReason() + " &c(" + ban.getTimeLeft() + ")";

			JsonBuilder ingame = json(PREFIX + message)
					.hover("&eClick for more information")
					.command("/history " + punishments.getName());

			Chat.broadcastIngame(ingame, StaticChannel.STAFF);
			Chat.broadcastDiscord(DISCORD_PREFIX + stripColor(message), StaticChannel.STAFF);
		});
	}

	// Mute
	@EventHandler
	public void onChat(ChatEvent event) {
		Chatter chatter = event.getChatter();
		if (chatter == null)
			return;

		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(chatter);
		punishments.getActiveMute().ifPresent(mute -> {
			event.setCancelled(true);
			mute.received();
			service.save(punishments);

			String originalMessage = event.getOriginalMessage();
			if (event instanceof PrivateChatEvent)
				originalMessage = "To " + ((PrivateChatEvent) event).getRecipientNames() + ": " + originalMessage;
			else if (event instanceof DiscordChatEvent)
				originalMessage = "#" + ((DiscordChatEvent) event).getDiscordTextChannel().getName() + ": " + originalMessage;

			String message = "&e" + punishments.getName() + " &cspoke while muted: &7" + originalMessage + " &c(" + mute.getTimeLeft() + ")";

			JsonBuilder ingame = json(PREFIX + message)
					.hover("&eClick for more information")
					.command("/history " + punishments.getName());

			Chat.broadcastIngame(ingame, StaticChannel.STAFF);
			Chat.broadcastDiscord(DISCORD_PREFIX + stripColor(message), StaticChannel.STAFF);

			punishments.send("&cYou are muted" + (isNullOrEmpty(mute.getReason()) ? "" : " for &7" + mute.getReason()) + " (" + mute.getTimeLeft() + ")");
		});
	}

	private static List<String> aliases(Class<? extends CustomCommand> clazz) {
		CustomCommand customCommand = Commands.get(clazz);
		if (customCommand != null)
			return customCommand.getAliases();
		return Collections.emptyList();
	}

	private static final List<Class<? extends CustomCommand>> muteCommandBlacklist = Arrays.asList(
			PoofCommand.class,
			PoofHereCommand.class,
			BoopCommand.class,
			PayCommand.class,
			TicketCommand.class,
			ReportCommand.class
	);

	@EventHandler
	public void onCommand(CommandEvent event) {
		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(event.getPlayer());
		punishments.getActiveMute().ifPresent(mute -> {
			if (!muteCommandBlacklist.contains(event.getCommand().getClass()))
				return;

			event.setCancelled(true);
			String message = "&e" + punishments.getName() + " &cused a blacklisted command while muted: &7" + event.getOriginalMessage() + " &c(" + mute.getTimeLeft() + ")";

			JsonBuilder ingame = json(PREFIX + message)
					.hover("&eClick for more information")
					.command("/history " + punishments.getName());

			Chat.broadcastIngame(ingame, StaticChannel.STAFF);
			Chat.broadcastDiscord(DISCORD_PREFIX + stripColor(message), StaticChannel.STAFF);

			punishments.send("&cYou cannot use this command while muted (" + mute.getTimeLeft() + ")");
		});
	}

	// Warning
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
