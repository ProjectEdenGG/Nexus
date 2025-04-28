package gg.projecteden.nexus.features.minigames.models;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService.LeaderboardRanking;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsUser.MatchStatRecord;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class MatchStatistics {
	protected MechanicType mechanic;
	@ToString.Exclude
	protected Match match;
	protected Arena arena;

	private Map<UUID, Map<MinigameStatistic, Integer>> statistics = new HashMap<>();

	public MatchStatistics(MechanicType mechanic, Match match) {
		this.mechanic = mechanic;
		this.match = match;
		if (this.match != null)
			this.arena = match.getArena();
	}

	public void award(MinigameStatistic statistic, HasUniqueId uuid) {
		this.award(statistic, uuid, 1);
	}

	public void award(MinigameStatistic statistic, HasUniqueId uuid, int amount) {
		if (!this.applies(statistic)) {
			Minigames.debug(match.getMechanic().getId() + " is not setup to track stat: " + statistic.getId());
			return;
		}

		Map<MinigameStatistic, Integer> userStats = this.statistics.getOrDefault(uuid.getUniqueId(), new HashMap<>());
		if (userStats.containsKey(statistic))
			amount += userStats.get(statistic);
		userStats.put(statistic, amount);

		this.statistics.put(uuid.getUniqueId(), userStats);
	}

	public void set(MinigameStatistic statistic, HasUniqueId uuid, int value) {
		if (!this.applies(statistic)) {
			Minigames.debug(match.getMechanic().getId() + " is not setup to track stat: " + statistic.getId());
			return;
		}

		Map<MinigameStatistic, Integer> userStats = this.statistics.getOrDefault(uuid.getUniqueId(), new HashMap<>());
		userStats.put(statistic, value);

		this.statistics.put(uuid.getUniqueId(), userStats);
	}

	public void report(MechanicType type) {
		MinigameStatsService service = new MinigameStatsService();
		for (Minigamer minigamer : getMatch().getAllMinigamers()) {
			MatchStatRecord record = new MatchStatRecord(type, this.statistics.get(minigamer.getUniqueId()));
			service.edit(minigamer, user -> user.addRecord(record));

			List<String> lines = new ArrayList<>();
			this.statistics.get(minigamer.getUniqueId()).forEach((stat, value) -> {
				if (stat.equals(TIME_PLAYED))
					lines.add("&3" + stat.getTitle() + ": &e" + Timespan.ofSeconds(value).format());
				else
					lines.add("&3" + stat.getTitle() + ": &e" + value);
			});
			minigamer.sendMessage(new JsonBuilder(Minigames.PREFIX + "&eHover to see your stats").hover(lines));
		}
	}

	@SneakyThrows
	public List<MinigameStatistic> getStatistics() {
		List<MinigameStatistic> statistics = new ArrayList<>();
		for (Class<?> clazz : ReflectionUtils.superclassesOf(this.getClass())) {
			for (Field field : clazz.getDeclaredFields()) {
				Minigames.debug("Checking class: " + field.getDeclaringClass().getName());
				if (field.getType() == MinigameStatistic.class)
					statistics.add((MinigameStatistic) field.get(null));
			}

			for (Class<?> iface : clazz.getInterfaces()) {
				for (Field field : iface.getDeclaredFields()) {
					if (field.getType() == MinigameStatistic.class)
						statistics.add((MinigameStatistic) field.get(null));
				}
			}
		}

		return statistics;
	}

	public boolean applies(MinigameStatistic statistic) {
		return getStatistics().stream()
			.anyMatch(stat -> stat.equals(statistic));
	}

	public String aggregate(MinigameStatistic statistic, LocalDateTime after, UUID self) {
		return null;
	}

	public List<LeaderboardRanking> getLeaderboard(MinigameStatistic statistic, LocalDateTime after) {
		return null;
	}

	public static final MinigameStatistic WINS = new MinigameStatistic("wins", "Wins");
	public static final MinigameStatistic TIME_PLAYED = new MinigameStatistic("time_played", "Time Played") {
		@Override
		public Object format(double score) {
			return Timespan.ofSeconds((long) score).format();
		}
	};
	public static final MinigameStatistic GAMES_PLAYED = new MinigameStatistic("games_played", "Games Played") {
		@Override
		public List<Bson> getPipeline(String afterDate, MechanicType mechanic, UUID self, boolean aggregate) {
			List<Bson> filters = new ArrayList<>() {{
				add(Filters.gt("statistics.date", afterDate));
				if (mechanic != null)
					add(Filters.eq("statistics.mechanic", mechanic.name()));
				if (self != null)
					add(Filters.eq("_id", self.toString()));
			}};

			List<Bson> pipeline = new ArrayList<>() {{
				add(Aggregates.unwind("$statistics"));
				add(Aggregates.match(Filters.and(filters)));
				add(Aggregates.group("$_id", Accumulators.sum("total", 1)));
			}};
			if (!aggregate)
				pipeline.add(Aggregates.sort(Sorts.descending("total")));

			return pipeline;
		}
	};
}
