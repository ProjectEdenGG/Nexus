package me.pugabyte.bncore.models.mcmmo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class McMMOPrestige {
	private String uuid;
	private Map<String, Integer> prestiges;

	public int getPrestige(String type) {
		if (prestiges.containsKey(type))
			return prestiges.get(type);

		return 0;
	}
}
