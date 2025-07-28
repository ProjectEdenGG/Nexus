package gg.projecteden.nexus.utils;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.utils.SerializationUtils.Json;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class HttpUtils {

	@Getter
	private static final OkHttpClient client = new OkHttpClient();

	public static String formatParameters(Map<String, String> parameters) {
		return parameters.entrySet().stream()
				.map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
				.reduce((p1, p2) -> p1 + "&" + p2)
				.orElse("");
	}

	@NotNull
	public static String unescapeHtml(String html) {
		return StringEscapeUtils.unescapeHtml(html).replaceAll("&apos;", "'"); // it doesnt know what &apos; is??
	}

	public static String encode(String parameter) {
		return URLEncoder.encode(parameter, StandardCharsets.UTF_8);
	}

	private static Object[] encode(Object[] objects) {
		return Arrays.stream(objects)
				.map(parameter -> {
					if (parameter == null)
						return "null";
					if (Utils.isPrimitiveNumber(parameter.getClass()))
						return parameter;
					return encode(parameter.toString());
				})
				.toArray(Object[]::new);
	}

	public static Builder createRequest(String url, Object... objects) {
		return new Request.Builder().url(String.format(url, encode(objects)));
	}

	@SneakyThrows
	public static File downloadFile(String url, String destination) {
		try (Response response = callUrl(url)) {
			if (response.body() == null)
				throw new NexusException("Response body is null");

			return saveFile(response.body(), destination);
		}
	}

	public static File saveFile(String url, String destination) {
		return saveFile(callUrl(url).body(), IOUtils.getPluginFile(destination));
	}

	public static File saveFile(ResponseBody body, String destination) {
		return saveFile(body, IOUtils.getPluginFile(destination));
	}

	@SneakyThrows
	public static File saveFile(ResponseBody body, File destination) {
		try (BufferedSink sink = Okio.buffer(Okio.sink(destination))) {
			sink.writeAll(body.source());
		}
		return destination;
	}

	@SneakyThrows
	public static Response callUrl(String url, Object... objects) {
		final Request.Builder request = createRequest(url, objects);
		return client.newCall(request.build()).execute();
	}

	public static void addUserAgent(Builder request) {
		request.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:60.0) Gecko/20100101 Firefox/81.0");
	}

	@SneakyThrows
	public static <T> T mapJson(Class<T> clazz, String url, Object... objects) {
		try (Response response = callUrl(url, objects)) {
			return mapJson(clazz, response);
		}
	}

	@SneakyThrows
	private static <T> T mapJson(Class<T> clazz, Response response) {
		return Utils.getGson().fromJson(response.body().string(), clazz);
	}

	public static String post(String url, Map<String, String> headers, Map<String, Object> body) {
		return post(url, Headers.of(headers), Json.of(body));
	}

	@SneakyThrows
	public static String post(String url, Headers headers, String body) {
		final Request request = createRequest(url)
			.headers(headers)
			.post(json(body))
			.build();

		try (Response response = client.newCall(request).execute()) {
			final ResponseBody responseBody = response.body();
			if (!response.isSuccessful()) {
				String message = response.code() + " " + response.message();
				if (responseBody != null)
					message += ": " + responseBody.string();

				throw new EdenException(message);
			}

			return responseBody == null ? null : responseBody.string();
		}
	}

	@SneakyThrows
	public static String get(String url) {
		final Request request = createRequest(url)
			.get()
			.build();

		try (Response response = client.newCall(request).execute()) {
			final ResponseBody responseBody = response.body();
			if (!response.isSuccessful()) {
				String message = response.code() + " " + response.message();
				if (responseBody != null)
					message += ": " + responseBody.string();

				throw new EdenException(message);
			}

			return responseBody == null ? null : responseBody.string();
		}
	}

	@NotNull
	private static RequestBody json(String body) {
		return RequestBody.create(MediaType.parse("application/json"), body);
	}

}
