package me.pugabyte.bncore.features.minigames.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.utils.YamlSerializationUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("Loadout")
public class Loadout implements ConfigurationSerializable {
	private ItemStack[] inventory = new ItemStack[41];
	private boolean isLoadoutEmpty;
	private List<PotionEffect> effects = new ArrayList<>();

	public Loadout(Map<String, Object> map) {
		this.inventory = YamlSerializationUtils.deserializeItems((Map<String, Object>) map.getOrDefault("inventory", inventory));
		this.effects = (List<PotionEffect>) map.getOrDefault("effects", effects);
		isLoadoutEmpty = Arrays.stream(inventory).noneMatch(Objects::nonNull);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("inventory", YamlSerializationUtils.serializeItems(inventory));
			put("effects", effects);
		}};
	}

	public void apply(Minigamer minigamer) {
		minigamer.clearState();
		Player player = minigamer.getPlayer();
		if (!isLoadoutEmpty)
			player.getInventory().setContents(inventory.clone());
		if (effects != null && effects.size() > 0)
			player.addPotionEffects(effects);
	}


}
