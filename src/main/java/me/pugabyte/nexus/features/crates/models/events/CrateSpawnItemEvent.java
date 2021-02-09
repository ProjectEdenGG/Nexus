package me.pugabyte.nexus.features.crates.models.events;

import me.pugabyte.nexus.features.crates.models.CrateLoot;
import org.bukkit.entity.Player;

public class CrateSpawnItemEvent extends CrateEvent {

	public CrateSpawnItemEvent(Player player, CrateLoot crateLoot) {
		super(player, crateLoot);
	}
}
