package me.pugabyte.nexus.features.justice;

import eden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.chat.events.ChatEvent;
import me.pugabyte.nexus.features.chat.events.DiscordChatEvent;
import me.pugabyte.nexus.features.chat.events.MinecraftChatEvent;
import me.pugabyte.nexus.features.chat.events.PrivateChatEvent;
import me.pugabyte.nexus.features.commands.BoopCommand;
import me.pugabyte.nexus.features.commands.poof.PoofCommand;
import me.pugabyte.nexus.features.commands.poof.PoofHereCommand;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.features.economy.commands.PayCommand;
import me.pugabyte.nexus.features.tickets.ReportCommand;
import me.pugabyte.nexus.features.tickets.TicketCommand;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.events.CommandRunEvent;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.afk.events.NotAFKEvent;
import me.pugabyte.nexus.models.chat.Chatter;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.punishments.Punishment;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.PunishmentsService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.TimeUtils.shortDateFormat;

@NoArgsConstructor
public class Justice extends Feature implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Justice");
	public static final String DISCORD_PREFIX = StringUtils.getDiscordPrefix("Justice");

	private void broadcast(Punishment punishment, String message) {
		broadcast(historyClick(punishment, new JsonBuilder(message)));
	}

	private void broadcast(JsonBuilder json) {
		Chat.broadcastIngame(new JsonBuilder(PREFIX).next(json), StaticChannel.STAFF);
		String discord = DISCORD_PREFIX + stripColor(json.toString());
		Chat.broadcastDiscord(discord, StaticChannel.STAFF);
		Discord.send(discord, TextChannel.STAFF_LOG);
	}

	private JsonBuilder historyClick(Punishment punishment, JsonBuilder ingame) {
		return ingame.hover("&eClick for more information").command("/history " + punishment.getName());
	}

	// Ban
	@EventHandler
	public void ban_onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(event.getUniqueId());

		punishments.logIp(event.getAddress().getHostAddress());

		Consumer<Punishment> kick = ban -> {
			if (event.getLoginResult() == Result.KICK_BANNED)
				return;

			event.disallow(Result.KICK_BANNED, ban.getDisconnectMessage());
			ban.received();

			String message = "&e" + punishments.getName() + " &ctried to join, but is " + ban.getType().getPastTense();
			if (ban.hasReason())
					message += " for &7" + ban.getReason();
			message += " &c(" + ban.getTimeLeft() + ")";

			broadcast(ban, message);
		};

		punishments.getAnyActiveBan().ifPresent(kick);

		if (!punishments.getNerd().getRank().isStaff())
			for (UUID alt : punishments.getAlts())
				Punishments.of(alt).getActiveAltBan().ifPresent(kick);

		service.save(punishments);
	}

	// Mute
	private void broadcastMute(Punishment mute, String message) {
		JsonBuilder json = new JsonBuilder(message);

		if (!isNullOrEmpty(mute.getReason()))
			json.hover("&eReason: &7" + mute.getReason()).hover("");

		historyClick(mute, json);
		broadcast(json);
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
	public void mute_onCommand(CommandRunEvent event) {
		if (!(event.getSender() instanceof Player))
			return;

		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(event.getPlayer());
		punishments.getActiveMute().ifPresent(mute -> {
			if (!muteCommandBlacklist.contains(event.getCommand().getClass()))
				return;

			event.setCancelled(true);
			String message = "&e" + punishments.getName() + " &cused a blacklisted command while muted: &7" + event.getOriginalMessage() + " &c(" + mute.getTimeLeft() + ")";

			broadcastMute(mute, message);

			punishments.send("&cYou cannot use this command while muted (" + mute.getTimeLeft() + ")");
		});
	}

	private String getMessageDetails(ChatEvent event) {
		String originalMessage = event.getOriginalMessage();
		if (event instanceof PrivateChatEvent)
			originalMessage = "To " + ((PrivateChatEvent) event).getRecipientNames() + ": " + originalMessage;
		else if (event instanceof DiscordChatEvent)
			originalMessage = "#" + ((DiscordChatEvent) event).getDiscordTextChannel().getName() + ": " + originalMessage;
		return originalMessage;
	}

	@EventHandler
	public void mute_onChat(ChatEvent event) {
		Chatter chatter = event.getChatter();
		if (chatter == null)
			return;

		final PunishmentsService service = new PunishmentsService();
		final Punishments punishments = service.get(chatter);
		punishments.getActiveMute().ifPresent(mute -> {
			event.setCancelled(true);
			mute.received();
			service.save(punishments);

			String originalMessage = getMessageDetails(event);

			String message = "&e" + punishments.getName() + " &cspoke while muted: &7" + originalMessage + " &c(" + mute.getTimeLeft() + ")";

			broadcastMute(mute, message);

			punishments.send("&cYou are muted" + (isNullOrEmpty(mute.getReason()) ? "" : " for &7" + mute.getReason()) + " &c(" + mute.getTimeLeft() + ")");
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void mute_onMinecraftChatEvent(MinecraftChatEvent event) {
		Nerd nerd = Nerd.of(event.getChatter());

		if (nerd.getRank().gt(Rank.GUEST))
			return;
		if (nerd.hasMoved())
			return;

		event.setCancelled(true);
		nerd.send("&cYou must move before you can speak in chat");
		String message = "&e" + nerd.getNickname() + " &ctried to speak before moving: &7" + getMessageDetails(event);
		Chat.broadcastIngame(Justice.PREFIX + message, StaticChannel.STAFF);
		Chat.broadcastDiscord(Justice.DISCORD_PREFIX + message, StaticChannel.STAFF);
	}

	// Warning
	@EventHandler
	public void warning_onJoin(PlayerJoinEvent event) {
		Tasks.wait(Time.SECOND.x(5), () -> Punishments.of(event.getPlayer()).tryShowWarns());
	}

	@EventHandler
	public void warning_onNotAFK(NotAFKEvent event) {
		Tasks.wait(Time.SECOND.x(2), () -> Punishments.of(event.getPlayer().getPlayer()).tryShowWarns());
	}

	// Watchlist
	@EventHandler
	public void watchlist_onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Tasks.waitAsync(Time.SECOND, () -> {
			if (!player.isOnline())
				return;

			Function<Punishment, JsonBuilder> notification = watchlist -> {
				String punisher = Nickname.of(watchlist.getPunisher());
				String timestamp = shortDateFormat(watchlist.getTimestamp().toLocalDate());
				return historyClick(watchlist, new JsonBuilder("&e" + watchlist.getName() + " &cwas watchlisted for &e"
						+ watchlist.getReason() + " &cby &e" + punisher + " &con &e" + timestamp));
			};

			if (Nerd.of(player).getRank().isMod())
				for (Player onlinePlayer : Bukkit.getOnlinePlayers())
					Punishments.of(onlinePlayer).getActiveWatchlist().ifPresent(watchlist ->
							PlayerUtils.send(player, new JsonBuilder(PREFIX).next(notification.apply(watchlist))));

			Punishments.of(player).getActiveWatchlist().ifPresent(watchlist ->
					Chat.broadcastIngame(new JsonBuilder(PREFIX).next(notification.apply(watchlist)), StaticChannel.STAFF));
		});
	}

	// Alts
	@EventHandler
	public void alts_onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Tasks.waitAsync(Time.SECOND, () -> {
			if (!player.isOnline())
				return;

			if (player.hasPermission("justice.alts.hide"))
				return;

			Punishments.of(player).sendAltsMessage(json -> Chat.broadcastIngame(json, StaticChannel.STAFF));
		});
	}

}
