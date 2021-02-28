package me.pugabyte.nexus.models.nerd;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Nerd.class)
public class NerdService extends MongoService {
	private final static Map<UUID, Nerd> cache = new HashMap<>();

	public Map<UUID, Nerd> getCache() {
		return cache;
	}

	@Override
	protected <T extends PlayerOwnedObject> T getNoCache(UUID uuid) {
		Nerd nerd = super.getNoCache(uuid);
		nerd.fromPlayer(PlayerUtils.getPlayer(uuid));
		return (T) nerd;
	}

	public List<Nerd> find(String partialName) {
	/*
		List<Nerd> nerds = database.sql(
				"select nerd.* " +
				"from name_history " +
				"inner join nerd " +
					"on name_history.uuid = nerd.uuid " +
				"left join hours " +
					"on hours.uuid = nerd.uuid " +
				"where name_history.name like ? " +
				"group by nerd.uuid " +
				"order by hours.total desc, position(? in name_history.name) " +
				"limit 50")
				.args("%" + partialName.replaceAll("_", "\\\\_") + "%", partialName)
				.results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(PlayerUtils.getPlayer(nerd.getUuid()));
	*/
		return null;
	}

	public List<Nerd> getNerdsWithBirthdays() {
		return null;
	}

	public Nerd getFromNickname(String nickname) {
		return null;
	}

}
