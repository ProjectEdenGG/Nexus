package me.pugabyte.bncore.models.rules;

import me.pugabyte.bncore.models.BaseService;

public class RulesService extends BaseService {

	@Override
	public HasReadRules get(String uuid) {
		HasReadRules first = database.where("uuid = ?", uuid).first(HasReadRules.class);
		// TODO: Better way?
		if (first.getUuid() == null)
			first.setUuid(uuid);
		return first;
	}

}
