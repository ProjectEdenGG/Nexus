package gg.projecteden.nexus.features.crates.crates;

import gg.projecteden.nexus.features.crates.models.Crate;
import gg.projecteden.nexus.features.crates.models.CrateLoot;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.crates.models.events.CrateSpawnItemEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class BossCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.BOSS;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&f&l--=[+]=--");
			add("&f&k[+] " + Rank.OWNER.getChatColor() + "&lBoss Crate &f&k[+]");
			add("&f&l--=[+]=--");
		}};
	}

	@EventHandler
	public void onItemSpawn(CrateSpawnItemEvent event) {
		CrateLoot loot = event.getCrateLoot();
		if (getCrateType().equals(event.getCrateType()) && loot.getWeight() <= 2) {
			broadcastLoot(event.getPlayer(), loot);
		}
	}
}
