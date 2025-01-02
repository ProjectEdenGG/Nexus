package gg.projecteden.nexus.features.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gg.projecteden.api.mongodb.models.nerd.Nerd;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;
import static gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;

public class SimpleHttpServer extends Feature {
	private static final int PORT = 8181;
	private static HttpServer server;

	@Override
	public void onStart() {
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
			server.createContext("/", new RequestHandler());
			server.setExecutor(null);
			server.start();
			Nexus.log("HTTP server listening on port " + PORT);
		} catch (IOException e) {
			Nexus.severe("Error starting HTTP server");
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
		server.stop(0);
	}

	public enum HttpMethod {
		GET,
		POST,
		// ...
	}

	private static final Map<HttpMethod, Map<String, Function<HttpExchange, Object>>> HANDLERS = Map.of(
		HttpMethod.GET, Map.of(
			"/status", exchange -> Map.of(
				"players", OnlinePlayers.where().vanished(false).count(),
				"version", Bukkit.getServer().getMinecraftVersion()
			),
			"/votes/sites", exchange -> {
				HashMap<Object, Object> map = new HashMap<>();
				VoteSite.getActiveSites().forEach(site -> map.put(site.getName(), site.getUrl()));
				return map;
			},
			"/staff", exchange -> {
				try {
					return Rank.getStaffNerds().get().values().stream()
						.flatMap(List::stream)
						.toList()
						.stream()
						.filter(Objects::nonNull)
						.map(nerd -> Map.of(
							"uuid", nerd.getUuid(),
							"uuidNoDashes", nerd.getUuid().toString().replaceAll("-", ""),
							"username", nerd.getName(),
							"nickname", nerd.getNickname(),
							"rank", nerd.getRank().name(),
							"about", nerd.getAbout() == null ? "" : nerd.getAbout(),
							"birthday", nerd.getBirthday() == null ? "" : "%s (%d years)".formatted(shortDateFormat(nerd.getBirthday()), nerd.getBirthday().until(LocalDate.now()).getYears()),
							"pronouns", nerd.getPronouns() == null ? "" : String.join(", ", nerd.getPronouns().stream().map(Nerd.Pronoun::toString).toList()),
							"preferredName", nerd.getPreferredName() == null ? "" : nerd.getPreferredName(),
							"promotionDate", nerd.getPromotionDate() == null ? "" : shortDateFormat(nerd.getPromotionDate())
						)).toList();
				} catch (Exception e) {
					Nexus.severe("Error while getting staff list");
					e.printStackTrace();
					return null;
				}
			}
		)
	);

	static class RequestHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) {
			try {
				Nexus.log("[API] " + exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());

				Object response = null;

				for (Map.Entry<HttpMethod, Map<String, Function<HttpExchange, Object>>> entry : HANDLERS.entrySet()) {
					HttpMethod httpMethod = entry.getKey();
					Map<String, Function<HttpExchange, Object>> handler = entry.getValue();
					if (!httpMethod.toString().equals(exchange.getRequestMethod()))
						continue;

					for (Map.Entry<String, Function<HttpExchange, Object>> entry2 : handler.entrySet()) {
						String path = entry2.getKey();
						Function<HttpExchange, Object> handlerFunction = entry2.getValue();
						if (!exchange.getRequestURI().getPath().matches(path))
							continue;

						response = handlerFunction.apply(exchange);
					}
				}

				if (response == null) {
					exchange.sendResponseHeaders(404, 0);
					return;
				}

				var responseString = Utils.getGson().toJson(response);
				Nexus.debug("[API] Response: " + responseString);
				exchange.sendResponseHeaders(200, responseString.getBytes().length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(responseString.getBytes());
				}
			} catch (IOException e) {
				Nexus.severe("Error handling request");
				e.printStackTrace();
			}
		}
	}

}
