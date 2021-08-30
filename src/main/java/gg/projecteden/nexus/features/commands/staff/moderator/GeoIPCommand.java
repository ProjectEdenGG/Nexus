package gg.projecteden.nexus.features.commands.staff.moderator;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission("group.moderator")
public class GeoIPCommand extends CustomCommand implements Listener {

	public GeoIPCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		if (Nexus.getEnv() == Env.PROD)
			Tasks.repeatAsync(TickTime.MINUTE, TickTime.HOUR, GeoIPCommand::writeFiles);
	}

	@Async
	@SneakyThrows
	@Path("<player>")
	void geoip(GeoIP geoIp) {
		String location = geoIp.getFriendlyLocationString();
		if (isPlayer())
			send(json("&3Location of &e" + geoIp.getName() + "&3: &e" + location).hover(geoIp.getIp()).insert(geoIp.getIp()));
		else
			send("Location of " + geoIp.getName() + " (" + geoIp.getIp() + "): " + location);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Tasks.async(() -> {
			Player player = event.getPlayer();
			String ip = player.getAddress().getHostString();
			new GeoIPService().request(player.getUniqueId(), ip);
		});
	}

	@Async
	@Path("write")
	@Permission(value = "group.admin", absolute = true)
	void write() {
		writeFiles();
		send(PREFIX + "Done");
	}

	@Path("debug [player]")
	@Permission(value = "group.admin", absolute = true)
	void debug(@Arg("self") OfflinePlayer player) {
		send(json(PREFIX + "Click to copy Mongo query").copy("db.geoip.find({\"_id\":\"" + player.getUniqueId().toString() + "\"}).pretty();"));
	}

	@SneakyThrows
	static void writeFiles() {
		final Map<String, Integer> hoursMap = new HashMap<>();
		final Map<String, Integer> playersMap = new HashMap<>();
		final Map<String, String> countriesMap = new HashMap<>();

		HoursService hoursService = new HoursService();

		new GeoIPService().getAll().forEach(geoIp -> {
			String key = geoIp.getCountryCode();
			if (key == null)
				return;

			if (!countriesMap.containsKey(key))
				countriesMap.put(key, geoIp.getCountryName());

			Hours hours = hoursService.get(geoIp);
			int hoursPlayed = hours.getTotal() / 3600;
			if (hoursPlayed < 1) return;

			if (hoursMap.containsKey(key))
				hoursPlayed += hoursMap.get(key);
			hoursMap.put(key, hoursPlayed);

			int players = 1;
			if (playersMap.containsKey(key))
				players += playersMap.get(key);
			playersMap.put(key, players);
		});

		Map<String, Integer> hoursSorted = sort(hoursMap);
		Map<String, Integer> playersSorted = sort(playersMap);

		File folder = Paths.get("plugins/website/").toFile();
		if (!folder.exists()) folder.createNewFile();
		writeHtml(Paths.get("plugins/website/geoipdata-hours.html"), hoursSorted, countriesMap);
		writeHtml(Paths.get("plugins/website/geoipdata-players.html"), playersSorted, countriesMap);
		writeJs(Paths.get("plugins/website/geoipdata-hours.js"), hoursSorted, "hours");
		writeJs(Paths.get("plugins/website/geoipdata-players.js"), playersSorted, "players");
	}

	private static void writeHtml(java.nio.file.Path htmlFile, Map<String, Integer> sortedMap, Map<String, String> countriesMap) {
		try (BufferedWriter writer = Files.newBufferedWriter(htmlFile, StandardCharsets.UTF_8)) {
			int index = 0;
			for (Entry<String, Integer> entry : sortedMap.entrySet()) {
				String country = countriesMap.get(entry.getKey());
				Integer count = entry.getValue();
				++index;

				writer.write("  <tr>" + System.lineSeparator());
				writer.write("    <th>" + index + "</th>" + System.lineSeparator());
				writer.write("    <th>" + country + "</th>" + System.lineSeparator());
				writer.write("    <th>" + count + "</th>" + System.lineSeparator());
				writer.write("  </tr>" + System.lineSeparator());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void writeJs(java.nio.file.Path htmlFile, Map<String, Integer> sortedMap, String var) {
		try (BufferedWriter writer = Files.newBufferedWriter(htmlFile, StandardCharsets.UTF_8)) {
			writer.write("var " + var + " = {" + System.lineSeparator());

			writer.write(sortedMap.entrySet().stream()
					.map(entry -> "  \"" + entry.getKey().toUpperCase() + "\": " + entry.getValue())
					.collect(Collectors.joining("," + System.lineSeparator())));

			writer.write(System.lineSeparator() + "};");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private static LinkedHashMap<String, Integer> sort(Map<String, Integer> playersMap) {
		return playersMap.entrySet().stream()
				.sorted(Entry.<String, Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

}
