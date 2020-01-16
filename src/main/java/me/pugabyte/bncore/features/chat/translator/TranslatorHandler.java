package me.pugabyte.bncore.features.chat.translator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TranslatorHandler {
	String apiKey;

	public TranslatorHandler(String apiKey) {
		this.apiKey = apiKey;
	}

	@SneakyThrows
	public String translate(String message, Language from, Language to) {
		String link = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%key%&text=%text%&lang=%language%";

		String language = (from == Language.UNKNOWN) ? to.toString() : from.toString() + "-" + to.toString();

		URL url = new URL(link
				.replace("%language%", language.toLowerCase())
				.replace("%key%", apiKey)
				.replace("%text%", URLEncoder.encode(message, "UTF-8")));

		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream(), StandardCharsets.UTF_8));
		String input = reader.readLine();
		reader.close();

		JsonArray array = new JsonParser().parse(input).getAsJsonObject().getAsJsonArray("text");
		StringBuilder stringBuilder = new StringBuilder();
		for (JsonElement member : array)
			stringBuilder.append(member.getAsString());

		return stringBuilder.toString().trim();
	}

	@SneakyThrows
	public Language getLanguage(String message) {
		String link = "https://translate.yandex.net/api/v1.5/tr.json/detect?key=%key%&text=%text%";

		URL url = new URL(link
				.replace("%key%", apiKey)
				.replace("%text%", URLEncoder.encode(message, "UTF-8")));

		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
		String input = reader.readLine();
		reader.close();

		JsonObject object = new JsonParser().parse(input).getAsJsonObject();
		return Language.valueOf(object.get("lang").getAsString().toUpperCase());
	}

}
