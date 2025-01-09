package gg.projecteden.nexus.features.minigames.models;

import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerLoadoutEvent;
import gg.projecteden.nexus.models.minigamersetting.MinigamerSetting;
import gg.projecteden.nexus.models.minigamersetting.MinigamerSettingService;
import gg.projecteden.nexus.utils.SerializationUtils.YML;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gg.projecteden.nexus.utils.PlayerUtils.hidePlayer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("Loadout")
public class Loadout implements ConfigurationSerializable {
	private ItemStack[] inventory = new ItemStack[41];
	private boolean isLoadoutEmpty;
	private List<PotionEffect> effects = new ArrayList<>();

	public Loadout(Map<String, Object> map) {
		this.inventory = YML.asInventory(YML.deserializeItemStacks((Map<String, Object>) map.getOrDefault("inventory", new HashMap<>())));
		this.effects = (List<PotionEffect>) map.getOrDefault("effects", effects);
		isLoadoutEmpty = Arrays.stream(this.inventory).noneMatch(Objects::nonNull);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("inventory", YML.serializeItemStacks(inventory));
			put("effects", effects);
		}};
	}

	public void apply(Minigamer minigamer) {
		minigamer.clearState();
		Player player = minigamer.getOnlinePlayer();

		minigamer.getMatch().getSpectators().forEach(spectator -> hidePlayer(spectator.getOnlinePlayer()).from(minigamer));

		if (!isLoadoutEmpty) {
			PlayerInventory inventory = player.getInventory();
			inventory.setContents(this.inventory.clone());
			ItemStack offHand = inventory.getItemInOffHand().clone();
			if (offHand.getType() == Material.BOW || offHand.getType() == Material.CROSSBOW) {
				MinigamerSettingService service = new MinigamerSettingService();
				MinigamerSetting settings = service.get(player);
				if (!settings.isBowInOffHand()) {
					inventory.setItemInOffHand(new ItemStack(Material.AIR));
					inventory.addItem(offHand);
				}
			}
		}

		if (effects != null && effects.size() > 0)
			player.addPotionEffects(effects);

		new MinigamerLoadoutEvent(minigamer, this).callEvent();
	}

}
