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

@Environments(Env.PROD)
public class SimpleHttpServer extends Feature {
	private static final int PORT = 8181;
	private static final Controller CONTROLLER = new Controller();
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
		public void handle(HttpExchange exchange) {
			try {
				String path = exchange.getRequestURI().getPath();
				Nexus.debug("[API] " + exchange.getRequestMethod() + " " + path);

				Object response = null;

				var httpMethod = HttpMethod.valueOf(exchange.getRequestMethod());

				endpoints:
				for (Method method : CONTROLLER.getClass().getDeclaredMethods()) {
					method.setAccessible(true);
					if (!method.isAnnotationPresent(httpMethod.getAnnotation()))
						continue;

					Annotation annotation = method.getAnnotation(httpMethod.getAnnotation());
					String controllerPath = (String) annotation.getClass().getMethod("value").invoke(annotation);

					var requestSplit = path.split("/");
					var controllerSplit = controllerPath.split("/");
					List<String> arguments = new ArrayList<>();

					Nexus.debug("[API] method: " + method.getName());
					Nexus.debug("[API] path: " + path);
					Nexus.debug("[API] controllerPath: " + controllerPath);
					Nexus.debug("[API] requestSplit: " + String.join(", ", requestSplit));
					Nexus.debug("[API] controllerSplit: " + String.join(", ", controllerSplit));

					if (requestSplit.length != controllerSplit.length) {
						Nexus.debug("[API] Argument length mismatch, continuing");
						continue;
					}

					for (int i = 0; i < controllerSplit.length; i++) {
						var requestStep = requestSplit[i];
						var controllerStep = controllerSplit[i];
						Nexus.debug("[API] requestStep: " + requestStep);
						Nexus.debug("[API] controllerStep: " + controllerStep);

						if (controllerStep.startsWith("{") && controllerStep.endsWith("}")) {
							arguments.add(requestStep);
						} else if (!requestStep.equals(controllerStep)) {
							Nexus.debug("[API] Step mismatch, continuing");
							continue endpoints;
						}
					}

					Nexus.debug("[API] Method: " + method.getName());
					Nexus.debug("[API] Arguments: " + arguments);
					response = method.invoke(CONTROLLER, arguments.toArray());
				}

				if (response == null) {
					exchange.sendResponseHeaders(404, 0);
					exchange.close();
					return;
				}

				var responseString = Utils.getGson().toJson(response);
				Nexus.debug("[API] Response: " + responseString);
				exchange.sendResponseHeaders(200, responseString.getBytes().length);
				try (OutputStream os = exchange.getResponseBody()) {
					os.write(responseString.getBytes());
				}
			} catch (Exception e) {
				Nexus.severe("Error handling request");
				e.printStackTrace();
			}
		}
	}

}
