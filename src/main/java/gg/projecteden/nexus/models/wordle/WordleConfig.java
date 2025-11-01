package gg.projecteden.nexus.models.wordle;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static java.time.format.DateTimeFormatter.ISO_DATE;

@Data
@Entity(value = "wordle_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class WordleConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<LocalDate, WordleGameConfig> configs = new ConcurrentHashMap<>();
	private List<String> allowedWords;

	public static final LocalDate EPOCH = LocalDate.of(2021, 6, 19);
	private static final String API = "https://www.nytimes.com/svc/wordle/v2/%s.json";
	private static final String GUESS_LIST = "https://paste.projecteden.gg/raw/poway"; // https://i.imgur.com/OoVneyC.png

	public WordleGameConfig get(LocalDate date) {
		if (isNullOrEmpty(allowedWords))
			allowedWords = HttpUtils.mapJson(List.class, GUESS_LIST);

		return configs.computeIfAbsent(date, $ -> HttpUtils.mapJson(WordleGameConfig.class, API, date.format(ISO_DATE)));
	}

	// Use instead of fetching game config to avoid unnecessary web requests
	public int getDaysSinceLaunch(LocalDate date) {
		return (int) ChronoUnit.DAYS.between(EPOCH, date);
	}

	@Data
	public static class WordleGameConfig {
		int id;
		String solution;
		@SerializedName("print_date")
		LocalDate printDate;
		@SerializedName("days_since_launch")
		int daysSinceLaunch;
		String editor;

	}

}
