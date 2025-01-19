package gg.projecteden.nexus.features.commands.staff.admin;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.minigames.utils.MinigameNight;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.utils.DateUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;
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
import java.util.*;

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
		month = RandomUtils.randomElement(Month.values());
		LocalDateTime dateTime = DateUtils.getDateTime(month);

		return IconType.getIconType(dateTime).getServerIcon();
	}

	@AllArgsConstructor
	private enum IconType {
		DEFAULT(null, null),
		PRIDE(DateUtils.getStart(Month.JUNE), DateUtils.getEnd(Month.JUNE)),
		//BEAR_FAIR(getStart(Month.JUNE, 29), getEnd(Month.JULY, 15)),

		DECEMBER(DateUtils.getStart(Month.DECEMBER), DateUtils.getEnd(Month.DECEMBER)),
		OCTOBER(DateUtils.getStart(Month.OCTOBER), DateUtils.getEnd(Month.OCTOBER)),
		TESTING1(DateUtils.getStart(Month.APRIL), DateUtils.getEnd(Month.APRIL)),
		TESTING2(DateUtils.getStart(Month.AUGUST), DateUtils.getEnd(Month.AUGUST)),
		TESTING3(DateUtils.getStart(Month.JANUARY), DateUtils.getEnd(Month.JANUARY)),
		TESTING4(DateUtils.getStart(Month.SEPTEMBER), DateUtils.getEnd(Month.SEPTEMBER)),
		;

		@Getter
		private final LocalDateTime startTime;
		@Getter
		private final LocalDateTime endTime;


		private static final String FOLDER = "plugins/Nexus/servericons/";

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
			for (IconType iconType : values()) {
				if (iconType.equals(DEFAULT)) continue;

				if (DateUtils.isWithin(dateTime, iconType.startTime, iconType.getEndTime()))
					return iconType;
			}

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
