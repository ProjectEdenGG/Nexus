package me.pugabyte.bncore.models.nerds;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.models.persistence.BearNationDatabase;
import me.pugabyte.bncore.models.persistence.Persistence;

public class NerdService {
	Database database = Persistence.getConnection(BearNationDatabase.BEARNATION);

	public Nerd get(String uuid) throws Exception {
		Nerd nerd = database.where("uuid = ?", uuid).first(Nerd.class);
		if (nerd == null) {
			throw new Exception("Nerd not found");
		}

		return nerd;
	}

}
