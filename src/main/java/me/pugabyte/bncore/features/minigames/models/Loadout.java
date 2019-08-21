package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@SerializableAs("Loadout")
public class Loadout implements ConfigurationSerializable {
	private ItemStack[] inventoryContents;
	private List<PotionEffect> potionEffects;

	public void apply(Minigamer minigamer) {
		Player player = minigamer.getPlayer();
		minigamer.clearState();
		player.setGameMode(minigamer.getMatch().getArena().getMechanic().getGameMode());
		if (inventoryContents != null) {
			player.getInventory().setContents(inventoryContents);
		}
		if (potionEffects != null) {
			player.addPotionEffects(potionEffects);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		if (getInventoryContents() != null)
			map.put("inventory", serializeItems());
		if (getPotionEffects() != null)
			map.put("effects", getPotionEffects());

		return map;
	}

	private Map<String, ItemStack> serializeItems() {
		Map<String, ItemStack> items = new HashMap<>();
		int slot = 0;
		for (ItemStack item : getInventoryContents()) {
			if (item != null)
				items.put(String.valueOf(slot), item);
			slot++;
		}

		return items;
	}

	public static Loadout deserialize(Map<String, Object> map) {
		return Loadout.builder()
				.inventoryContents(deserializeItems((Map<String, Object>) map.get("inventory")))
				.potionEffects((List<PotionEffect>) map.get("effects"))
				.build();
	}

	private static ItemStack[] deserializeItems(Map<String, Object> items) {
		ItemStack[] inventory = new ItemStack[41];
		for (Map.Entry<String, Object> item : items.entrySet()) {
			inventory[Integer.parseInt(item.getKey())] = (ItemStack) item.getValue();
		}

		return inventory;
	}

}
