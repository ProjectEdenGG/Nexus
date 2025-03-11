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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

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
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				String path = exchange.getRequestURI().getPath();
				Debug.log(API, exchange.getRequestMethod() + " " + path);

				Object response = null;

				var httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());
				Debug.log(API, "httpMethod: " + httpMethod.name() + " " + httpMethod.getAnnotation().getSimpleName());

				endpoints:
				for (Method method : CONTROLLER.getClass().getDeclaredMethods()) {
					method.setAccessible(true);
					Debug.log(API, "method: " + method.getName());
					if (!method.isAnnotationPresent(httpMethod.getAnnotation()))
						continue;

					Annotation annotation = method.getAnnotation(httpMethod.getAnnotation());
					String controllerPath = (String) annotation.getClass().getMethod("value").invoke(annotation);

					var requestSplit = path.split("/");
					var controllerSplit = controllerPath.split("/");
					List<Object> arguments = new ArrayList<>();

					Debug.log(API, "path: " + path);
					Debug.log(API, "controllerPath: " + controllerPath);
					Debug.log(API, "requestSplit: " + String.join(", ", requestSplit));
					Debug.log(API, "controllerSplit: " + String.join(", ", controllerSplit));

					if (requestSplit.length != controllerSplit.length) {
						Debug.log(API, "Argument length mismatch, continuing");
						continue;
					}

					for (int i = 0; i < controllerSplit.length; i++) {
						var requestStep = requestSplit[i];
						var controllerStep = controllerSplit[i];
						Debug.log(API, "requestStep: " + requestStep);
						Debug.log(API, "controllerStep: " + controllerStep);

						if (controllerStep.startsWith("{") && controllerStep.endsWith("}")) {
							arguments.add(requestStep);
						} else if (!requestStep.equals(controllerStep)) {
							Debug.log(API, "Step mismatch, continuing");
							continue endpoints;
						}
					}

					var params = method.getParameterTypes();
					if (params.length > 0) {
						var last = params[params.length - 1];
						if (HttpExchange.class.equals(last))
							arguments.add(exchange);
					}

					Debug.log(API, "Method: " + method.getName());
					Debug.log(API, "Arguments: " + arguments);
					response = method.invoke(CONTROLLER, arguments.toArray());
				}

				if (response == null) {
					exchange.sendResponseHeaders(404, 0);
					exchange.close();
					return;
				}

				var responseString = Utils.getGson().toJson(response);
				Debug.log(API, "Response: " + responseString);
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
