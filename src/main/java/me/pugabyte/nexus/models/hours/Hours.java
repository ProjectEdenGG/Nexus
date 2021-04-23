package me.pugabyte.nexus.models.hours;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "hours", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateConverter.class})
public class Hours extends eden.models.hours.Hours implements PlayerOwnedObject {
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

}
