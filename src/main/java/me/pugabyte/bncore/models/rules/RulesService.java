package me.pugabyte.bncore.models.rules;

import me.pugabyte.bncore.models.MySQLService;

public class RulesService extends MySQLService {

	@Override
	public HasReadRules get(String uuid) {
		HasReadRules first = database.where("uuid = ?", uuid).first(HasReadRules.class);
		if (first.getUuid() == null)
			first.setUuid(uuid);
		return first;
	}

}
