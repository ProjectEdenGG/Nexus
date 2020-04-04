package me.pugabyte.bncore.models.particle;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.Tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity("effect_owner")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class ParticleOwner extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<ParticleType, Map<ParticleSetting, Object>> settings = new HashMap<>();

	public Map<ParticleSetting, Object> getSettings(ParticleType particleType) {
		return settings.get(particleType);
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
		for (int taskId : taskIds)
			tasks.add(new ParticleTask(particleType, taskId));
	}

}
