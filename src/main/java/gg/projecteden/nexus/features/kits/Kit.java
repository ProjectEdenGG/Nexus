package gg.projecteden.nexus.features.kits;

import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SerializationUtils.YML;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@SerializableAs("Kit")
public class Kit implements ConfigurationSerializable {

	public int id;
	String name = "Kit";
	ItemStack[] items = new ItemStack[0];
	int delay = 0;

	public Kit(Map<String, Object> map) {
		this.name = (String) map.getOrDefault("name", name);
		this.items = Arrays.stream(YML.asArray(YML.deserializeItemStacks((Map<String, Object>) map.getOrDefault("items", new HashMap<>())))).filter(Nullables::isNotNullOrAir).toArray(ItemStack[]::new);
		this.delay = (int) map.getOrDefault("delay", delay);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("name", name);
			put("items", YML.serializeItemStacks(items));
			put("delay", delay);
		}};
	}

}
