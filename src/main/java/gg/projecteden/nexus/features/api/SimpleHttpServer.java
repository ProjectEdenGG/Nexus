package gg.projecteden.nexus.features.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.api.annotations.Get;
import gg.projecteden.nexus.features.api.annotations.Post;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.nexus.utils.Debug.DebugType.API;

@Environments(Env.PROD)
public class SimpleHttpServer extends Feature {
	private static final int PORT = 8181;
	private static final Controller CONTROLLER = new Controller();
	private static HttpServer server;

	@Override
	public void onStart() {
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 50);
			server.createContext("/", new RequestHandler());
			server.setExecutor(Executors.newFixedThreadPool(10));
			server.start();
			Nexus.log("HTTP server listening on port " + PORT);

			Tasks.async(BlockPartyWebSocketServer::start);
			Nexus.log("Websocket server listening on port " + BlockPartyWebSocketServer.PORT);
		} catch (IOException e) {
			Nexus.severe("Error starting HTTP server");
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
		server.stop(0);
		BlockPartyWebSocketServer.stop();
	}

	@Getter
	@RequiredArgsConstructor
	public enum HttpMethod {
		GET(Get.class),
		POST(Post.class),
		// ...
		;

		private final Class<? extends Annotation> annotation;
	}

	static class RequestHandler implements HttpHandler {

		private static void debug(HttpExchange exchange, String message) {
			if (exchange.getRequestURI().getPath().contains("/live/players.json"))
				return;

			Debug.log(API, message);
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				String path = exchange.getRequestURI().getPath();
				debug(exchange, "======");
				debug(exchange, exchange.getRequestMethod() + " " + path);

				Object response = null;

				var httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
				debug(exchange, "httpMethod: " + httpMethod.name() + " " + httpMethod.getAnnotation().getSimpleName());

				JSONObject body = null;
				try (InputStream inputStream = exchange.getRequestBody()) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
					StringBuilder builder = new StringBuilder();

					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}

					var bodyString = builder.toString();
					debug(exchange, "Request body: " + bodyString);
					if (isNotNullOrEmpty(bodyString)) {
						try {
							body = new JSONObject(bodyString);
							debug(exchange, "Request body JSON: " + body);
						} catch (JSONException ignore) {
							ignore.printStackTrace();
						}
					}
				}

				endpoints:
				for (Method method : CONTROLLER.getClass().getDeclaredMethods()) {
					method.setAccessible(true);
					debug(exchange, "method: " + method.getName());
					if (!method.isAnnotationPresent(httpMethod.getAnnotation()))
						continue;

					Annotation annotation = method.getAnnotation(httpMethod.getAnnotation());
					String controllerPath = (String) annotation.getClass().getMethod("value").invoke(annotation);

					String[] requestSplit = path.split("/");
					String[] controllerSplit = controllerPath.split("/");
					List<Object> arguments = new ArrayList<>();

					debug(exchange, "path: " + path);
					debug(exchange, "controllerPath: " + controllerPath);
					debug(exchange, "requestSplit: " + String.join(", ", requestSplit));
					debug(exchange, "controllerSplit: " + String.join(", ", controllerSplit));

					if (requestSplit.length != controllerSplit.length) {
						debug(exchange, "Argument length mismatch, continuing");
						continue;
					}

					for (int i = 0; i < controllerSplit.length; i++) {
						String requestStep = requestSplit[i];
						String controllerStep = controllerSplit[i];
						debug(exchange, "requestStep: " + requestStep);
						debug(exchange, "controllerStep: " + controllerStep);

						if (controllerStep.startsWith("{") && controllerStep.endsWith("}")) {
							arguments.add(requestStep);
						} else if (!requestStep.equals(controllerStep)) {
							debug(exchange, "Step mismatch, continuing");
							continue endpoints;
						}
					}

					for (int i = 0; i < method.getParameterTypes().length; i++) {
						debug(exchange, "method.getParameterTypes()[i]: " + method.getParameterTypes()[i].getSimpleName());
						if (method.getParameterTypes()[i].equals(HttpExchange.class))
							arguments.add(i, exchange);

						if (method.getParameterTypes()[i].equals(JSONObject.class))
							arguments.add(i, body);
					}

					debug(exchange, "Method: " + method.getName());
					debug(exchange, "Arguments: " + arguments);
					response = method.invoke(CONTROLLER, arguments.toArray());
					break;
				}

				if (response == null) {
					exchange.sendResponseHeaders(404, 0);
					exchange.close();
					return;
				}

				var responseString = Utils.getGson().toJson(response);
				debug(exchange, "Response: " + responseString);
				exchange.sendResponseHeaders(200, responseString.getBytes().length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(responseString.getBytes());
				}
			} catch (Exception e) {
				Nexus.severe("Error handling request");
				e.printStackTrace();
				var message = e.getMessage();
				exchange.sendResponseHeaders(500, message.getBytes().length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(message.getBytes());
				}
			} finally {
				exchange.close();
			}
		}

	}

}
