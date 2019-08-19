package me.pugabyte.bncore.models.nerds;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.BaseService;

public class NerdService extends BaseService {
	@Override
	public Nerd get(String uuid) {
		return database.where("uuid = ?", uuid).first(Nerd.class);
	}

	public void save(Nerd nerd) {
		BNCore.async(() -> database.upsert(nerd).execute());
	}

}
