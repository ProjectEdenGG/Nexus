package gg.projecteden.nexus.features.resourcepack.decoration.virtualinventory.models;

import lombok.Getter;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class Properties implements Keyed {
	static final Map<NamespacedKey, Properties> KEY_MAP = new HashMap<>();
	@Getter
	final NamespacedKey key;

	Properties(NamespacedKey key) {
		this.key = key;
		KEY_MAP.put(key, this);
	}
}
