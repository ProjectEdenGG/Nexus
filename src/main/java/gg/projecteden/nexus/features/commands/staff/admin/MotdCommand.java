package gg.projecteden.nexus.features.commands.staff.admin;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class MotdCommand extends CustomCommand implements Listener {
	GeoIPService geoIPService = new GeoIPService();
	LocalResourcePackUserService rpUserService = new LocalResourcePackUserService();
	PunishmentsService punishmentsService = new PunishmentsService();

	private static final String motdTop = "&f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f " +
		"&a&l⚘ &f &#ffff44&lProject Eden" + (Nexus.getEnv() == Env.PROD ? "" : " &6[" + Nexus.getEnv().name() + "]") + " &f &a&l⚘\n";
	private static final String motdBottom = "&f &f &3Survival &7| &3Creative &7| &3Minigames &7| &3Close Community";
	private static final String originalMotd = motdTop + motdBottom;
	private static @Nullable String motd = originalMotd;

	public MotdCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<text...>")
	void motd(String text) {
		motd = colorize(text.replace("\\n", System.lineSeparator()));
		send(PREFIX + "Motd updated");
	}

	@Path("reset")
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

		String motd = getMOTD(ipAddress);
		if (motd == null)
			return;

		event.setMotd(colorize(motd));
	}

	private @Nullable String getMOTD(String ipAddress) {
		String message = motd;

		GeoIP geoIP = getBestMatch(ipAddress);
		if (geoIP == null)
			return message;

		Nerd nerd = geoIP.getNerd();
		Nexus.log("ServerPingEvent: " + ipAddress + " -> " + nerd.getNickname());

		// Unique

//		LocalResourcePackUser rpUser = rpUserService.get(nerd);
//		if(rpUser.isEnabled())
//			message = "&f\u797E";

		if (LocalDate.now().isEqual(nerd.getBirthday()) || Dev.WAKKA.is(nerd))
			message = motdTop
				+ "&f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f &f " + "&3Happy birthday &e" + nerd.getNickname() + "&3!";

		// Default
		return message;
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
		for (GeoIP geoIP : geoIPList) {
			Punishments punishments = punishmentsService.get(geoIP);
			punishments.getAnyActiveBan().ifPresent(punishment -> geoIPList.remove(geoIP));
		}

		// TODO: add any other checks to determine the best player under this IP

		if (geoIPList.isEmpty())
			return null;

		return geoIPList.get(0);
	}

}
