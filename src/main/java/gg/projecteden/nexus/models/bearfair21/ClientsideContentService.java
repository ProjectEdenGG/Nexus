package gg.projecteden.nexus.models.bearfair21;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.models.bearfair21.ClientsideContent.Content;
import gg.projecteden.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(ClientsideContent.class)
public class ClientsideContentService extends MongoPlayerService<ClientsideContent> {
	private final static Map<UUID, ClientsideContent> cache = new ConcurrentHashMap<>();

	public Map<UUID, ClientsideContent> getCache() {
		return cache;
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
