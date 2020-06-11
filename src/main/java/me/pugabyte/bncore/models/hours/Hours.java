package me.pugabyte.bncore.models.hours;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocalDateConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

@Data
@Entity("hours")
@NoArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateConverter.class})
public class Hours extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<LocalDate, Integer> times = new HashMap<>();

	public void increment() {
		increment(1);
	}

	public void increment(int amount) {
		times.put(LocalDate.now(), times.getOrDefault(LocalDate.now(), 0) + amount);
	}

	@ToString.Include
	public int getTotal() {
		return times.values().stream().reduce(0, Integer::sum);
	}

	@ToString.Include
	public int getYearly() {
		return getYearly(Year.now());
	}

	public int getYearly(Year year) {
		return times.entrySet().stream()
				.filter(entry -> entry.getKey().getYear() == year.getValue())
				.mapToInt(Entry::getValue)
				.sum();
	}

	@ToString.Include
	public int getMonthly() {
		LocalDate now = LocalDate.now();
		return getMonthly(Year.now(), now.getMonth());
	}

	public int getMonthly(Year year, Month month) {
		return times.entrySet().stream()
				.filter(entry -> entry.getKey().getYear() == year.getValue() && entry.getKey().getMonth() == month)
				.mapToInt(Entry::getValue)
				.sum();
	}

//	public int getWeekly(Year year, Month month) {
//		return times.entrySet().stream()
//				.filter(entry -> entry.getKey().getYear() == year.getValue() && entry.getKey().getMonth() == month)
//				.mapToInt(Entry::getValue)
//				.sum();
//	}

	@ToString.Include
	public int getDaily() {
		return getDaily(LocalDate.now());
	}

	public int getDaily(@NotNull LocalDate date) {
		return times.get(date);
	}

}
