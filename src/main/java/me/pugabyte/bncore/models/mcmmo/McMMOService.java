package me.pugabyte.bncore.models.mcmmo;

import me.pugabyte.bncore.models.BaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class McMMOService extends BaseService {
	public McMMOPrestige getPrestige(String uuid) {
		List<HashMap> data = database.sql("select type, count from mcmmo_prestige where uuid = ?", uuid).results(HashMap.class);

		Map<String, Integer> prestiges = new HashMap<>();
		for (HashMap<String, Object> row : data)
			prestiges.put((String) row.get("type"), (Integer) row.get("count"));

		return new McMMOPrestige(uuid, prestiges);
	}
}
