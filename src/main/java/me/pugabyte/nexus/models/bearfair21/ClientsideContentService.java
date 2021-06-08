package me.pugabyte.nexus.models.bearfair21;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ClientsideContent.class)
public class ClientsideContentService extends MongoService<ClientsideContent> {
	private final static Map<UUID, ClientsideContent> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, ClientsideContent> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public List<Content> getList() {
		return get0().getContentList();
	}

	public List<Content> getList(ContentCategory category) {
		List<Content> result = new ArrayList<>();
		for (Content content : getList()) {
			if (content.getCategory().equals(category))
				result.add(content);
		}

		return result;
	}
}
