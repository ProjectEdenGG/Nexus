package me.pugabyte.bncore.features.menus.rewardchests;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.SerializationUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@SerializableAs("RewardChestLoot")
public class RewardChestLoot implements ConfigurationSerializable {

	public int id;
	public String title = "Reward Chest Loot";
	public ItemStack[] items = new ItemStack[0];
	public boolean active = true;
	public RewardChestType type = RewardChestType.ALL;

	public RewardChestLoot(RewardChestType type) {
		this.type = type;
	}

	public RewardChestLoot(String title, ItemStack... items) {
		this.title = title;
		this.items = items;
	}

	public RewardChestLoot(Map<String, Object> map) {
		this.title = (String) map.getOrDefault("title", title);
		this.items = Arrays.stream(SerializationUtils.YML.deserializeItems((Map<String, Object>) map.getOrDefault("items", items)))
				.filter(itemStack -> !ItemUtils.isNullOrAir(itemStack)).collect(Collectors.toList()).toArray(new ItemStack[0]);
		this.active = (boolean) map.getOrDefault("active", active);
		this.type = RewardChestType.valueOf((String) map.getOrDefault("type", type.name()));
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("title", title);
			put("items", SerializationUtils.YML.serializeItems(items));
			put("active", active);
			put("type", type.name());
		}};
	}


}
