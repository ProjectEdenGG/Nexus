package gg.projecteden.nexus.models.parkour;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LobbyParkourCourse.class)
public class LobbyParkourCourseService extends MongoBukkitService<LobbyParkourCourse> {
	private final static Map<UUID, LobbyParkourCourse> cache = new ConcurrentHashMap<>();

	public Map<UUID, LobbyParkourCourse> getCache() {
		return cache;
	}

}
