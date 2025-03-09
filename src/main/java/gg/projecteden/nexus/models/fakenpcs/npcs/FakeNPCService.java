package gg.projecteden.nexus.models.fakenpcs.npcs;

import gg.projecteden.api.mongodb.annotations.ObjectClass;
import gg.projecteden.nexus.framework.persistence.mongodb.MongoBukkitService;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUser;
import gg.projecteden.nexus.models.fakenpcs.users.FakeNPCUserService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ObjectClass(FakeNPC.class)
public class FakeNPCService extends MongoBukkitService<FakeNPC> {
	private final static Map<UUID, FakeNPC> cache = new ConcurrentHashMap<>();

	public Map<UUID, FakeNPC> getCache() {
		return cache;
	}

	@Override
	protected void beforeDelete(FakeNPC npc) {
		npc.despawn();
		final FakeNPCUserService userService = new FakeNPCUserService();
		for (FakeNPCUser user : userService.getAll()) {
			user.hide(npc);

			if (user.getSelected().equals(npc.getUuid()))
				user.setSelected(null);
		}

		userService.saveCacheSync();
	}

}
