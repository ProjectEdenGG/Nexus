package me.pugabyte.bncore.models.mcmmo;

import me.pugabyte.bncore.models.MySQLService;
import me.pugabyte.bncore.utils.Tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class McMMOService extends MySQLService {
	public McMMOPrestige getPrestige(String uuid) {
		List<HashMap> data = database.sql("select type, count from mcmmo_prestige where uuid = ?", uuid).results(HashMap.class);

		Map<String, Integer> prestiges = new HashMap<>();
		for (HashMap<String, Object> row : data)
			prestiges.put((String) row.get("type"), (Integer) row.get("count"));

		return new McMMOPrestige(uuid, prestiges);
	}

	public void save(McMMOPrestige prestiges) {
		Tasks.async(() -> prestiges.getPrestiges().forEach((type, count) -> database
				.sql("INSERT INTO mcmmo_prestige VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE count=VALUES(count)")
				.args(prestiges.getUuid(), type, count)
				.execute()));
	}
}
