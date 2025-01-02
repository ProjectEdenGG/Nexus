package gg.projecteden.nexus.features.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.voter.VoteSite;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
				"players", OnlinePlayers.getAll().size(),
				"version", Bukkit.getServer().getMinecraftVersion()
			),
			"/votes/sites", exchange -> {
				HashMap<Object, Object> map = new HashMap<>();
				VoteSite.getActiveSites().forEach(site -> map.put(site.getName(), site.getUrl()));
				return map;
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
				Nexus.log("[API] Response: " + responseString);
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
