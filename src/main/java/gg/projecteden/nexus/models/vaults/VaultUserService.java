package gg.projecteden.nexus.models.vaults;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.utils.Nullables;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(VaultUser.class)
public class VaultUserService extends MongoPlayerService<VaultUser> {
	private final static Map<UUID, VaultUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, VaultUser> getCache() {
		return cache;
	}

	@Override
	protected void beforeSave(VaultUser user) {
		user.getVaults().forEach((page, contents) -> {
			if (contents.stream().noneMatch(Nullables::isNotNullOrAir))
				user.getVaults().remove(page);
		});
	}

}
