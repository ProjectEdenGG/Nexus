package gg.projecteden.nexus.features.justice;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.chat.events.ChatEvent;
import gg.projecteden.nexus.features.chat.events.DiscordChatEvent;
import gg.projecteden.nexus.features.chat.events.MinecraftChatEvent;
import gg.projecteden.nexus.features.chat.events.PrivateChatEvent;
import gg.projecteden.nexus.features.commands.BoopCommand;
import gg.projecteden.nexus.features.commands.teleport.request.TeleportHereRequestCommand;
import gg.projecteden.nexus.features.commands.teleport.request.TeleportRequestCommand;
import gg.projecteden.nexus.features.economy.commands.PayCommand;
import gg.projecteden.nexus.features.tickets.ReportCommand;
import gg.projecteden.nexus.features.tickets.TicketCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.events.CommandRunEvent;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.afk.events.NotAFKEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIP.Security;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.PunishmentType;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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

import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;
import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

@NoArgsConstructor
public class Justice extends Feature implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("Justice");
	public static final String DISCORD_PREFIX = StringUtils.getDiscordPrefix("Justice");
	public static final String URL = "https://justice." + Nexus.DOMAIN;

	private void broadcast(Punishment punishment, String message) {
		broadcast(historyClick(punishment, new JsonBuilder(message)));
	}

	private void broadcast(JsonBuilder message) {
		Broadcast.log().prefix("Justice").message(message).send();
	}

	private JsonBuilder historyClick(Punishment punishment, JsonBuilder ingame) {
		return ingame.hover("&eClick for more information").command("/history " + punishment.getName());
	}

	public static boolean isNewPlayer(PlayerOwnedObject nerd) {
		if (Rank.of(nerd).gt(Rank.GUEST))
			return false;
		if (new HoursService().get(nerd).has(TickTime.HOUR))
			return false;

		return true;
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

			ban.received();
			event.disallow(Result.KICK_BANNED, ban.getDisconnectMessage());

			String message = "&e" + punishments.getNickname() + " &ctried to join, but is " + ban.getType().getPastTense();
			if (ban.hasReason())
					message += " for &7" + ban.getReason();
			message += " &c(" + ban.getTimeLeft() + ")";

			broadcast(ban, message);
		};

		punishments.getAnyActiveBan().ifPresent(kick);

		if (!punishments.getRank().isStaff())
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
			TeleportRequestCommand.class,
			TeleportHereRequestCommand.class,
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

			punishments.sendMessage("&cYou cannot use this command while muted (" + mute.getTimeLeft() + ")");
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

			punishments.sendMessage("&cYou are muted" + (isNullOrEmpty(mute.getReason()) ? "" : " for &7" + mute.getReason()) + " &c(" + mute.getTimeLeft() + ")");
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void mute_onMinecraftChatEvent(MinecraftChatEvent event) {
		Nerd nerd = Nerd.of(event.getChatter());
		if (!isNewPlayer(nerd))
			return;
		if (nerd.hasMoved())
			return;

		event.setCancelled(true);
		nerd.sendMessage("&cYou must move before you can speak in chat");
		String message = "&e" + nerd.getNickname() + " &ctried to speak before moving: &7" + getMessageDetails(event);
		Broadcast.staff().prefix("Justice").message(message).send();
	}

	// Warning
	@EventHandler
	public void warning_onJoin(PlayerJoinEvent event) {
		Tasks.wait(TickTime.SECOND.x(5), () -> Punishments.of(event.getPlayer()).tryShowWarns());
	}

	@EventHandler
	public void warning_onNotAFK(NotAFKEvent event) {
		Tasks.wait(TickTime.SECOND.x(2), () -> {
			if (event.getUser().isOnline())
				Punishments.of(event.getUser().getOnlinePlayer()).tryShowWarns();
		});
	}

	// Watchlist
	@EventHandler
	public void watchlist_onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Tasks.waitAsync(TickTime.SECOND, () -> {
			if (!player.isOnline())
				return;

			Function<Punishment, JsonBuilder> notification = watchlist -> {
				String punisher = Nickname.of(watchlist.getPunisher());
				String timestamp = shortDateFormat(watchlist.getTimestamp().toLocalDate());
				return historyClick(watchlist, new JsonBuilder("&e" + watchlist.getName() + " &cwas watchlisted for &e"
						+ watchlist.getReason() + " &cby &e" + punisher + " &con &e" + timestamp));
			};

			if (Rank.of(player).isMod())
				for (Player onlinePlayer : OnlinePlayers.getAll())
					Punishments.of(onlinePlayer).getActiveWatchlist().ifPresent(watchlist ->
							PlayerUtils.send(player, new JsonBuilder(PREFIX).next(notification.apply(watchlist))));

			Punishments.of(player).getActiveWatchlist().ifPresent(watchlist -> Broadcast.staffIngame().prefix("Justice").message(notification.apply(watchlist)).send());
		});
	}

	// Alts
	@EventHandler
	public void alts_onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Tasks.waitAsync(TickTime.SECOND, () -> {
			if (!player.isOnline())
				return;

			if (player.hasPermission("justice.alts.hide"))
				return;

			Punishments.of(player).sendAltsMessage(json -> Broadcast.staffIngame().message(json).send());
		});
	}

	// Bot prevention
	@SneakyThrows
	@EventHandler(priority = EventPriority.LOW)
	public void bots_onJoin(AsyncPlayerPreLoginEvent event) {
		if (Bukkit.getOfflinePlayer(event.getUniqueId()).hasPlayedBefore())
			return;

		String ip = event.getAddress().getHostAddress();

		GeoIPService service = new GeoIPService();
		GeoIP geoip = service.get(event.getUniqueId());
		Security security = geoip.getSecurity(ip);
		service.save(geoip);

		String name = event.getName();
		if (security == null) {
			Nexus.warn("Security data for " + name + " on " + ip + " is null");
			return;
		}

		int fraudScore = security.getFraudScore();
		Nexus.log("Fraud score for " + name + ": " + fraudScore);

		if (geoip.isMitigated())
			return;

		// TODO git#672
		if (true)
			return;

		Punishments punishments = Punishments.of(event.getUniqueId());

		if (punishments.getActiveMute().isPresent())
			return;

		if (fraudScore >= 75) {
			punishments.add(Punishment.ofType(PunishmentType.MUTE)
					.punisher(UUID0)
					.input("Suspected bot")
					.now(true));

			geoip.setMitigated(true);
			service.save(geoip);

			String message = "&e" + name + " &chas a fraud score of &e" + fraudScore + "&c, they have been automatically muted";

			Broadcast.staff().prefix("Justice").message(message).send();
		}

	}

}
