package me.pugabyte.bncore.models.particle;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ColorConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity("particle_owner")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ColorConverter.class})
public class ParticleOwner extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Getter(AccessLevel.PRIVATE)
	private Map<ParticleType, Map<ParticleSetting, Object>> settings = new HashMap<>();
	@Getter
	private List<ParticleType> activeParticles = new ArrayList<>();

	public Map<ParticleSetting, Object> getSettings(ParticleType particleType) {
		if (!settings.containsKey(particleType))
			settings.put(particleType, new HashMap<>());
		Map<ParticleSetting, Object> map = settings.get(particleType);

		// Do some deserialization if necessary
		new HashMap<>(map).forEach((key, value) -> {
			if (Map.class.isAssignableFrom(value.getClass()) && ((Map<?, ?>) value).containsKey("r")) {
				Map<String, Integer> color = (Map<String, Integer>) value;
				map.put(key, Color.fromRGB(color.get("r"), color.get("g"), color.get("b")));
			}
		});
		return map;
	}

	@Transient
	private Set<ParticleTask> tasks = new HashSet<>();

	public List<ParticleTask> getTasks(ParticleType particleType) {
		return tasks.stream().filter(task -> task.getParticleType() == particleType).collect(Collectors.toList());
	}

	public List<ParticleTask> getTasks(int taskId) {
		return tasks.stream().filter(task -> task.getTaskId() == taskId).collect(Collectors.toList());
	}

	public void cancelTasks() {
		new HashSet<>(tasks).forEach(task -> {
			Tasks.cancel(task.getTaskId());
			tasks.remove(task);
		});
	}

	public void cancelTasks(ParticleType particleType) {
		getTasks(particleType).forEach(particleTask -> {
			Tasks.cancel(particleTask.getTaskId());
			tasks.remove(particleTask);
		});
	}

	public void cancelTasks(int taskId) {
		getTasks(taskId).forEach(particleTask -> {
			Tasks.cancel(particleTask.getTaskId());
			tasks.remove(particleTask);
		});
	}

	public void addTasks(ParticleType particleType, int... taskIds) {
		activeParticles.add(particleType);
		for (int taskId : taskIds)
			tasks.add(new ParticleTask(particleType, taskId));
	}

}
