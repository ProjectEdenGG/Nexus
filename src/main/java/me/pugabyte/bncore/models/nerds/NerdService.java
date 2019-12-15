package me.pugabyte.bncore.models.nerds;

import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.models.BaseService;
import org.bukkit.entity.Player;

import java.util.List;

public class NerdService extends BaseService {
	@Override
	public Nerd get(String uuid) {
		Player player = (Player) Utils.getPlayer(uuid);
		Nerd nerd = database.where("uuid = ?", uuid).first(Nerd.class);
		nerd.fromPlayer(player);
		return nerd;
	}

	public Nerd find(String partialName) {
		return database.sql(
				"select nerd.* from nerd inner join hours on hours.uuid = nerd.uuid where name like ? order by position(? in name), hours.total desc",
				"%" + partialName + "%", partialName
		).first(Nerd.class);
	}

	public List<Nerd> getOnlineNerds() {
		List<Nerd> nerds = database.where("uuid in ?", Utils.getOnlineUuids()).results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

	public void save(Nerd nerd) {
		Utils.async(() -> database.upsert(nerd).execute());
	}

}
