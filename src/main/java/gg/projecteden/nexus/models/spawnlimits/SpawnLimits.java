package gg.projecteden.nexus.models.spawnlimits;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.WorldConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Data
@Entity(value = "spawn_limits", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, WorldConverter.class})
public class SpawnLimits implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<World, Map<SpawnLimitType, Integer>> settings = new HashMap<>();

	public Map<SpawnLimitType, Integer> get(World world) {
		return settings.computeIfAbsent(world, $ -> new HashMap<>());
	}

	@Getter
	@AllArgsConstructor
	public enum SpawnLimitType {
		AMBIENT(Bukkit.getAmbientSpawnLimit(), World::getAmbientSpawnLimit, World::setAmbientSpawnLimit),
		ANIMALS(Bukkit.getAnimalSpawnLimit(), World::getAnimalSpawnLimit, World::setAnimalSpawnLimit),
		MONSTERS(Bukkit.getMonsterSpawnLimit(), World::getMonsterSpawnLimit, World::setMonsterSpawnLimit),
		WATER_AMBIENT(Bukkit.getWaterAmbientSpawnLimit(), World::getWaterAmbientSpawnLimit, World::setWaterAmbientSpawnLimit),
		WATER_ANIMALS(Bukkit.getWaterAnimalSpawnLimit(), World::getWaterAnimalSpawnLimit, World::setWaterAnimalSpawnLimit),
		;

		private final int defaultValue;
		private final Function<World, Integer> getter;
		private final BiConsumer<World, Integer> setter;

		public void set(World world, int value) {
			setter.accept(world, value);
		}

		public int get(World world) {
			return getter.apply(world);
		}

		public boolean isDefaultFor(World world) {
			return defaultValue == get(world);
		}

		public void reset(World world) {
			set(world, getDefaultValue());
		}
	}

}
