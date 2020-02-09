package me.pugabyte.bncore.models.nerds;

import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

public class NerdService extends BaseService {
	@Override
	public Nerd get(String uuid) {
		Nerd nerd = database.where("uuid = ?", uuid).first(Nerd.class);
		nerd.fromPlayer(Utils.getPlayer(uuid));
		return nerd;
	}

	public Nerd find(String partialName) {
		return database
				.select("nerd.*")
				.table("nerd")
				.leftJoin("hours")
					.on("hours.uuid = nerd.uuid")
				.where("name like ?")
				.orderBy("position(? in name), hours.total desc")
				.args("%" + partialName + "%", partialName)
				.first(Nerd.class);
	}

	public List<Nerd> getOnlineNerds() {
		List<Nerd> nerds = database.where("uuid in ?", asList(Utils.getOnlineUuids())).results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

	public List<Nerd> getOnlineNerdsWith(String permission) {
		List<String> filtered = Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.hasPermission(permission))
				.map(player -> player.getUniqueId().toString())
				.collect(Collectors.toList());

		List<Nerd> nerds = database.where("uuid in (" + asList(filtered) + ")").results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

}
