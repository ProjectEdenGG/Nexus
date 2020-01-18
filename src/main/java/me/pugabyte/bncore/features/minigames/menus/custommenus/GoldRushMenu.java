package me.pugabyte.bncore.features.minigames.menus.custommenus;

import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.GoldRushArena;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.entity.Player;

@CustomMechanicSettings(MechanicType.GOLD_RUSH)
public class GoldRushMenu extends MenuUtils implements InventoryProvider {

	GoldRushArena arena;
	public GoldRushMenu(Arena arena){
		this.arena = (GoldRushArena) arena;
	}

	@Override
	public void init(Player player, InventoryContents inventoryContents) {

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
