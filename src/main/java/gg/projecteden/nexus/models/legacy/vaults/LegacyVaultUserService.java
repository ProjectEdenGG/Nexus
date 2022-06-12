package gg.projecteden.nexus.models.legacy.vaults;


import gg.projecteden.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.player.MongoPlayerService;
import gg.projecteden.nexus.utils.Nullables;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(LegacyVaultUser.class)
public class LegacyVaultUserService extends MongoPlayerService<LegacyVaultUser> {
	private final static Map<UUID, LegacyVaultUser> cache = new ConcurrentHashMap<>();

	public Map<UUID, LegacyVaultUser> getCache() {
		return cache;
	}

	@Override
	protected void beforeSave(LegacyVaultUser user) {
		user.getVaults().forEach((page, contents) -> {
			if (contents.stream().noneMatch(Nullables::isNotNullOrAir))
				user.getVaults().remove(page);
		});
	}

}
