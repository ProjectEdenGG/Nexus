package me.pugabyte.bncore.models.cooldown;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("cooldown")
@NoArgsConstructor
@AllArgsConstructor
public class Cooldown extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@NonNull
	private Map<String, LocalDateTime> cooldowns = new HashMap<>();

	public Cooldown(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean exists(String type) {
		return cooldowns.containsKey(type);
	}

	public LocalDateTime get(String type) {
		return cooldowns.getOrDefault(type, null);
	}

	public boolean check(String type) {
		return !exists(type) || cooldowns.get(type).isBefore(LocalDateTime.now());
	}

	public Cooldown create(String type, double ticks) {
		cooldowns.put(type, LocalDateTime.now().plusSeconds((long) ticks / 20));
		return this;
	}

	public void clear(String type) {
		cooldowns.remove(type);
	}

}

