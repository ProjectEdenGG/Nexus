package gg.projecteden.nexus.features.chat.translator;

import gg.projecteden.nexus.utils.HttpUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor
public class TranslatorHandler {
	private static final String API_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

	@SneakyThrows
	public Language detect(String message) {
		Detection.Response response = HttpUtils.mapJson(Detection.Response.class, Detection.getURL(), Translator.getApiKey(), message);
		return response.getLang() != null ? Language.valueOf(response.getLang().toUpperCase()) : null;
	}

	@SneakyThrows
	public String translate(String message, Language from, Language to) {
		String language = (from == Language.UNKNOWN) ? to.toString() : from.toString() + "-" + to.toString();
		Translation.Response response = HttpUtils.mapJson(Translation.Response.class, Detection.getURL(), Translator.getApiKey(), message, language.toLowerCase());
		return String.join("", response.getText()).trim();
	}

	private static class Detection {
		@Getter
		private static final String URL = API_URL + "detect?key=%s&text=%s";

		@Data
		private static class Response {
			private int code;
			private String lang;
		}
	}

	private static class Translation {
		@Getter
		private static final String URL = API_URL + "translate?key=%s&text=%s&lang=%s";

		@Data
		private static class Response {
			private int code;
			private String lang;
			private String[] text;
		}
	}

}
