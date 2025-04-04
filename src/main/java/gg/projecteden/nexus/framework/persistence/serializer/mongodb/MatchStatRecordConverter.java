package gg.projecteden.nexus.framework.persistence.serializer.mongodb;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.Mapper;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsUser.MatchStatRecord;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchStatRecordConverter extends TypeConverter implements SimpleValueConverter {

	private Mapper mapper;

	public MatchStatRecordConverter(Mapper mapper) {
		super(MatchStatRecord.class);
		this.mapper = mapper;
	}

	@Override
	public Object encode(Object value, MappedField optionalExtraInfo) {
		if (value == null) return null;
		if (value instanceof MatchStatRecord matchStatRecord) {
			Map<String, Object> db = new HashMap<>();
			db.put("mechanic", matchStatRecord.getMechanic().name());
			db.put("date", LocalDateTimeConverter.toEncoded(matchStatRecord.getDate()));
			db.put("stats", new HashMap<>() {{
				for (Map.Entry<MinigameStatistic, Integer> entry : matchStatRecord.getStats().entrySet())
					put(entry.getKey().getId(), entry.getValue());
			}});
			return db;
		}

		throw new RuntimeException("Unknown class for MatchStatRecord: " + value.getClass().getSimpleName());
	}

	@Override
	public Object decode(Class<?> aClass, Object o, MappedField mappedField) {
		if (o == null) return null;
		if (o instanceof Map map) {
			MechanicType type = MechanicType.valueOf((String) map.get("mechanic"));
			LocalDateTime date = LocalDateTimeConverter.decode(map.get("date"));
			Map<MinigameStatistic, Integer> stats = new HashMap<>();
			Map<String, Integer> encodedStats = (Map<String, Integer>) map.get("stats");

			List<MinigameStatistic> statsList = type.getStatistics();
			for (String key : encodedStats.keySet()) {
				MinigameStatistic stat = statsList.stream().filter(s -> s.getId().equals(key)).findFirst().orElse(null);
				if (stat != null)
					stats.put(stat, Integer.parseInt(encodedStats.get(key).toString()));
			}

			MatchStatRecord record = new MatchStatRecord(type, stats);
			record.setDate(date);
			return record;
		}
		return null;
	}
}
