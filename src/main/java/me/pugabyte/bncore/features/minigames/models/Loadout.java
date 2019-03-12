package me.pugabyte.bncore.features.minigames.models;

import lombok.Builder;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Builder
@Data
public class Loadout {
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


}
