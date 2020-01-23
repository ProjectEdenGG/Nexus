package me.pugabyte.bncore.models.nerds;

import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

public class NerdService extends BaseService {
	@Override
	public <T> T get(String uuid) {
		Player player = (Player) Utils.getPlayer(uuid);
		Nerd nerd = database.where("uuid = ?", uuid).first(Nerd.class);
		nerd.fromPlayer(player);
		return (T) nerd;
	}

	public Nerd find(String partialName) {
		return database
				.select("nerd.*")
				.table("nerd")
					.innerJoin("hours")
					.on("hours.uuid = nerd.uuid")
				.where("name like ?")
				.orderBy("position(? in name), hours.total desc")
				.args("%" + partialName + "%", partialName)
				.first(Nerd.class);
	}

	public List<Nerd> getOnlineNerds() {
		List<Nerd> nerds = database.where("uuid in ?", Utils.getOnlineUuids()).results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

}
