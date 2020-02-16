package me.pugabyte.bncore.models.rule;

import me.pugabyte.bncore.models.MySQLService;

public class RuleService extends MySQLService {

	@Override
	public HasReadRules get(String uuid) {
		HasReadRules first = database.where("uuid = ?", uuid).first(HasReadRules.class);
		if (first.getUuid() == null)
			first.setUuid(uuid);
		return first;
	}

}
