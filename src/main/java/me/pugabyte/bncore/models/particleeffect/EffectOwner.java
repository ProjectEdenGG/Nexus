package me.pugabyte.bncore.models.particleeffect;

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
public class EffectOwner extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<EffectType, Map<EffectSetting, Object>> settings = new HashMap<>();

	public Map<EffectSetting, Object> getSettings(EffectType effectType) {
		return settings.get(effectType);
	}

	@Transient
	private Set<EffectTask> tasks = new HashSet<>();

	public List<EffectTask> getTasks(EffectType effectType) {
		return tasks.stream().filter(task -> task.getEffectType() == effectType).collect(Collectors.toList());
	}

	public List<EffectTask> getTasks(int taskId) {
		return tasks.stream().filter(task -> task.getTaskId() == taskId).collect(Collectors.toList());
	}

	public void cancelTasks() {
		new HashSet<>(tasks).forEach(task -> {
			Tasks.cancel(task.getTaskId());
			tasks.remove(task);
		});
	}

	public void cancelTasks(EffectType effectType) {
		getTasks(effectType).forEach(effectTask -> {
			Tasks.cancel(effectTask.getTaskId());
			tasks.remove(effectTask);
		});
	}

	public void cancelTasks(int taskId) {
		getTasks(taskId).forEach(effectTask -> {
			Tasks.cancel(effectTask.getTaskId());
			tasks.remove(effectTask);
		});
	}

	public void addTasks(EffectType effectType, int... taskIds) {
		for (int taskId : taskIds)
			tasks.add(new EffectTask(effectType, taskId));
	}

}
