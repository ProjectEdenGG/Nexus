package gg.projecteden.nexus.models.structure;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.block.CreatureSpawner;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(Structure.class)
public class StructureService extends MongoBukkitService<Structure> {
	private final static Map<UUID, Structure> cache = new ConcurrentHashMap<>();

	public Map<UUID, Structure> getCache() {
		return cache;
	}

	public @Nullable Structure get(Location minPoint) {
		return getAll().stream().filter(_structure -> _structure.getMinPoint().equals(minPoint)).findFirst().orElse(null);
	}

	public @NonNull Structure getOrCreate(Location minPoint) {
		Structure structure = get(minPoint);
		if (structure == null) {
			structure = new Structure(UUIDUtils.UUID0, minPoint);
			save(structure);
		}

		return structure;
	}

	public boolean existsAt(Location minPoint) {
		return get(minPoint) != null;
	}


	public @Nullable Structure getFrom(CreatureSpawner spawner) {
		for (Structure structure : getAll()) {
			if (structure.getSpawner(spawner) != null)
				return structure;
		}

		return null;
	}
}
