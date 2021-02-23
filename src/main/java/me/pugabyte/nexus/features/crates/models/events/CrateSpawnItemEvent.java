package me.pugabyte.nexus.features.crates.models.events;

import me.pugabyte.nexus.features.crates.models.CrateLoot;
import me.pugabyte.nexus.features.crates.models.CrateType;
import org.bukkit.entity.Player;

public class CrateSpawnItemEvent extends CrateEvent {

	public CrateSpawnItemEvent(Player player, CrateLoot crateLoot, CrateType type) {
		super(player, crateLoot, type);
	}
}
