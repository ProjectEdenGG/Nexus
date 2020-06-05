package me.pugabyte.bncore.features.menus.rewardchests;

import lombok.Data;
import me.pugabyte.bncore.utils.SerializationUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("RewardChestLoot")
public class RewardChestLoot implements ConfigurationSerializable {

	public String title;
	public ItemStack[] items = new ItemStack[1];
	public boolean active = true;

	public RewardChestLoot(String title, ItemStack... items) {
		this.title = title;
		this.items = items;
	}

	public RewardChestLoot(String title, boolean active, ItemStack... items) {
		this.title = title;
		this.active = active;
		this.items = items;
	}

	public RewardChestLoot(Map<String, Object> map) {
		this.title = (String) map.getOrDefault("title", "Mystery Chest");
		this.items = SerializationUtils.YML.deserializeItems((Map<String, Object>) map.getOrDefault("items", items));
		this.active = (boolean) map.getOrDefault("active", active);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("title", title);
			put("items", SerializationUtils.YML.serializeItems(items));
			put("active", true);
		}};
	}
}
