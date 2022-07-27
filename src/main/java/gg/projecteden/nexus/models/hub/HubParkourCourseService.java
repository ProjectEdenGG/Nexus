package gg.projecteden.nexus.models.hub;

import gg.projecteden.api.mongodb.MongoService;
import gg.projecteden.api.mongodb.annotations.ObjectClass;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(HubParkourCourse.class)
public class HubParkourCourseService extends MongoService<HubParkourCourse> {
	private final static Map<UUID, HubParkourCourse> cache = new ConcurrentHashMap<>();

	public Map<UUID, HubParkourCourse> getCache() {
		return cache;
	}

}
