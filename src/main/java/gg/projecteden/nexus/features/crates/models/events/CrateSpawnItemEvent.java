package gg.projecteden.nexus.features.crates.models.events;

import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import org.bukkit.entity.Player;

public class CrateSpawnItemEvent extends CrateEvent {

	public CrateSpawnItemEvent(Player player, CrateLoot crateLoot, CrateType type) {
		super(player, crateLoot, type);
	}
}
