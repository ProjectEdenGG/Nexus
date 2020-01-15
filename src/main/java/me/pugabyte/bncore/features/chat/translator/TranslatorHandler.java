package me.pugabyte.bncore.features.chat.translator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class TranslatorHandler {
	String apiKey;

	public TranslatorHandler(String apiKey) {
		this.apiKey = apiKey;
	}

	public String translate(String message, Language from, Language to) throws Exception {
		String link = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=%key%&text=%text%&lang=%language%";
		String language;
		if (from != Language.unknown) {
			language = from.toString() + "-" + to.toString();
		} else {
			language = to.toString();
		}
		message = URLEncoder.encode(message, "UTF-8");
		URL url = new URL(link.replace("%language%", language).replace("%key%", apiKey).replace("%text%", message));
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
		httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream(), "UTF-8"));
		String input = reader.readLine();
		reader.close();
		JsonObject object = new JsonParser().parse(input).getAsJsonObject();
		JsonArray array = object.getAsJsonArray("text");
		StringBuilder stringBuilder = new StringBuilder();
		for (JsonElement member : array) {
			stringBuilder.append(member.getAsString());
		}
		return stringBuilder.toString().trim();
	}

	public String getLanguage(String message) throws Exception {
		String link = "https://translate.yandex.net/api/v1.5/tr.json/detect?key=%key%&text=%text%";
		message = URLEncoder.encode(message, "UTF-8");
		URL url = new URL(link.replace("%key%", apiKey).replace("%text%", message));
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
		httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
		String input = reader.readLine();
		reader.close();

		JsonObject object = new JsonParser().parse(input).getAsJsonObject();
		return object.get("lang").getAsString();
	}

}
