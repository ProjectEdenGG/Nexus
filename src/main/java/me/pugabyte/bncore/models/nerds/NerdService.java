package me.pugabyte.bncore.models.nerds;

import me.pugabyte.bncore.Utils;
import me.pugabyte.bncore.models.BaseService;
import org.bukkit.entity.Player;

public class NerdService extends BaseService {
	@Override
	public Nerd get(String uuid) {
		Player player = (Player) Utils.getPlayer(uuid);
		Nerd nerd = database.where("uuid = ?", uuid).first(Nerd.class);
		if (nerd == null)
			nerd = new Nerd();
		nerd.fromPlayer(player);
		return nerd;
	}

	public Nerd find(String partialName) {
		return database.where("name like ?", "%" + partialName + "%").first(Nerd.class);
	}

	public void save(Nerd nerd) {
		Utils.async(() -> database.upsert(nerd).execute());
	}

}
