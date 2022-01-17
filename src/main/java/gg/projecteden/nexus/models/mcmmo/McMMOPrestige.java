package gg.projecteden.nexus.models.mcmmo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class McMMOPrestige {
	private String uuid;
	private Map<String, Integer> prestiges;

	public int getPrestige(String type) {
		return prestiges.getOrDefault(type.toLowerCase(), 0);
	}

	public void setPrestige(String type, int prestige) {
		prestiges.put(type.toLowerCase(), prestige);
	}

	public void prestige(String type) {
		setPrestige(type, getPrestige(type) + 1);
	}
}
