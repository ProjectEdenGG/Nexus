package me.pugabyte.nexus.features.damagetracker;

import me.pugabyte.nexus.features.damagetracker.models.DamageEvent;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.entity.LivingEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DamageTracker {
	private static Map<String, List<DamageEvent>> history = new HashMap<>();

	public DamageTracker() {
		new DamageTrackerListener();
		Tasks.repeat(Time.MINUTE, Time.MINUTE, this::janitor);
	}

	public static void log(DamageEvent event) {
		String uuid = event.getEntity().getUniqueId().toString();
		List<DamageEvent> events = get(event.getEntity());
		if (!event.existsIn(events)) {
			events.add(event);
			history.put(uuid, events);
		}
	}

	private void janitor() {
		Map<String, List<DamageEvent>> newHistory = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();

		Iterator<Map.Entry<String, List<DamageEvent>>> iterator = history.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, List<DamageEvent>> pair = iterator.next();
			String uuid = pair.getKey();

			List<DamageEvent> events = pair.getValue();
			List<DamageEvent> newEvents = new ArrayList<>(events);

			for (DamageEvent event : events) {
				LocalDateTime then = event.getTime();
				if (ChronoUnit.SECONDS.between(then, now) < (60 * 5)) {
					newEvents.remove(event);
				}

				newHistory.put(uuid, newEvents);
			}

			iterator.remove();
		}

		history = newHistory;
	}

	public static List<DamageEvent> get(LivingEntity entity) {
		String uuid = entity.getUniqueId().toString();
		List<DamageEvent> events;
		if (history.containsKey(uuid)) {
			events = history.get(uuid);
		} else {
			events = new ArrayList<>();
		}
		return events;
	}

	public static List<DamageEvent> get(LivingEntity entity, long ticks) {
		List<DamageEvent> events = get(entity).stream()
				.filter(event -> {
					LocalDateTime now = LocalDateTime.now();
					LocalDateTime then = event.getTime();
					double seconds = ticks / 20;
					return ChronoUnit.SECONDS.between(then, now) < seconds;
				})
				.collect(Collectors.toList());
		return events;
	}

}
