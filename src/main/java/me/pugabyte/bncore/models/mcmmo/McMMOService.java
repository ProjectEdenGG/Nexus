package me.pugabyte.bncore.models.mcmmo;

import me.pugabyte.bncore.models.BaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McMMOService extends BaseService {
	public McMMOPrestige getPrestige(String uuid) {
		Map<String, Integer> prestiges = new HashMap<>();
		List<HashMap> data = database.sql("select type, count from mcmmo_prestige where uuid = ?", uuid).results(HashMap.class);

		for (HashMap<String, Object> row : data) {
			String type = "";
			Integer count = 0;
			for (Map.Entry<String, Object> entry : row.entrySet()) {
				String column = entry.getKey();
				if ("type".equalsIgnoreCase(column))
					type = (String) entry.getValue();
				else if ("count".equalsIgnoreCase(column))
					count = (Integer) entry.getValue();
			}
			prestiges.put(type, count);
		}

		return new McMMOPrestige(uuid, prestiges);
	}
}
