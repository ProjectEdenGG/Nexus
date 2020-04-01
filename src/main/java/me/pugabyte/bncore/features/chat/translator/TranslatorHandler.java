package me.pugabyte.bncore.features.chat.translator;

import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;

public class TranslatorHandler {
	private static String apiUrl = "https://translate.yandex.net/api/v1.5/tr.json/";
	String apiKey;

	public TranslatorHandler(String apiKey) {
		this.apiKey = apiKey;
	}

	@SneakyThrows
	public Language detect(String message) {
		String link = Detection.getUrl()
				.replace("{{key}}", apiKey)
				.replace("{{text}}", URLEncoder.encode(message, "UTF-8"));

		Request request = new Request.Builder().url(link).build();

		try (Response response = new OkHttpClient().newCall(request).execute()) {
			Detection.Response result = new Gson().fromJson(response.body().string(), Detection.Response.class);
			return Language.valueOf(result.getLang().toUpperCase());
		}
	}

	@SneakyThrows
	public String translate(String message, Language from, Language to) {
		String language = (from == Language.UNKNOWN) ? to.toString() : from.toString() + "-" + to.toString();

		String link = Translation.getUrl()
				.replace("{{key}}", apiKey)
				.replace("{{text}}", URLEncoder.encode(message, "UTF-8"))
				.replace("{{language}}", language.toLowerCase());

		Request request = new Request.Builder().url(link).build();

		try (Response response = new OkHttpClient().newCall(request).execute()) {
			Translation.Response result = new Gson().fromJson(response.body().string(), Translation.Response.class);
			return String.join("", result.getText()).trim();
		}
	}

	private static class Detection {
		@Getter
		private static String url = apiUrl + "detect?key={{key}}&text={{text}}";

		@Data
		private static class Response {
			private int code;
			private String lang;
		}
	}

	private static class Translation {
		@Getter
		private static String url = apiUrl + "translate?key={{key}}&text={{text}}&lang={{language}}";

		@Data
		private static class Response {
			private int code;
			private String lang;
			private String[] text;
		}
	}

}
