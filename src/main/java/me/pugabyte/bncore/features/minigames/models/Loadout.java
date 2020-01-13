package me.pugabyte.bncore.features.minigames.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.utils.SerializationUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("Loadout")
public class Loadout implements ConfigurationSerializable {
	private ItemStack[] inventory = new ItemStack[41];
	private List<PotionEffect> effects = new ArrayList<>();

	public Loadout(Map<String, Object> map) {
		this.inventory = SerializationUtils.deserializeItems((Map<String, Object>) map.getOrDefault("inventory", inventory));
		this.effects = (List<PotionEffect>) map.getOrDefault("effects", effects);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("inventory", SerializationUtils.serializeItems(inventory));
			put("effects", effects);
		}};
	}

	public void apply(Minigamer minigamer) {
		minigamer.clearState();
		Player player = minigamer.getPlayer();
		if (inventory != null)
			player.getInventory().setContents(inventory.clone());
		if (effects != null)
			player.addPotionEffects(effects);
	}


}
