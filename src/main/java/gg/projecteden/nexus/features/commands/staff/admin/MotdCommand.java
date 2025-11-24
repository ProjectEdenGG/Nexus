package gg.projecteden.nexus.features.commands.staff.admin;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.event.server.PaperServerListPingEvent.ListedPlayerInfo;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.utils.DateUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class MotdCommand extends CustomCommand implements Listener {
	GeoIPService geoIPService = new GeoIPService();
	PunishmentsService punishmentsService = new PunishmentsService();

	private static final String motdTop = "&f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f " +
		"&a&l⚘ &f &#ffff44&lProject Eden" + (Nexus.getEnv() == Env.PROD ? "" : " &6[" + Nexus.getEnv().name() + "]") + " &f &a&l⚘\n";
	private static final String motdBottom = "&f &f &3Survival &7| &3Creative &7| &3Minigames &7| &3Close Community";
	private static final String originalMotd = motdTop + motdBottom;
	private static @Nullable String motd = originalMotd;

	private static final int MAX_CHARS_ISH = 48; // assumes monospace font
	private static final double SPACE_WIDTH_MULTIPLIER = 1.35;

	public MotdCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<text...>")
	@Description("Update the server's server list MOTD")
	void motd(String text) {
		motd = StringUtils.colorize(text.replace("\\n", System.lineSeparator()));
		send(PREFIX + "Motd updated");
	}

	@Path("line2 <text...>")
	@Description("Update the server's server list MOTD second line")
	void motd_line2_(String text) {
		motd = motdTop + StringUtils.colorize(text.replace("\\n", System.lineSeparator()));
		send(PREFIX + "Motd updated");
	}

	@Path("reset")
	@Description("Reset the server's MOTD to default")
	void motdReset() {
		motd = originalMotd;
		send(PREFIX + "Motd Reset");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void on(PaperServerListPingEvent event) {
		InetAddress address = event.getAddress();
		if (address.isLoopbackAddress())
			return;

		List<ListedPlayerInfo> newListedPlayers = new ArrayList<>();
		for (ListedPlayerInfo listedPlayer : new ArrayList<>(event.getListedPlayers())) {
			String name = listedPlayer.name();
			if (!name.equalsIgnoreCase("anonymous player")) {
				name = Nickname.of(listedPlayer.id());
			}

			newListedPlayers.add(new ListedPlayerInfo(name, listedPlayer.id()));
		}
		event.getListedPlayers().clear();
		event.getListedPlayers().addAll(newListedPlayers);


		String ipAddress = address.getHostAddress();

		GeoIP geoIP = getBestMatch(ipAddress);
		if (geoIP == null) {
			if (motd != null)
				event.setMotd(StringUtils.colorize(motd));
			return;
		}

		Nerd nerd = geoIP.getNerd();
		if (nerd.isOnline()) // Event sometimes fires when player is online
			return;

		if (CooldownService.isNotOnCooldown(nerd.getUuid(), "server-ping_" + nerd.getUuid(), TickTime.MINUTE.x(5)))
			Nexus.log("ServerPingEvent: " + ipAddress + " -> " + nerd.getNickname());

		// Message
		String motd = getMOTD(nerd);
		if (motd != null)
			event.setMotd(StringUtils.colorize(motd));

		// Icon
		event.setServerIcon(getIcon());
	}

	private @Nullable String getMOTD(Nerd nerd) {
		String message = motd;
		String line2 = null;

//		LocalResourcePackUser rpUser = rpUserService.get(nerd);
//		if(rpUser.isEnabled() && nerd.getRank().isAdmin()) {
//			message = "&f\u797E";
//		}

		EdenEvent edenEvent = EdenEvent.getActiveEvent(nerd);
		if (edenEvent != null && edenEvent.getMotd() != null)
			line2 = edenEvent.getMotd();

		if (new MinigameNight().isNow())
			line2 = "&3Join us in &eMinigame Night! &3Use &e/gl";

		if (nerd.getBirthday() != null && LocalDate.now().isEqual(nerd.getBirthday()))
			line2 = "&3Happy birthday &e" + nerd.getNickname() + "&3!";

		if (line2 != null) {
			int padding = (int) (((MAX_CHARS_ISH - StringUtils.stripColor(line2).length()) / 2.0) * SPACE_WIDTH_MULTIPLIER);
			message = motdTop + "&f ".repeat(padding) + line2;
		}

		// Default
		return message;
	}

	@SneakyThrows
	private @NotNull CachedServerIcon getIcon() {
		Month month = LocalDate.now().getMonth();
		LocalDateTime dateTime = DateUtils.getDateTime(month);

		return IconType.getIconType(dateTime).getServerIcon();
	}

	@Getter
	private enum IconType {
		DEFAULT,
		HALLOWEEN(Month.OCTOBER),

		ORIENTATION_ASEXUAL(Month.JUNE),
		ORIENTATION_BISEXUAL(Month.JUNE),
		ORIENTATION_DEMI(Month.JUNE),
		ORIENTATION_GAY(Month.JUNE),
		ORIENTATION_LESBIAN(Month.JUNE),
		ORIENTATION_PANSEXUAL(Month.JUNE),
		ORIENTATION_POLYSEXUAL(Month.JUNE),
		;

		private final LocalDateTime startTime;
		private final LocalDateTime endTime;

		private static final String FOLDER = "plugins/Nexus/servericons/";

		IconType() {
			this.startTime = null;
			this.endTime = null;
		}

		IconType(Month month) {
			this(month, month);
		}

		IconType(Month start, Month end) {
			this.startTime = DateUtils.getStart(start);
			this.endTime = DateUtils.getEnd(end);
		}

		public File getFile() {
			return new File(FOLDER + name().toLowerCase() + ".png");
		}

		@SneakyThrows
		public CachedServerIcon getServerIcon() {
			final File file = this.getFile();
			if (file.exists())
				return Bukkit.getServer().loadServerIcon(file);

			if (Nexus.getEnv() == Env.PROD)
				Nexus.severe("Server icon not found at " + file.getPath());

			return Bukkit.getServerIcon();
		}

		public static IconType getIconType(LocalDateTime dateTime) {
			Set<IconType> options = Arrays.stream(values())
				.filter(type -> type.getStartTime() != null && type.getEndTime() != null)
				.filter(type -> DateUtils.isWithin(dateTime, type.getStartTime(), type.getEndTime()))
				.collect(Collectors.toSet());

			if (!options.isEmpty())
				return RandomUtils.randomElement(options);

			return DEFAULT;
		}
	}

	private @Nullable GeoIP getBestMatch(String ip) {
		List<GeoIP> geoIPList = geoIPService.getPlayers(ip);
		if (geoIPList.isEmpty())
			return null;

		if (geoIPList.size() == 1)
			return geoIPList.get(0);

		HashMap<UUID, GeoIP> geoIPMapReference = new HashMap<>();
		geoIPList.forEach(geoIP -> geoIPMapReference.put(geoIP.getUuid(), geoIP));

		// remove online players
		for (Player player : OnlinePlayers.getAll()) {
			geoIPList.remove(geoIPMapReference.get(player.getUniqueId()));
		}

		// remove any actively banned players
		for (GeoIP geoIP : new ArrayList<>(geoIPList)) {
			Punishments punishments = punishmentsService.get(geoIP);
			punishments.getAnyActiveBan().ifPresent(punishment -> geoIPList.remove(geoIP));

			if (geoIP.getTimestamp() == null)
				geoIPList.remove(geoIP);
		}

		// TODO: add any other checks to determine the best player under this IP

		// TODO: ADD DEBUG

		if (geoIPList.isEmpty())
			return null;

		return Collections.max(geoIPList, Comparator.comparing(geoip -> new HoursService().get(geoip).getTotal()));
	}

}
