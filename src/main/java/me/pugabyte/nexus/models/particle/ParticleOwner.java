package me.pugabyte.nexus.models.particle;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ColorConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.EnumUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Color;

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
public class ParticleOwner implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Getter(AccessLevel.PRIVATE)
	private Map<ParticleType, Map<ParticleSetting, Object>> settings = new HashMap<>();
	@Getter
	private Set<ParticleType> activeParticles = new HashSet<>();

	public Map<ParticleSetting, Object> getSettings(ParticleType particleType) {
		if (!settings.containsKey(particleType) || settings.get(particleType) == null)
			settings.put(particleType, new HashMap<>());
		Map<ParticleSetting, Object> map = settings.get(particleType);

		if (map != null && !map.isEmpty())
			// Do some deserialization if necessary
			new HashMap<>(map).forEach((key, value) -> {
				if (value == null) return;
				if (Map.class.isAssignableFrom(value.getClass()) && ((Map<?, ?>) value).containsKey("r")) {
					Map<String, Integer> color = (Map<String, Integer>) value;
					map.put(key, Color.fromRGB(color.get("r"), color.get("g"), color.get("b")));
				} else if (Enum.class.isAssignableFrom(key.getValue()) && value instanceof String) {
					map.put(key, EnumUtils.valueOf(key.getValue(), (String) value));
				}
			});
		return map;
	}

	private transient Set<ParticleTask> tasks = new HashSet<>();

	public List<ParticleTask> getTasks(ParticleType particleType) {
		return tasks.stream().filter(task -> task.getParticleType() == particleType).collect(Collectors.toList());
	}

	public List<ParticleTask> getTasks(int taskId) {
		return tasks.stream().filter(task -> task.getTaskId() == taskId).collect(Collectors.toList());
	}

	public void cancelTasks() {
		activeParticles.clear();
		new ParticleService().save(this);
		new HashSet<>(tasks).forEach(task -> {
			Tasks.cancel(task.getTaskId());
			tasks.remove(task);
		});
	}

	public void cancelTasks(ParticleType particleType) {
		activeParticles.remove(particleType);
		new ParticleService().save(this);
		getTasks(particleType).forEach(particleTask -> {
			Tasks.cancel(particleTask.getTaskId());
			tasks.remove(particleTask);
		});
	}

	public void cancelTasks(int taskId) {
		Tasks.cancel(taskId);
		getTasks(taskId).forEach(particleTask -> tasks.remove(particleTask));
	}

	public void addTasks(ParticleType particleType, int... taskIds) {
		activeParticles.add(particleType);
		new ParticleService().save(this);
		for (int taskId : taskIds)
			tasks.add(new ParticleTask(particleType, taskId));
	}

}
