package gg.projecteden.nexus.models.particle;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ColorConverter;
import gg.projecteden.nexus.utils.Tasks;
import lombok.*;
import org.bukkit.Color;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
@Entity(value = "particle_owner", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ColorConverter.class})
public class ParticleOwner implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Getter(AccessLevel.PRIVATE)
	private Map<ParticleType, Map<ParticleSetting, Object>> settings = new ConcurrentHashMap<>();
	@Getter
	private Set<ParticleType> activeParticles = new HashSet<>();

	private boolean showBlockMarkers;

	public Map<ParticleSetting, Object> getSettings(ParticleType particleType) {
		Map<ParticleSetting, Object> map = settings.computeIfAbsent(particleType, $ -> new ConcurrentHashMap<>());

		if (!Nullables.isNullOrEmpty(map))
			deserialize(map);

		return map;
	}

	private transient Set<ParticleTask> tasks = new HashSet<>();

	public List<ParticleTask> getTasks(ParticleType particleType) {
		return tasks.stream().filter(task -> task.getParticleType() == particleType).collect(Collectors.toList());
	}

	public List<ParticleTask> getTasks(int taskId) {
		return tasks.stream().filter(task -> task.getTaskId() == taskId).collect(Collectors.toList());
	}

	public boolean isActive(ParticleType type) {
		return getTasks(type).size() > 0;
	}

	public void cancel() {
		activeParticles.clear();
		save();

		new HashSet<>(tasks).forEach(task -> {
			Tasks.cancel(task.getTaskId());
			tasks.remove(task);
		});
	}

	public void cancel(ParticleType particleType) {
		activeParticles.remove(particleType);
		save();

		getTasks(particleType).forEach(particleTask -> {
			Tasks.cancel(particleTask.getTaskId());
			tasks.remove(particleTask);
		});
	}

	public void cancel(int taskId) {
		Tasks.cancel(taskId);
		getTasks(taskId).forEach(particleTask -> tasks.remove(particleTask));
	}

	public void start(ParticleType type) {
		type.run(this);
	}

	public void start(ParticleType particleType, int... taskIds) {
		activeParticles.add(particleType);
		save();

		addTaskIds(particleType, taskIds);
	}

	public void addTaskIds(ParticleType particleType, int[] taskIds) {
		for (int taskId : taskIds)
			tasks.add(new ParticleTask(particleType, taskId));
	}

	public boolean canUse(ParticleType particleType) {
		return particleType.canBeUsedBy(getOnlinePlayer());
	}

	private void save() {
		new ParticleService().save(this);
	}

	private void deserialize(Map<ParticleSetting, Object> map) {
		new HashMap<>(map).forEach((key, value) -> {
			if (value == null)
				return;

			boolean isRgbMap = Map.class.isAssignableFrom(value.getClass()) && ((Map<?, ?>) value).containsKey("r");
			boolean isEnum = Enum.class.isAssignableFrom(key.getValue()) && value instanceof String;

			if (isRgbMap) {
				Map<String, Integer> color = (Map<String, Integer>) value;
				map.put(key, Color.fromRGB(color.get("r"), color.get("g"), color.get("b")));
			}

			if (isEnum)
				map.put(key, EnumUtils.valueOf(key.getValue(), (String) value));
		});
	}

}
