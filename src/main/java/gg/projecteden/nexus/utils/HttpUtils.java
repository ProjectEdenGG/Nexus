package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
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
		return saveFile(callUrl(url).body(), Nexus.getFile(destination));
	}

	public static File saveFile(ResponseBody body, String destination) {
		return saveFile(body, Nexus.getFile(destination));
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
		return client.newCall(createRequest(url, objects).build()).execute();
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

}
