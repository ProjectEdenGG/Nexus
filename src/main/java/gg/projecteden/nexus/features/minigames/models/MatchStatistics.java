package gg.projecteden.nexus.features.minigames.models;

import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.features.minigames.models.statistics.models.MinigameStatistic;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsService;
import gg.projecteden.nexus.models.minigamestats.MinigameStatsUser.MatchStatRecord;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class MatchStatistics {
	@ToString.Exclude
	protected Match match;
	protected Arena arena;

	private Map<UUID, Map<MinigameStatistic, Integer>> statistics = new HashMap<>();

	public MatchStatistics(Match match) {
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

	public static final MinigameStatistic WINS = new MinigameStatistic("wins", "Wins");
	public static final MinigameStatistic TIME_PLAYED = new MinigameStatistic("time_played", "Time Played") {
		@Override
		public Object format(long score) {
			return Timespan.ofSeconds(score).format();
		}
	};
}
